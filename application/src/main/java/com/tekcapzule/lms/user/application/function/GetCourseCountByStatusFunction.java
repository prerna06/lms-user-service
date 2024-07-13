package com.tekcapzule.lms.user.application.function;

import com.tekcapzule.core.utils.HeaderUtil;
import com.tekcapzule.core.utils.Outcome;
import com.tekcapzule.core.utils.Stage;
import com.tekcapzule.lms.user.application.config.AppConfig;
import com.tekcapzule.lms.user.application.function.input.GetCourseByStatusInput;
import com.tekcapzule.lms.user.domain.model.EnrollmentStatus;
import com.tekcapzule.lms.user.domain.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class GetCourseCountByStatusFunction implements Function<Message<GetCourseByStatusInput>, Message<Map<EnrollmentStatus, Long>>> {

    private final UserService userService;

    private final AppConfig appConfig;

    public GetCourseCountByStatusFunction(final UserService userService, final AppConfig appConfig) {
        this.userService = userService;
        this.appConfig = appConfig;
    }


    @Override
    public Message<Map<EnrollmentStatus, Long>> apply(Message<GetCourseByStatusInput> getInputMessage) {
        Map<String, Object> responseHeaders = new HashMap<>();
        Map<String, Object> payload = new HashMap<>();
        Map<EnrollmentStatus, Long> enrollmentCount = new HashMap<>();
        String stage = appConfig.getStage().toUpperCase();
        try {
            GetCourseByStatusInput getCourseByStatusInput = getInputMessage.getPayload();
            log.info(String.format("Entering get user Function -  User Id:%s", getCourseByStatusInput.getUserId()));
            enrollmentCount = userService.getCourseCountGroupByStatus(getCourseByStatusInput.getUserId(), getCourseByStatusInput.getTenantId());
            Map<String, Object> responseHeader = new HashMap();
            if (enrollmentCount == null) {
                responseHeaders = HeaderUtil.populateResponseHeaders(responseHeaders, Stage.valueOf(stage), Outcome.NOT_FOUND);
            } else {
                responseHeaders = HeaderUtil.populateResponseHeaders(responseHeaders, Stage.valueOf(stage), Outcome.SUCCESS);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            responseHeaders = HeaderUtil.populateResponseHeaders(responseHeaders, Stage.valueOf(stage), Outcome.ERROR);
        }
        return new GenericMessage(enrollmentCount, responseHeaders);
    }
}