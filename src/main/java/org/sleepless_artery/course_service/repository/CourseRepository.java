package org.sleepless_artery.course_service.repository;

import org.sleepless_artery.course_service.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    boolean existsByAuthorIdAndTitleIgnoreCase(Long authorId, String title);

    List<Course> findByAuthorId(Long authorId);
}
