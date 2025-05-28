package org.sleepless_artery.course_service.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.course_service.service.CourseService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final CourseService courseService;

    @KafkaListener(topics = "user.profiles.deleted", groupId = "course-service")
    public void listenUserDeletedEvent(@Header(KafkaHeaders.RECEIVED_KEY) String key) {
        courseService.deleteCoursesByAuthorId(Long.parseLong(key));
    }

    @KafkaListener(topics = "lesson.course.updated", groupId = "course-service")
    public void listen(Long courseId) {
        courseService.updateCourse(courseId);
    }
}
