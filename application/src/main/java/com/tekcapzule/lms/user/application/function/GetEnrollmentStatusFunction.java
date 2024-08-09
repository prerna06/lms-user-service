package com.tekcapzule.lms.user.application.function;

import com.tekcapzule.core.utils.HeaderUtil;
import com.tekcapzule.core.utils.Outcome;
import com.tekcapzule.core.utils.Stage;
import com.tekcapzule.lms.user.application.config.AppConfig;
import com.tekcapzule.lms.user.application.function.input.GetEnrollmentStatusByCourseIdInput;
import com.tekcapzule.lms.user.domain.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class GetEnrollmentStatusFunction implements Function<Message<GetEnrollmentStatusByCourseIdInput>, Message<String>> {

    private final UserService userService;

    private final AppConfig appConfig;

    public GetEnrollmentStatusFunction(final UserService userService, final AppConfig appConfig) {
        this.userService = userService;
        this.appConfig = appConfig;
    }


    @Override
    public Message<String> apply(Message<GetEnrollmentStatusByCourseIdInput> getInputMessage) {
        Map<String, Object> responseHeaders = new HashMap<>();
        String enrollmentStatus = null;
        String stage = appConfig.getStage().toUpperCase();
        try {
            GetEnrollmentStatusByCourseIdInput getEnrollmentStatusByCourseIdInput = getInputMessage.getPayload();
            log.info(String.format("Entering get enrollmentstatus Function -  User Id:%s", getEnrollmentStatusByCourseIdInput.getUserId()));
            enrollmentStatus = userService.getEnrollmentStatus(getEnrollmentStatusByCourseIdInput.getUserId(), getEnrollmentStatusByCourseIdInput.getTenantId(), getEnrollmentStatusByCourseIdInput.getCourseId());
            responseHeaders = HeaderUtil.populateResponseHeaders(responseHeaders, Stage.valueOf(stage), Outcome.SUCCESS);

        } catch (Exception ex) {
            log.error(ex.getMessage());
            responseHeaders = HeaderUtil.populateResponseHeaders(responseHeaders, Stage.valueOf(stage), Outcome.ERROR);
        }
        return new GenericMessage(enrollmentStatus, responseHeaders);
    }
}