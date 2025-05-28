package org.sleepless_artery.course_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.sleepless_artery.course_service.dto.CourseRequestDto;
import org.sleepless_artery.course_service.dto.CourseResponseDto;
import org.sleepless_artery.course_service.service.CourseService;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@Validated
@RefreshScope
@RestController
@RequestMapping("courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;


    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDto> findById(@PathVariable @Positive final Long id) {
        return ResponseEntity.ok(courseService.findById(id));
    }


    @GetMapping
    public ResponseEntity<Page<CourseResponseDto>> getCourses(
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) LocalDate startingDate,
            @RequestParam(required = false) LocalDate endingDate,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(
                courseService.getCourses(title, authorId, description, startingDate, endingDate, pageable)
        );
    }


    @PostMapping
    public ResponseEntity<CourseResponseDto> createCourse(
            @Valid @RequestBody final CourseRequestDto courseRequestDto
    ) {
        return ResponseEntity.ok(courseService.createCourse(courseRequestDto));
    }


    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDto> updateCourse(
            @PathVariable final Long id,
            @Valid @RequestBody final CourseRequestDto courseRequestDto
    ) {
        return ResponseEntity.ok(courseService.updateCourse(id, courseRequestDto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable final Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/author/{authorId}")
    public ResponseEntity<Void> deleteCoursesByAuthor(@PathVariable final Long authorId) {
        courseService.deleteCoursesByAuthorId(authorId);
        return ResponseEntity.noContent().build();
    }
}
