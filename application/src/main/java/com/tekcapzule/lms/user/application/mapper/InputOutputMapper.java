package com.tekcapzule.lms.user.application.mapper;

import com.tekcapzule.core.domain.Command;
import com.tekcapzule.core.domain.ExecBy;
import com.tekcapzule.core.domain.Origin;
import com.tekcapzule.lms.user.application.function.input.*;
import com.tekcapzule.lms.user.domain.command.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.function.BiFunction;

@Slf4j
public final class InputOutputMapper {
    private InputOutputMapper() {

    }

    public static final BiFunction<Command, Origin, Command> addOrigin = (command, origin) -> {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        command.setChannel(origin.getChannel());
        command.setExecBy(ExecBy.builder().tenantId(origin.getTenantId()).userId(origin.getUserId()).build());
        command.setExecOn(utc.toString());
        return command;
    };

    public static final BiFunction<CreateInput, Origin, CreateCommand> buildCreateCommandFromCreateInput = (createInput, origin) -> {
        CreateCommand createCommand =  CreateCommand.builder().build();
        BeanUtils.copyProperties(createInput, createCommand);
        addOrigin.apply(createCommand, origin);
        return createCommand;
    };

    public static final BiFunction<UpdateInput, Origin, UpdateCommand> buildUpdateCommandFromUpdateInput = (updateInput, origin) -> {
        UpdateCommand updateCommand = UpdateCommand.builder().build();
        BeanUtils.copyProperties(updateInput, updateCommand);
        addOrigin.apply(updateCommand, origin);
        return updateCommand;
    };

    public static final BiFunction<UpdateUserProgressInput, Origin, UpdateUserProgressCommand> buildUpdateUserProgressCommandFromUpdateUserProgressInput = (updateInput, origin) -> {
        UpdateUserProgressCommand updateUserProgressCommand = UpdateUserProgressCommand.builder().build();
        BeanUtils.copyProperties(updateInput, updateUserProgressCommand);
        addOrigin.apply(updateUserProgressCommand, origin);
        return updateUserProgressCommand;
    };

    public static final BiFunction<DisableInput, Origin, DisableCommand> buildDisableCommandFromDisableInput = (disableInput, origin) -> {
        DisableCommand disableCommand =  DisableCommand.builder().build();
        BeanUtils.copyProperties(disableInput, disableCommand);
        addOrigin.apply(disableCommand, origin);
        return disableCommand;
    };

    public static final BiFunction<SubscribeTopicInput, Origin, SubscribeTopicCommand> buildFollowTopicCommandFromFollowTopicInput = (subscribeTopicInput, origin) -> {
        SubscribeTopicCommand subscribeTopicCommand =  SubscribeTopicCommand.builder().build();
        BeanUtils.copyProperties(subscribeTopicInput, subscribeTopicCommand);
        addOrigin.apply(subscribeTopicCommand, origin);
        return subscribeTopicCommand;
    };

    public static final BiFunction<UnsubscribeTopicInput, Origin, UnSubscribeTopicCommand> buildUnfollowTopicCommandFromUnfollowTopicInput = (unsubscribeTopicInput, origin) -> {
        UnSubscribeTopicCommand unSubscribeTopicCommand =  UnSubscribeTopicCommand.builder().build();
        BeanUtils.copyProperties(unsubscribeTopicInput, unSubscribeTopicCommand);
        addOrigin.apply(unSubscribeTopicCommand, origin);
        return unSubscribeTopicCommand;
    };

    public static final BiFunction<OptInCourseInput, Origin, OptInCourseCommand> buildRegisterCourseFromRegisterCourseInput = (optInCourseInput, origin) -> {
        OptInCourseCommand optInCourseCommand =  OptInCourseCommand.builder().build();
        BeanUtils.copyProperties(optInCourseInput, optInCourseCommand);
        addOrigin.apply(optInCourseCommand, origin);
        return optInCourseCommand;
    };

    public static final BiFunction<OptOutCourseInput, Origin, OptOutCourseCommand> buildDeregisterCourseCommandFromDeregisterCourseInput = (optOutCourseInput, origin) -> {
        OptOutCourseCommand removeBookmarkCommand =  OptOutCourseCommand.builder().build();
        BeanUtils.copyProperties(optOutCourseInput, removeBookmarkCommand);
        addOrigin.apply(removeBookmarkCommand, origin);
        return removeBookmarkCommand;
    };

    public static final BiFunction<GetCertificateInput, Origin, GetCertificateCommand> buildGetOrGenerateCertificateCommand = (getCertificateInput, origin) -> {
        GetCertificateCommand getCertificateCommand =  GetCertificateCommand.builder().build();
        BeanUtils.copyProperties(getCertificateInput, getCertificateCommand);
        addOrigin.apply(getCertificateCommand, origin);
        return getCertificateCommand;
    };

    public static final BiFunction<CompleteCourseInput, Origin, CompleteCourseCommand> buildCompleteCourseCommand = (completeCourseInput, origin) -> {
        CompleteCourseCommand completeCourseCommand =  CompleteCourseCommand.builder().build();
        BeanUtils.copyProperties(completeCourseInput, completeCourseCommand);
        addOrigin.apply(completeCourseCommand, origin);
        return completeCourseCommand;
    };

    public static final BiFunction<CreateQuickLinkInput, Origin, CreateQuickLinkCommand> buildCreateQuickLinkCommand = (createQuickLinkInput, origin) -> {
        CreateQuickLinkCommand createQuickLinkCommand =  CreateQuickLinkCommand.builder().build();
        BeanUtils.copyProperties(createQuickLinkInput, createQuickLinkCommand);
        addOrigin.apply(createQuickLinkCommand, origin);
        return createQuickLinkCommand;
    };


}
