package com.tekcapzule.lms.user.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tekcapzule.lms.user.domain.model.*;
import com.tekcapzule.lms.user.domain.model.Module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Test {

    public static void main(String[] args) throws JsonProcessingException {
        LMSCourse course = new LMSCourse();
        Module module = new Module();
        Chapter chapter = new Chapter();
        chapter.setSerialNumber(1);
        chapter.setWatchedDuration(20);
        module.setSerialNumber(2);
        module.setChapters(Arrays.asList(chapter));
        course.setCourseId("4423653");
        course.setModules(Arrays.asList(module));

        LMSCourse coursea = new LMSCourse();
        Module modulea = new Module();
        List<Chapter> chapters = new ArrayList<>();
        Chapter chaptera = new Chapter();
        chaptera.setSerialNumber(1);
        chaptera.setWatchedDuration(15);
        chapters.add(chaptera);
        chaptera = new Chapter();
        chaptera.setSerialNumber(2);
        chaptera.setWatchedDuration(20);
        chapters.add(chaptera);
        List<Module> modules = new ArrayList<>();
        modulea.setSerialNumber(1);
        modulea.setChapters(chapters);
        modules.add(modulea);
        coursea.setCourseId("4423653");
        coursea.setModules(modules);

        Enrollment enrollment1 = new Enrollment();
        enrollment1.setCourseId(coursea.getCourseId());
        enrollment1.setCourse(coursea);
        LmsUser lmsUser = new LmsUser();
        lmsUser.setEnrollments(Arrays.asList(enrollment1));
        updateOrAddChapter(enrollment1, course);
        System.out.println(new ObjectMapper().writeValueAsString(lmsUser));
    }

    public static void updateOrAddChapter(Enrollment enrollment, LMSCourse course) {
        Optional<Module> moduleOpt = enrollment.getCourse().getModules().stream()
                .filter(module -> module.getSerialNumber() == course.getModules().get(0).getSerialNumber())
                .findFirst();

        if (moduleOpt.isPresent()) {
            Module module = moduleOpt.get();
            Optional<Chapter> chapterOpt = module.getChapters().stream()
                    .filter(chapter -> chapter.getSerialNumber() == course.getModules().get(0).getChapters().get(0).getSerialNumber())
                    .findFirst();

            if (chapterOpt.isPresent()) {
                chapterOpt.get().setWatchedDuration(course.getModules().get(0).getChapters().get(0).getWatchedDuration());
            } else {
                module.getChapters().add(course.getModules().get(0).getChapters().get(0));
            }
            module.setWatchedDuration(module.getChapters().stream()
                    .mapToInt(Chapter::getWatchedDuration)
                    .sum());
        } else {
            enrollment.getCourse().getModules().add(course.getModules().get(0));
        }
        enrollment.getCourse().setWatchedDuration(enrollment.getCourse().getModules().stream()
                .mapToInt(Module::getWatchedDuration)
                .sum());
    }
}
