package com.tekcapzule.lms.user.domain.model;

import lombok.Getter;

public enum EnrollmentStatus {
    NOTSTARTED("Enrolled"),
    INPROGRESS("In Progress"),
    COMPLETED("Completed");

    @Getter
    private String status;

    EnrollmentStatus (String status){
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public static EnrollmentStatus getEnrollmentStatus(String courseStatus) {
        for (EnrollmentStatus enrollmentStatus : values()) {
            if (enrollmentStatus.status.equals(courseStatus)) {
                return enrollmentStatus;
            }
        }
        throw new IllegalArgumentException(courseStatus);
    }
}
