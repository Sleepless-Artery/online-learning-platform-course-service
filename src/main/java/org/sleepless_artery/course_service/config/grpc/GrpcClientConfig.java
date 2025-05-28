package org.sleepless_artery.course_service.config.grpc;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.sleepless_artery.course_service.UserVerificationServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GrpcClientConfig {

    @GrpcClient("user-service")
    private UserVerificationServiceGrpc.UserVerificationServiceBlockingStub blockingStub;

    @Bean
    public UserVerificationServiceGrpc.UserVerificationServiceBlockingStub userVerificationServiceBlockingStub() {
        return blockingStub;
    }
}
