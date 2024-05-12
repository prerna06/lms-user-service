package com.tekcapzule.lms.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CourseStatus {
    OPTEDIN("OptedIn"),
    INPROGRESS("In Progress"),
    COMPLETED("Completed");

    @Getter
    private String valaue;

}
