package org.sleepless_artery.course_service.exception;

public class GrpcProcessingException extends RuntimeException {
    public GrpcProcessingException(String message) {
        super(message);
    }
}
