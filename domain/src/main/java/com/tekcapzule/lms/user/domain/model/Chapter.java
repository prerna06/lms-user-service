package com.tekcapzule.lms.user.domain.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBDocument
public class Chapter {
    private int serialNumber;
    private int duration;
    private String name;
    private String coverImageUrl;
    private String startDate;
    private String endDate;
    private int watchedDuration;
    @DynamoDBAttribute(attributeName = "chapterType")
    @DynamoDBTypeConvertedEnum
    private ChapterType chapterType;
    @DynamoDBAttribute(attributeName = "chapterStatus")
    @DynamoDBTypeConvertedEnum
    private ChapterStatus chapterStatus;
}
