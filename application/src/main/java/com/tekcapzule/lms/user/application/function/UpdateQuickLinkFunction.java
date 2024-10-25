package com.tekcapzule.lms.user.application.function;

import com.tekcapzule.core.domain.Origin;
import com.tekcapzule.core.utils.HeaderUtil;
import com.tekcapzule.core.utils.Outcome;
import com.tekcapzule.core.utils.Stage;
import com.tekcapzule.lms.user.application.config.AppConfig;
import com.tekcapzule.lms.user.application.function.input.CreateQuickLinkInput;
import com.tekcapzule.lms.user.application.mapper.InputOutputMapper;
import com.tekcapzule.lms.user.domain.command.CreateQuickLinkCommand;
import com.tekcapzule.lms.user.domain.model.QuickLink;
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
public class UpdateQuickLinkFunction implements Function<Message<CreateQuickLinkInput>, Message<List<QuickLink>>> {

    private final UserService userService;

    private final AppConfig appConfig;

    public UpdateQuickLinkFunction(final UserService userService, final AppConfig appConfig) {
        this.userService = userService;
        this.appConfig = appConfig;
    }


    @Override
    public Message<List<QuickLink>> apply(Message<CreateQuickLinkInput> createQuickLinkInputMessage) {
        Map<String, Object> responseHeaders = new HashMap<>();
        Map<String, Object> payload = new HashMap<>();
        List<QuickLink> quickLinks = new ArrayList<>();
        String stage = appConfig.getStage().toUpperCase();
        try {
            CreateQuickLinkInput createQuickLinkInput = createQuickLinkInputMessage.getPayload();
            log.info(String.format("Entering get user Function -  User Id:%s", createQuickLinkInput.getUserId()));
            CreateQuickLinkCommand createQuickLinkCommand = InputOutputMapper.buildCreateQuickLinkCommand.apply(createQuickLinkInput, Origin.builder().build());
            quickLinks = userService.updateQuickLink(createQuickLinkCommand);
            Map<String, Object> responseHeader = new HashMap();
            if (quickLinks == null) {
                responseHeaders = HeaderUtil.populateResponseHeaders(responseHeaders, Stage.valueOf(stage), Outcome.NOT_FOUND);
            } else {
                responseHeaders = HeaderUtil.populateResponseHeaders(responseHeaders, Stage.valueOf(stage), Outcome.SUCCESS);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            responseHeaders = HeaderUtil.populateResponseHeaders(responseHeaders, Stage.valueOf(stage), Outcome.ERROR);
        }
        return new GenericMessage(quickLinks, responseHeaders);
    }
}