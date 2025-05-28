package org.sleepless_artery.course_service.grpc.server;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.sleepless_artery.course_service.CourseVerificationServiceGrpc;
import org.sleepless_artery.course_service.VerifyCourseExistenceRequest;
import org.sleepless_artery.course_service.VerifyCourseExistenceResponse;
import org.sleepless_artery.course_service.service.CourseService;


@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CourseVerificationServiceImpl extends CourseVerificationServiceGrpc.CourseVerificationServiceImplBase {

    private final CourseService courseService;

    @Override
    public void verifyCourseExistence(
            VerifyCourseExistenceRequest request, StreamObserver<VerifyCourseExistenceResponse> streamObserver
    ) {
        try {
            streamObserver.onNext(
                    VerifyCourseExistenceResponse.newBuilder()
                            .setExistence(courseService.existsById(request.getId()))
                            .build()
            );
            streamObserver.onCompleted();
        } catch (Exception e) {
            log.error("An error occurred on the grpc server side while verifying course existence: {}", e.getMessage());

            streamObserver.onError(Status.INTERNAL
                    .withDescription("Error verifying course existence")
                    .asRuntimeException());
        }
    }
}
