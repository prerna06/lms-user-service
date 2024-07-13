package com.tekcapzule.lms.user.domain.service;

import com.tekcapzule.lms.user.domain.command.*;
import com.tekcapzule.lms.user.domain.model.Enrollment;
import com.tekcapzule.lms.user.domain.model.EnrollmentStatus;
import com.tekcapzule.lms.user.domain.model.LmsUser;

import java.util.List;
import java.util.Map;


public interface UserService {

    void create(CreateCommand createCommand);

    void update(UpdateCommand updateCommand);

    void disable(DisableCommand disableCommand);

    void optInCourse(OptInCourseCommand optInCourseCommand);

    void optOutCourse(OptOutCourseCommand optOutCourseCommand);

    void subscribeTopic(SubscribeTopicCommand subscribeTopicCommand);

    void unsubscribeTopic(UnSubscribeTopicCommand unSubscribeTopicCommand);

    LmsUser get(String userId, String tenantId);

    Map<EnrollmentStatus, List<Enrollment>> getCoursesGroupByStatus(String userId, String tenantId);

    Map<EnrollmentStatus, Long> getCourseCountGroupByStatus(String userId, String tenantId);

    List<Enrollment> getCourseByStatus(String userId, String tenantId, String status);

    int getAllUsersCount();

    void updateUserProgress(UpdateUserProgressCommand updateUserProgressCommand);

    void updateOrAddUserActivity(UpdateUserProgressCommand updateUserProgressCommand);
}
