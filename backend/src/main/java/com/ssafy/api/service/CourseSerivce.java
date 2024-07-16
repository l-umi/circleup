package com.ssafy.api.service;

import com.ssafy.api.response.CourseRes;
import com.ssafy.api.response.CoursesRes;
import com.ssafy.api.response.InstructorRes;
import com.ssafy.api.response.TagRes;
import com.ssafy.common.exception.handler.NotFoundException;
import com.ssafy.db.entity.Course;
import com.ssafy.db.entity.Instructor;
import com.ssafy.db.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseSerivce {

    private final CourseRepository courseRepository;

    //////////////////////////////////////////////////////////////////////////
    public List<TagRes> getTagList() {
        return courseRepository.getAllTag().stream().map(tag-> new TagRes(tag.getId(), tag.getName())).collect(Collectors.toList());
    }
    //////////////////////////////////////////////////////////////////////////
    public List<CoursesRes> getCoursesByTitle(String name, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return courseRepository.findByKeyword(name, pageable)
                .stream()
                .map(CoursesRes::of)
                .collect(Collectors.toList());
    }

    public List<CoursesRes> getCoursesByView(int page, int size){
        Pageable pageable = PageRequest.of(page, size);

        return courseRepository.findAllByOrderByViewDesc(pageable).stream().map(CoursesRes::of).collect(Collectors.toList());
    }

    public List<CoursesRes> getFreeCourses(int page, int size){
        Pageable pageable = PageRequest.of(page, size);

        return courseRepository.findByPrice(0L, pageable)
                .stream()
                .map(CoursesRes::of)
                .collect(Collectors.toList());
    }

    public List<CoursesRes> getOfferingCourses(int page, int size){
        Pageable pageable = PageRequest.of(page, size);

        Long tagSize = courseRepository.getTagSize();
        LocalDate today = LocalDate.now();
        Long randomTagId = today.getDayOfYear() % tagSize + 1;

        return courseRepository.findByTagId(randomTagId, pageable)
                .stream()
                .map(CoursesRes::of)
                .collect(Collectors.toList());
    }

    public List<CoursesRes> getRegisteredCourses(Long memberId, int page, int size){
        Pageable pageable = PageRequest.of(page, size);

        return courseRepository.findByRegisteredMemberId(memberId, pageable)
                .stream()
                .map(CoursesRes::of)
                .collect(Collectors.toList());
    }

    public List<CoursesRes> getCoursesByTags(List<Long> tags, int page, int size){
        Pageable pageable = PageRequest.of(page, size);

        return courseRepository.findByTagIds(tags, Long.valueOf(tags.size()), pageable)
                .stream()
                .map(CoursesRes::of)
                .collect(Collectors.toList());

    }
    //////////////////////////////////////////////////////////////////////////

    @Transactional
    public CourseRes getCourseById(Long id){
        Course course = courseRepository.findAllById(id).orElseThrow(
                ()-> new NotFoundException("Not Found Course : Course_id is " + id)
        );
        course.upView();
        return CourseRes.of(course);
    }


    public InstructorRes getInstructorByCourseId(Long id){
        return courseRepository.findInstructorByCourseId(id).orElseThrow(
                ()-> new NotFoundException("Not Found Instructor of Course : Course_id is " + id)
        );
    }

    //////////////////////////////////////////////////////////////////////////

    public List<CoursesRes> getCoursesImade(Long member_id){
        Instructor instructor = courseRepository.getInstructorById(member_id).orElseThrow(
                () -> new NotFoundException("Not Found Instructor : Instructor_id is " + member_id)
        );
//        Member member = courseRepository.getMemberById(instructor.getId()).orElseThrow(
//                () -> new NotFoundException("Not Found Member : Member_id is " + member_id)
//        );
        return courseRepository.findByInstructor(instructor).stream()
                .map(CoursesRes::of)
                .collect(Collectors.toList());
    }

    public List<CoursesRes> getCoursesIregistered(Long member_id){
        return courseRepository.findByRegisteredMemberId(member_id)
                .stream().map(CoursesRes::of).collect(Collectors.toList());
    }
}
