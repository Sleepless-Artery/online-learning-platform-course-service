package org.sleepless_artery.course_service.repository.specifications;

import org.sleepless_artery.course_service.model.Course;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;


public class CourseSpecifications {

    public static Specification<Course> titleLike(String pattern) {
        return ((root, query, criteriaBuilder) ->
                pattern == null || pattern.isBlank()
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(root.get("title"), "%" + pattern + "%")
        );
    }


    public static Specification<Course> hasAuthorId(Long authorId) {
        return ((root, query, criteriaBuilder) ->
            authorId == null
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.equal(root.get("authorId"), authorId)
        );
    }


    public static Specification<Course> updatedAtBetween(LocalDate startingDate, LocalDate endingDate) {
        return ((root, query, criteriaBuilder) -> {
            if (startingDate == null && endingDate == null) {
                return criteriaBuilder.conjunction();
            }
            if (startingDate == null) {
                return criteriaBuilder.between(root.get("lastUpdateDate"), LocalDate.EPOCH, endingDate);
            }
            if (endingDate == null) {
                return criteriaBuilder.between(root.get("lastUpdateDate"), startingDate, LocalDate.now());
            }
            return criteriaBuilder.between(root.get("lastUpdateDate"), startingDate, endingDate);
        });
    }


    public static Specification<Course> descriptionLike(String pattern) {
        return ((root, query, criteriaBuilder) ->
            pattern == null || pattern.isBlank()
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.like(root.get("description"), "%" + pattern + "%")
        );
    }
}
