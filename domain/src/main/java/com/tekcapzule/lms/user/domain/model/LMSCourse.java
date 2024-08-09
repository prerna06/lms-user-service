package com.tekcapzule.lms.user.domain.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBDocument
public class LMSCourse {
    private String courseId;
    private int watchedDuration;
    private String status;
    private int lastVisitedModule;
    private int lastVisitedChapter;
    @DynamoDBAttribute(attributeName="modules")
    private List<Module> modules;
    private int assessmentScore;
    private String assessmentStatus;
}

