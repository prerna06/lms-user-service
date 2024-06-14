package com.tekcapzule.lms.user.domain.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tekcapzule.core.domain.Command;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class UpdateUserProgressCommand extends Command {
    private String userId;
    private String tenantId;
    private String courseId;
    private String moduleId;
    private String chapterId;
    private double progressPercentage;
    private String lastAccessed;
    private double watchedDuration;
}
