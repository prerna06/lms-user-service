package com.tekcapzule.lms.user.application.function.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class GetCertificateInput {
    private String userId;
    private String firstName;
    private String lastName;
    private String courseId;
    private String courseName;
    private String courseInstructor;
    private String courseDuration;
    private String certificateType;
}
