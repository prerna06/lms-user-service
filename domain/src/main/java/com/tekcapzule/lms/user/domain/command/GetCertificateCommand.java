package com.tekcapzule.lms.user.domain.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tekcapzule.core.domain.Command;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class GetCertificateCommand extends Command {
    private String userId;
    private String firstName;
    private String lastName;
    private String courseId;
    private String courseName;
    private String courseInstructor;
    private String courseDuration;
    private String certificateType;
}
