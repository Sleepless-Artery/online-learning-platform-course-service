package org.sleepless_artery.course_service.grpc.client;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sleepless_artery.course_service.VerifyUserExistenceRequest;
import org.sleepless_artery.course_service.config.grpc.GrpcClientConfig;
import org.sleepless_artery.course_service.exception.GrpcProcessingException;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserVerificationServiceGrpcClient {

    private final GrpcClientConfig grpcClientConfig;


    public boolean verifyUserExistence(Long userId) {
        log.info("Sending gRPC request to verify user existence");

        VerifyUserExistenceRequest request = VerifyUserExistenceRequest.newBuilder()
                .setId(userId)
                .build();

        try {
            return grpcClientConfig.userVerificationServiceBlockingStub()
                    .withDeadlineAfter(30, TimeUnit.SECONDS)
                    .verifyUserExistence(request)
                    .getExistence();
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                return false;
            }
            log.error("gRPC error: {}", e.getStatus());
            throw new GrpcProcessingException("User verification service unavailable");
        }
    }
}
