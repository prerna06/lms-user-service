package com.tekcapzule.lms.user.application.function;

import com.tekcapzule.core.domain.Origin;
import com.tekcapzule.core.utils.HeaderUtil;
import com.tekcapzule.core.utils.Outcome;
import com.tekcapzule.core.utils.PayloadUtil;
import com.tekcapzule.core.utils.Stage;
import com.tekcapzule.lms.user.application.config.AppConfig;
import com.tekcapzule.lms.user.application.function.input.CompleteCourseInput;
import com.tekcapzule.lms.user.application.function.input.GetCertificateInput;
import com.tekcapzule.lms.user.application.mapper.InputOutputMapper;
import com.tekcapzule.lms.user.domain.command.CompleteCourseCommand;
import com.tekcapzule.lms.user.domain.command.GetCertificateCommand;
import com.tekcapzule.lms.user.domain.service.PdfService;
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
public class CompleteCourseFunction implements Function<Message<CompleteCourseInput>, Message<byte[]>> {

    private final UserService userService;

    private final AppConfig appConfig;

    public CompleteCourseFunction(final UserService userService, final AppConfig appConfig) {
        this.userService = userService;
        this.appConfig = appConfig;
    }


    @Override
    public Message<byte[]> apply(Message<CompleteCourseInput> getInputMessage) {
        Map<String, Object> responseHeaders = new HashMap<>();
        Map<String, Object> payload = new HashMap<>();
        byte[] certificateByteData = null;
        String stage = appConfig.getStage().toUpperCase();
        try {
            CompleteCourseInput completeCourseInput = getInputMessage.getPayload();
            log.info(String.format("Entering get user Function -  User Id:%s", completeCourseInput.getUserId()));
            Origin origin = HeaderUtil.buildOriginFromHeaders(getInputMessage.getHeaders());
            CompleteCourseCommand completeCourseCommand = InputOutputMapper.buildCompleteCourseCommand.apply(completeCourseInput, origin);
            certificateByteData = userService.completeCourse(completeCourseCommand);
            responseHeaders = HeaderUtil.populateResponseHeaders(responseHeaders, Stage.valueOf(stage), Outcome.SUCCESS);
            responseHeaders.put("Content-Type", "application/pdf");
            payload = PayloadUtil.composePayload(Outcome.SUCCESS);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            responseHeaders = HeaderUtil.populateResponseHeaders(responseHeaders, Stage.valueOf(stage), Outcome.ERROR);
        }
        return new GenericMessage(certificateByteData, responseHeaders);
    }
}