package com.tekcapzule.lms.user.application.function;

import com.tekcapzule.core.domain.Origin;
import com.tekcapzule.core.utils.HeaderUtil;
import com.tekcapzule.core.utils.Outcome;
import com.tekcapzule.core.utils.PayloadUtil;
import com.tekcapzule.core.utils.Stage;
import com.tekcapzule.lms.user.application.config.AppConfig;
import com.tekcapzule.lms.user.application.function.input.GetCertificateInput;
import com.tekcapzule.lms.user.application.mapper.InputOutputMapper;
import com.tekcapzule.lms.user.domain.command.GetCertificateCommand;
import com.tekcapzule.lms.user.domain.model.LmsUser;
import com.tekcapzule.lms.user.domain.service.PdfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class GetCertificateFunction implements Function<Message<GetCertificateInput>, Message<byte[]>> {

    private final PdfService pdfService;

    private final AppConfig appConfig;

    public GetCertificateFunction(final PdfService pdfService, final AppConfig appConfig) {
        this.pdfService = pdfService;
        this.appConfig = appConfig;
    }


    @Override
    public Message<byte[]> apply(Message<GetCertificateInput> getInputMessage) {
        Map<String, Object> responseHeaders = new HashMap<>();
        Map<String, Object> payload = new HashMap<>();
        byte[] certificateByteData = null;
        String stage = appConfig.getStage().toUpperCase();
        try {
            GetCertificateInput getCertificateInput = getInputMessage.getPayload();
            log.info(String.format("Entering get user Function -  User Id:%s", getCertificateInput.getUserId()));
            Origin origin = HeaderUtil.buildOriginFromHeaders(getInputMessage.getHeaders());
            GetCertificateCommand getCertificateCommand = InputOutputMapper.buildGetOrGenerateCertificateCommand.apply(getCertificateInput, origin);
            certificateByteData = pdfService.generateCertificate(getCertificateCommand);
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