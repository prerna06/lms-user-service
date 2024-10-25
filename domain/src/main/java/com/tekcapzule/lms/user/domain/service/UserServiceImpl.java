package com.tekcapzule.lms.user.domain.service;

import com.tekcapzule.lms.user.domain.command.*;
import com.tekcapzule.lms.user.domain.exception.FoundException;
import com.tekcapzule.lms.user.domain.model.*;
import com.tekcapzule.lms.user.domain.model.Module;
import com.tekcapzule.lms.user.domain.repository.UserDynamoRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private static final String HASH = "#";
    private static String NAME = "%s %s";
    private static final String COMPLETION_CERTIFICATE = "completion_certificate";
    private UserDynamoRepository userDynamoRepository;
    private PdfService pdfService;

    @Autowired
    public UserServiceImpl(UserDynamoRepository userDynamoRepository, PdfService pdfService) {
        this.userDynamoRepository = userDynamoRepository;
        this.pdfService = pdfService;
    }

    @Override
    public void create(CreateCommand createCommand) {

        log.info(String.format("Entering create user service - Phone No.:%s", createCommand.getPhoneNumber()));
        log.info(String.format("Entering create user service - User Id:%s", createCommand.getEmailId()));


        LmsUser lmsUser = LmsUser.builder()
                .userId(createCommand.getEmailId())
                .emailId(createCommand.getEmailId())
                .firstName(createCommand.getFirstName())
                .lastName(createCommand.getLastName())
                .phoneNumber(createCommand.getPhoneNumber())
                .activeSince(DateTime.now(DateTimeZone.UTC).toString())
                /*.address(Address.builder()
                        .addressLine1(createCommand.getAddress().getAddressLine1())
                        .addressLine2(createCommand.getAddress().getAddressLine1())
                        .city(createCommand.getAddress().getCity())
                        .state(createCommand.getAddress().getState())
                        .country(createCommand.getAddress().getCountry())
                        .zipCode(createCommand.getAddress().getZipCode()).build())*/
                .status(Status.ACTIVE)
                .build();

        lmsUser.setAddedOn(createCommand.getExecOn());
        lmsUser.setUpdatedOn(createCommand.getExecOn());
        lmsUser.setAddedBy(createCommand.getExecBy().getUserId());

        userDynamoRepository.save(lmsUser);
    }

    @Override
    public void update(UpdateCommand updateCommand) {

        log.info(String.format("Entering update user service - User Id:%s", updateCommand.getUserId()));

        LmsUser lmsUser = userDynamoRepository.findBy(updateCommand.getUserId());
        if (lmsUser != null) {
            lmsUser.setEmailId(updateCommand.getEmailId());
            lmsUser.setFirstName(updateCommand.getFirstName());
            lmsUser.setLastName(updateCommand.getLastName());
            lmsUser.setPhoneNumber(updateCommand.getPhoneNumber());
            lmsUser.setAddress(Address.builder()
                    .addressLine1(updateCommand.getAddress().getAddressLine1())
                    .addressLine2(updateCommand.getAddress().getAddressLine2())
                    .city(updateCommand.getAddress().getCity())
                    .state(updateCommand.getAddress().getState())
                    .country(updateCommand.getAddress().getCountry())
                    .zipCode(updateCommand.getAddress().getZipCode())
                    .build()
            );
            lmsUser.setSubscribedTopics(updateCommand.getSubscribedTopics());
            lmsUser.setUpdatedOn(updateCommand.getExecOn());
            lmsUser.setUpdatedBy(updateCommand.getExecBy().getUserId());
            userDynamoRepository.save(lmsUser);
        }
    }

    @Override
    public void disable(DisableCommand disableCommand) {

        log.info(String.format("Entering disable user service - User Id:%s", disableCommand.getUserId()));

        LmsUser lmsUser = userDynamoRepository.findBy(disableCommand.getUserId());
        if (lmsUser != null) {

            lmsUser.setStatus(Status.INACTIVE);

            lmsUser.setUpdatedOn(disableCommand.getExecOn());
            lmsUser.setUpdatedBy(disableCommand.getExecBy().getUserId());

            userDynamoRepository.save(lmsUser);
        }
    }

    @Override
    public void optInCourse(OptInCourseCommand optInCourseCommand) {

        log.info(String.format("Entering OptIn course service - User Id:%s, course Id:%s", optInCourseCommand.getUserId(),
                optInCourseCommand.getCourseId()));

        LmsUser lmsUser = userDynamoRepository.findBy(optInCourseCommand.getUserId());
        if (lmsUser != null) {
            List<Enrollment> enrollments = lmsUser.getEnrollments();
            log.info("lmsUser.getEnrollments() :: %s" , enrollments);
            if ( enrollments == null) {
                log.info("lmsUser.getEnrollments() 1  :: %s" , enrollments);
                enrollments = new ArrayList<>();
            }
            Optional<Enrollment> enrollmentFound = enrollments.stream()
                    .filter(e -> e.getCourseId().equals(optInCourseCommand.getCourseId()))
                    .findFirst();
            if(enrollmentFound.isPresent()){
                throw new FoundException("Already Enrolled");
            }
            enrollments.add(Enrollment.builder()
                    .courseId(optInCourseCommand.getCourseId())
                            .course(LMSCourse.builder().courseId(optInCourseCommand.getCourseId())
                                    .build())
                    .enrollmentStatus(EnrollmentStatus.NOTSTARTED)
                    .build());
            log.info("lmsUser.getEnrollments() 2  :: %s" , enrollments);
            lmsUser.setEnrollments(enrollments);
            log.info("lmsUser.getEnrollments() 3 :: %s" , enrollments);
            lmsUser.setUpdatedOn(optInCourseCommand.getExecOn());
            lmsUser.setUpdatedBy(optInCourseCommand.getExecBy().getUserId());

            userDynamoRepository.save(lmsUser);
        }
    }

    @Override
    public void optOutCourse(OptOutCourseCommand optOutCourseCommand) {

        log.info(String.format("Entering optOut course service - User Id:%s, course Id:%s", optOutCourseCommand.getUserId(),
                optOutCourseCommand.getCourseId()));

        LmsUser lmsUser = userDynamoRepository.findBy(optOutCourseCommand.getUserId());
        if (lmsUser != null) {
            List<Enrollment> enrollments = lmsUser.getEnrollments();
            enrollments.removeIf(course -> course.getCourseId().equals(optOutCourseCommand.getCourseId()));
            lmsUser.setUpdatedOn(optOutCourseCommand.getExecOn());
            lmsUser.setUpdatedBy(optOutCourseCommand.getExecBy().getUserId());

            userDynamoRepository.save(lmsUser);
        }
    }

    @Override
    public void subscribeTopic(SubscribeTopicCommand subscribeTopicCommand) {
        log.info(String.format("Entering follow topic service - User Id:%s, Topic Code:%s", subscribeTopicCommand.getUserId(), subscribeTopicCommand.getTopicCodes()));

        LmsUser lmsUser = userDynamoRepository.findBy(subscribeTopicCommand.getUserId());
        if (lmsUser != null) {

            List<String> followedTopics = new ArrayList<>();
            followedTopics.addAll(subscribeTopicCommand.getTopicCodes());
            lmsUser.setSubscribedTopics(followedTopics);

            lmsUser.setUpdatedOn(subscribeTopicCommand.getExecOn());
            lmsUser.setUpdatedBy(subscribeTopicCommand.getExecBy().getUserId());

            userDynamoRepository.save(lmsUser);
        }
    }

    @Override
    public void unsubscribeTopic(UnSubscribeTopicCommand unSubscribeTopicCommand) {
        log.info(String.format("Entering unfollow topic service - User Id:%s, Topic Code:%s", unSubscribeTopicCommand.getUserId(), unSubscribeTopicCommand.getTopicCodes()));

        LmsUser lmsUser = userDynamoRepository.findBy(unSubscribeTopicCommand.getUserId());
        if (lmsUser != null) {

            List<String> followedTopics = new ArrayList<>();
            if (lmsUser.getSubscribedTopics() != null) {
                followedTopics = lmsUser.getSubscribedTopics();
            }

            followedTopics.removeAll(unSubscribeTopicCommand.getTopicCodes());
            lmsUser.setSubscribedTopics(followedTopics);

            lmsUser.setUpdatedOn(unSubscribeTopicCommand.getExecOn());
            lmsUser.setUpdatedBy(unSubscribeTopicCommand.getExecBy().getUserId());

            userDynamoRepository.save(lmsUser);
        }
    }

    @Override
    public LmsUser get(String userId, String tenantId) {

        log.info(String.format("Entering get user service - User Id:%s", userId));
        //return userDynamoRepository.findBy(tenantId+ HASH +userId);
        return userDynamoRepository.findBy(userId);
    }

    @Override
    public List<Enrollment> getCoursesGroupByStatus(String userId, String tenantId, String satus) {
        log.info(String.format("Entering get course by status service - User Id:%s", userId));

        //LmsUser user = userDynamoRepository.findBy(tenantId+ HASH +userId);
        LmsUser user = userDynamoRepository.findBy(userId);
        if(user!=null){
            return user.getEnrollments().stream().filter(e-> satus.equals(e.getEnrollmentStatus().getStatus())).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public Map<EnrollmentStatus, Long> getCourseCountGroupByStatus(String userId, String tenantId) {
        log.info(String.format("Entering get course by status service - User Id:%s", userId));

        //LmsUser user = userDynamoRepository.findBy(tenantId+ HASH +userId);
        LmsUser user = userDynamoRepository.findBy(userId);
        if(user!=null){
            return user.getEnrollments().stream().collect(Collectors.groupingBy(Enrollment::getEnrollmentStatus, Collectors.counting()));
        }
        return null;
    }

    @Override
    public int getAllUsersCount() {
        log.info("Entering get all users count service");
        return userDynamoRepository.getAllUsersCount();
    }

    @Override
    public void updateUserProgress(UpdateUserProgressCommand updateUserProgressCommand) {
        log.info("Entering updateUserProgress for UserId %s, Course Id %s ", updateUserProgressCommand.getUserId(), updateUserProgressCommand.getCourse().getCourseId());
        LmsUser lmsUser = userDynamoRepository.findBy(updateUserProgressCommand.getUserId());
        if(lmsUser == null){
            new RuntimeException("User not found");
        }
        log.info("User found :: "+lmsUser);
        LMSCourse course = updateUserProgressCommand.getCourse();
        log.info("Course found ::"+course);
        if (course == null) {
            new RuntimeException("No course in the request");
        }
        Optional<Enrollment> enrollmentOpt = lmsUser.getEnrollments().stream()
                .filter(e -> e.getCourseId().equals(course.getCourseId()))
                .findFirst();
        log.info("Enrollment found ::"+enrollmentOpt.get());
        if (enrollmentOpt.isPresent()) {
            Enrollment enrollment = enrollmentOpt.get();
            enrollment.setEnrollmentStatus(EnrollmentStatus.getEnrollmentStatus(course.getStatus()));
            enrollment.setCourse(course);
            updateEnrollment(lmsUser, enrollment, course);
        } else {
            new RuntimeException("Course enrollment not found");
        }
    }
    @Override
    public void updateOrAddUserActivity(UpdateUserProgressCommand updateUserProgressCommand) {
        log.info("Entering updateOrAddUserActivity for UserId %s, Course Id %s ", updateUserProgressCommand.getUserId(), updateUserProgressCommand.getCourse().getCourseId());
        LmsUser lmsUser = userDynamoRepository.findBy(updateUserProgressCommand.getUserId());
        log.info("User found :: "+lmsUser);
        LMSCourse course = updateUserProgressCommand.getCourse();
        Optional<Enrollment> enrollmentOpt = lmsUser.getEnrollments().stream()
                .filter(e -> e.getCourseId().equals(course.getCourseId()))
                .findFirst();
        if (enrollmentOpt.isPresent()) {
            Enrollment enrollment = enrollmentOpt.get();
            enrollment.setEnrollmentStatus(EnrollmentStatus.getEnrollmentStatus(course.getStatus()));
            enrollment.getCourse().setLastVisitedChapter(course.getLastVisitedChapter());
            enrollment.getCourse().setLastVisitedModule(course.getLastVisitedModule());
            enrollment.getCourse().setStatus(course.getStatus());
            enrollment.getCourse().setWatchedDuration(course.getWatchedDuration());
            enrollment.getCourse().setAssessmentScore(course.getAssessmentScore());
            enrollment.getCourse().setAssessmentStatus(course.getAssessmentStatus());

            log.info("Enrollment Found :: " + enrollment.getCourseId());
            for (Module moduleInRequest : course.getModules()){
                if(enrollment.getCourse().getModules()!=null) {
                    Optional<Module> moduleOpt = enrollment.getCourse().getModules().stream()
                            .filter(module -> module.getSerialNumber() == moduleInRequest.getSerialNumber())
                            .findFirst();

                    if (moduleOpt.isPresent()) {
                        Module module = moduleOpt.get();
                        log.info("Module Found :: " + module.getSerialNumber());
                        module.setWatchedDuration(moduleInRequest.getWatchedDuration());
                        module.setStatus(moduleInRequest.getStatus());
                        module.setQuizScore(moduleInRequest.getQuizScore());
                        module.setQuizStatus(moduleInRequest.getQuizStatus());
                        for (Chapter chapterInReq : moduleInRequest.getChapters()) {
                            if(module.getChapters()!=null) {
                                Optional<Chapter> chapterOpt = module.getChapters().stream()
                                        .filter(chapter -> chapter.getSerialNumber() == chapterInReq.getSerialNumber())
                                        .findFirst();

                                if (chapterOpt.isPresent()) {
                                    log.info("Chapter Found :: Updating " + chapterOpt.get().getSerialNumber());
                                    Chapter chapter = chapterOpt.get();
                                    chapter.setWatchedDuration(chapterInReq.getWatchedDuration());
                                    chapter.setStatus(chapterInReq.getStatus());
                                    module = updateChapter(module, chapter, chapterInReq.getSerialNumber());
                                } else {
                                    log.info("Chapter Not Found :: Adding");
                                    module.getChapters().add(chapterInReq);
                                    module.getChapters().sort(Comparator.comparingInt(Chapter::getSerialNumber));
                                }
                            }  else {
                                log.info("No Chapters Found :: Adding");
                                module.setChapters(Arrays.asList(chapterInReq));
                                module.getChapters().sort(Comparator.comparingInt(Chapter::getSerialNumber));
                            }


                        }
                        updateModule(enrollment, module, moduleInRequest.getSerialNumber());
                    } else {
                        log.info("Module Not Found :: Adding");
                        enrollment.getCourse().getModules().add(moduleInRequest);
                        enrollment.getCourse().getModules().sort(Comparator.comparingInt(Module::getSerialNumber));
                    }
                } else {
                    log.info("No Module Found :: Adding");

                    enrollment.getCourse().setModules(Arrays.asList(moduleInRequest));
                    enrollment.getCourse().getModules().sort(Comparator.comparingInt(Module::getSerialNumber));
                }
            }
            updateEnrollment(lmsUser, enrollment, course);
        }
    }

    @Override
    public String getEnrollmentStatus(String userId, String tenantId, String courseId) {
        log.info("Entering getEnrollmentStatus for UserId %s, Course Id %s ", userId, courseId);
        LmsUser lmsUser = userDynamoRepository.findBy(userId);
        log.info("User found :: "+lmsUser);
        if(lmsUser != null) {
            List<Enrollment> enrollments = lmsUser.getEnrollments();
            if(enrollments!=null && !enrollments.isEmpty()) {
                Optional<Enrollment> enrollmentOpt = lmsUser.getEnrollments().stream()
                        .filter(e -> e.getCourseId().equals(courseId))
                        .findFirst();
                if(enrollmentOpt.isPresent()){
                    return enrollmentOpt.get().getEnrollmentStatus().getStatus();
                }
            }
        } else {
            throw new RuntimeException(String.format("User: %s not found", userId));
        }
        return null;
    }

    @Override
    public byte[] completeCourse(CompleteCourseCommand completeCourseCommand) {
        log.info("Entering getEnrollmentStatus for UserId %s, Course Id %s ", completeCourseCommand.getUserId(), completeCourseCommand.getCourse().getCourseId());
        LmsUser lmsUser = userDynamoRepository.findBy(completeCourseCommand.getUserId());
        if(lmsUser == null){
            new RuntimeException("User not found");
        }
        LMSCourse course = completeCourseCommand.getCourse();
        if (course == null) {
            new RuntimeException("No course in the request");
        }
        Optional<Enrollment> enrollmentOpt = lmsUser.getEnrollments().stream()
                .filter(e -> e.getCourseId().equals(completeCourseCommand.getCourse().getCourseId()))
                .findFirst();
        if (enrollmentOpt.isPresent()) {
            Enrollment enrollment = enrollmentOpt.get();
            enrollment.setEnrollmentStatus(EnrollmentStatus.COMPLETED);
            enrollment.getCourse().setStatus(course.getStatus());
            enrollment.getCourse().setWatchedDuration(course.getWatchedDuration());
            enrollment.getCourse().setAssessmentScore(course.getAssessmentScore());
            enrollment.getCourse().setAssessmentStatus(course.getAssessmentStatus());
            lmsUser = updatePointsBadges(lmsUser, course);
            updateEnrollment(lmsUser, enrollment, course);
        }
        GetCertificateCommand getCertificateCommand = GetCertificateCommand.builder()
                .firstName(lmsUser.getFirstName())
                .lastName(lmsUser.getLastName())
                .courseDuration(String.valueOf(course.getCourseDuration()))
                .courseName(course.getCourseName())
                .courseInstructor(course.getInstructor())
                .certificateType(COMPLETION_CERTIFICATE).build();

        log.info("User found :: "+lmsUser);
        return pdfService.generateCertificate(getCertificateCommand);
    }

    @Override
    public List<UserRank> getLeaderBoard(String userId, String tenantId) {
        log.info("In getLeaderBoard");
        List<LmsUser> allUsers = userDynamoRepository.findAll();

        List<UserRank> userRanks = allUsers.stream()
                .map(lmsUser -> UserRank.builder()
                        .userName(String.format(NAME, lmsUser.getFirstName(), lmsUser.getLastName()))
                        .points(lmsUser.getPoints())
                        .build())
                .collect(Collectors.toList());
        userRanks.sort((u1, u2)-> Integer.compare(u2.getPoints(), u1.getPoints()));
        int rank = 1;

        for (int i = 0; i < userRanks.size(); i++) {

            // If points are equal, assign the same rank
            if (i > 0 && allUsers.get(i).getPoints() == allUsers.get(i - 1).getPoints()) {
                log.info("User Points :: "+allUsers.get(i).getPoints());
                userRanks.get(i).setRank(userRanks.get(i - 1).getRank());
            } else {
                userRanks.get(i).setRank(rank);
            }
            rank++;
        }
        log.info("userRanks", userRanks);
        return userRanks;
    }

    @Override
    public List<QuickLink> createQuickLink(CreateQuickLinkCommand createQuickLinkCommand) {
        log.info("Entering createQuickLink for UserId %s ", createQuickLinkCommand.getUserId());
        LmsUser lmsUser = userDynamoRepository.findBy(createQuickLinkCommand.getUserId());
        if(lmsUser == null){
            new RuntimeException("User not found");
        }
        List<QuickLink> quickLinks = createQuickLinkCommand.getQuickLinks();
        if (lmsUser.getQuickLinks() == null) {
            lmsUser.setQuickLinks(quickLinks);
        } else {
            lmsUser.getQuickLinks().addAll(quickLinks);
        }
        userDynamoRepository.save(lmsUser);
        return lmsUser.getQuickLinks();
    }

    @Override
    public List<QuickLink> updateQuickLink(CreateQuickLinkCommand createQuickLinkCommand) {
        log.info("Entering createQuickLink for UserId %s ", createQuickLinkCommand.getUserId());
        LmsUser lmsUser = userDynamoRepository.findBy(createQuickLinkCommand.getUserId());
        if(lmsUser == null){
            new RuntimeException("User not found");
        }
        List<QuickLink> quickLinks = createQuickLinkCommand.getQuickLinks();
        lmsUser.setQuickLinks(quickLinks);
        userDynamoRepository.save(lmsUser);
        return lmsUser.getQuickLinks();
    }

    private void updateEnrollment(LmsUser lmsUser, Enrollment enrollment, LMSCourse course) {
        log.info("In updateEnrollment");
        lmsUser.getEnrollments().stream()
                .filter(e -> e.getCourseId().equals(course.getCourseId()))
                .findFirst()
                .ifPresent(e -> {
                    int index = lmsUser.getEnrollments().indexOf(e);
                    lmsUser.getEnrollments().set(index, enrollment);
                });
        userDynamoRepository.save(lmsUser);
    }

    private LmsUser updatePointsBadges(LmsUser lmsUser, LMSCourse course) {
        int totalPoints = lmsUser.getPoints() + course.getPoints();
        lmsUser.setPoints(totalPoints);
        Map<String, Integer> badges = lmsUser.getBadges();
        if(badges == null){
            badges = new HashMap<>();
        }
        if(totalPoints == 100){
            badges.put("SILVER", 1);
        }
        if(totalPoints == 200){
            badges.put("SILVER", 1);
        }
        if(totalPoints == 300){
            badges.put("GOLD", 1);
        }
        return lmsUser;
    }

    private Enrollment updateModule(Enrollment enrollment, Module module, int moduleSerialNo) {
        log.info("In Module");
        LMSCourse lmsCourse = enrollment.getCourse();
        lmsCourse.getModules().stream()
                .filter(m -> m.getSerialNumber() == (moduleSerialNo))
                .findFirst()
                .ifPresent(m -> {
                    int index = lmsCourse.getModules().indexOf(m);
                    lmsCourse.getModules().set(index, module);
                });
        enrollment.setCourse(lmsCourse);
        log.info("In Module :: Updated enrollment "+enrollment);
        return enrollment;
    }
    private Module updateChapter(Module module, Chapter chapter, int chapterSerialNo) {
        log.info("In Chapter");
        module.getChapters().stream()
                .filter(c -> c.getSerialNumber() == (chapterSerialNo))
                .findFirst()
                .ifPresent(c -> {
                    int index = module.getChapters().indexOf(c);
                    module.getChapters().set(index, chapter);
                });
        log.info("In Chapter :: Updated module "+module);
        return module;
    }
}
