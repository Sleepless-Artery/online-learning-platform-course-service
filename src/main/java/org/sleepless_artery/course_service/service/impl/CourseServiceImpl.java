package org.sleepless_artery.course_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sleepless_artery.course_service.dto.CourseRequestDto;
import org.sleepless_artery.course_service.dto.CourseResponseDto;
import org.sleepless_artery.course_service.exception.AuthorDoesNotExistException;
import org.sleepless_artery.course_service.exception.CourseAlreadyExistsException;
import org.sleepless_artery.course_service.exception.CourseNotFoundException;
import org.sleepless_artery.course_service.grpc.client.UserVerificationServiceGrpcClient;
import org.sleepless_artery.course_service.kafka.producer.KafkaProducer;
import org.sleepless_artery.course_service.mapper.CourseMapper;
import org.sleepless_artery.course_service.model.Course;
import org.sleepless_artery.course_service.repository.CourseRepository;
import org.sleepless_artery.course_service.repository.specifications.CourseSpecifications;
import org.sleepless_artery.course_service.service.CourseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;


@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    private final UserVerificationServiceGrpcClient userVerificationServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    @Value("${spring.kafka.topic.prefix}")
    private String prefix;

    @Value("${spring.kafka.topic.domain}")
    private String domain;


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "courses", key = "#id")
    public CourseResponseDto findById(Long id) {
        return courseMapper.toCourseResponseDto(
                courseRepository.findById(id)
                        .orElseThrow(() -> {
                            log.warn("Cannot find course with id {}", id);
                            return new CourseNotFoundException();
                        })
        );
    }


    @Override
    public Page<CourseResponseDto> getCourses(
            String title, Long authorId, String description,
            LocalDate startingDate, LocalDate endingDate, Pageable pageable
    ) {
        return courseRepository.findAll(
                Specification.where(CourseSpecifications.titleLike(title))
                        .and(CourseSpecifications.hasAuthorId(authorId))
                        .and(CourseSpecifications.descriptionLike(description))
                        .and(CourseSpecifications.updatedAtBetween(startingDate, endingDate)),
                pageable
                ).map(courseMapper::toCourseResponseDto);
    }


    @Override
    @Transactional
    public CourseResponseDto createCourse(CourseRequestDto courseRequestDto) {
        log.info("Creating course with title '{}'", courseRequestDto.getTitle());

        Long authorId = courseRequestDto.getAuthorId();

        if (courseRepository.existsByAuthorIdAndTitleIgnoreCase(authorId, courseRequestDto.getTitle())) {
            log.warn("Author with id '{}' already has a course with title '{}'",
                    authorId, courseRequestDto.getTitle()
            );
            throw new CourseAlreadyExistsException();
        }

        if (!userVerificationServiceGrpcClient.verifyUserExistence(authorId)) {
            log.warn("User with id {} does not exist", authorId);
            throw new AuthorDoesNotExistException();
        }

        return courseMapper.toCourseResponseDto(
                courseRepository.save(courseMapper.toCourse(courseRequestDto))
        );
    }


    @Override
    @Transactional
    @CachePut(value = "courses", key = "#id")
    public CourseResponseDto updateCourse(Long id, CourseRequestDto courseRequestDto) {
        log.info("Updating course with id {}", id);

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot find course with id {}", id);
                    return new CourseNotFoundException();
                });

        if (!courseRequestDto.getTitle().equals(course.getTitle())) {
            if (courseRepository.existsByAuthorIdAndTitleIgnoreCase(
                    courseRequestDto.getAuthorId(), courseRequestDto.getTitle())
            ) {
                log.warn("Author with id '{}' already has a course with title '{}'",
                        courseRequestDto.getAuthorId(), courseRequestDto.getTitle()
                );
                throw new CourseAlreadyExistsException();
            }
            course.setTitle(courseRequestDto.getTitle());
        }
        if (!courseRequestDto.getDescription().equals(course.getDescription())) {
            course.setDescription(courseRequestDto.getDescription());
        }

        return courseMapper.toCourseResponseDto(courseRepository.save(course));
    }


    @Override
    @Transactional
    @CacheEvict(value = "courses", key = "#id")
    public void updateCourse(Long id) {
        log.info("Updating course with id {}", id);

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot find course with id {}", id);
                    return new CourseNotFoundException();
                });

        course.setLastUpdateDate(LocalDate.now());
        courseRepository.save(course);
    }


    @Override
    @Transactional
    @CacheEvict(value = "courses", key = "#id")
    public void deleteCourse(Long id) {
        log.info("Deleting course with id {}", id);
        courseRepository.deleteById(id);
        kafkaProducer.send(String.format("%s.%s.%s", prefix, domain, "deleted"), id);
    }


    @Override
    @Transactional
    public void deleteCoursesByAuthorId(Long authorId) {
        log.info("Deleting courses by author with id {}", authorId);
        courseRepository.findByAuthorId(authorId)
                .forEach(course -> deleteCourse(course.getId()));
    }


    @Override
    @Transactional(readOnly = true)
    public boolean existsById(long id) {
        log.info("Checking if course with id '{}' exists", id);
        return courseRepository.existsById(id);
    }
}
