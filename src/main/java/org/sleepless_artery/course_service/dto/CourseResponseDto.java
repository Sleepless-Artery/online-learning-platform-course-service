package org.sleepless_artery.course_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;


@Getter
@AllArgsConstructor
public class CourseResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private Long authorId;
    private LocalDate creationDate;
    private LocalDate lastUpdateDate;
    private String description;
}
