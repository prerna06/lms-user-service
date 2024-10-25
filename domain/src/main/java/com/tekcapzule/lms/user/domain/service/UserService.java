package com.tekcapzule.lms.user.domain.service;

import com.tekcapzule.lms.user.domain.command.*;
import com.tekcapzule.lms.user.domain.model.*;

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

    List<Enrollment> getCoursesGroupByStatus(String userId, String tenantId, String status);

    Map<EnrollmentStatus, Long> getCourseCountGroupByStatus(String userId, String tenantId);

    int getAllUsersCount();

    void updateUserProgress(UpdateUserProgressCommand updateUserProgressCommand);

    void updateOrAddUserActivity(UpdateUserProgressCommand updateUserProgressCommand);

    String getEnrollmentStatus(String userId, String tenantId, String courseId);

    byte[] completeCourse(CompleteCourseCommand completeCourseCommand);

    List<UserRank> getLeaderBoard(String userId, String tenantId);

    List<QuickLink> createQuickLink(CreateQuickLinkCommand createQuickLinkCommand);

    List<QuickLink> updateQuickLink(CreateQuickLinkCommand createQuickLinkCommand);
}
