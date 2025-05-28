package org.sleepless_artery.course_service.service;

import org.sleepless_artery.course_service.dto.CourseRequestDto;
import org.sleepless_artery.course_service.dto.CourseResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;


public interface CourseService {

    CourseResponseDto findById(Long id);

    Page<CourseResponseDto> getCourses(
            String title, Long authorId, String description,
            LocalDate startingDate, LocalDate endingDate, Pageable pageable
    );

    CourseResponseDto createCourse(CourseRequestDto courseRequestDto);

    CourseResponseDto updateCourse(Long id, CourseRequestDto courseRequestDto);

    void updateCourse(Long id);

    void deleteCourse(Long id);

    void deleteCoursesByAuthorId(Long authorId);

    boolean existsById(long id);
}