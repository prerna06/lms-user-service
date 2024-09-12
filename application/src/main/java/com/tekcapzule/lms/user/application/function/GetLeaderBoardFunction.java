package com.tekcapzule.lms.user.application.function;

import com.tekcapzule.core.utils.HeaderUtil;
import com.tekcapzule.core.utils.Outcome;
import com.tekcapzule.core.utils.Stage;
import com.tekcapzule.lms.user.application.config.AppConfig;
import com.tekcapzule.lms.user.application.function.input.GetInput;
import com.tekcapzule.lms.user.domain.model.UserRank;
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
public class GetLeaderBoardFunction implements Function<Message<GetInput>, Message<List<UserRank
        >>> {

    private final UserService userService;

    private final AppConfig appConfig;

    public GetLeaderBoardFunction(final UserService userService, final AppConfig appConfig) {
        this.userService = userService;
        this.appConfig = appConfig;
    }


    @Override
    public Message<List<UserRank>> apply(Message<GetInput> getInputMessage) {
        Map<String, Object> responseHeaders = new HashMap<>();
        Map<String, Object> payload = new HashMap<>();
        List<UserRank> userRanks = new ArrayList<>();
        String stage = appConfig.getStage().toUpperCase();
        try {
            GetInput getInput = getInputMessage.getPayload();
            log.info(String.format("Entering getLeaderBoard", getInput.getUserId()));
            userRanks = userService.getLeaderBoard(getInput.getUserId(), getInput.getTenantId());
            Map<String, Object> responseHeader = new HashMap();
            if (userRanks == null) {
                responseHeaders = HeaderUtil.populateResponseHeaders(responseHeaders, Stage.valueOf(stage), Outcome.NOT_FOUND);
            } else {
                responseHeaders = HeaderUtil.populateResponseHeaders(responseHeaders, Stage.valueOf(stage), Outcome.SUCCESS);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            responseHeaders = HeaderUtil.populateResponseHeaders(responseHeaders, Stage.valueOf(stage), Outcome.ERROR);
        }
        return new GenericMessage(userRanks, responseHeaders);
    }
}