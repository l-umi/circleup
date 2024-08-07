package com.ssafy.api.service;

import com.ssafy.api.response.CourseRes;
import com.ssafy.api.response.CoursesRes;
import com.ssafy.api.response.SearchRes;
import com.ssafy.common.custom.BadRequestException;
import com.ssafy.common.custom.NotFoundException;
import com.ssafy.db.entity.Course;
import com.ssafy.db.entity.Instructor;
import com.ssafy.db.entity.Member;
import com.ssafy.db.entity.Status;
import com.ssafy.db.entity.enums.Role;
import com.ssafy.db.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseSerivce {

    private final CourseRepository courseRepository;
    private final TagRepository tagRepository;
    private final InstructorRepository instructorRepository;
    private final MemberRepository memberRepository;

    ////////////////////////////////////////

    // 상태 별 강의 조회 for 강사
    public List<CoursesRes> getMyCoursesByStatus(Long memberId, String status) {
        Member instructor = memberRepository.findById(memberId)
                .orElseThrow(()-> new NotFoundException("ID not found"));

        if(!instructor.getRole().equals(Role.Instructor)) throw new BadRequestException("Not Instructor");

        return courseRepository.findByStatusAndInstructorId(Status.get(status), memberId)
                .stream()
                .map(CoursesRes::of)
                .collect(Collectors.toList());
    }

    public List<SearchRes> getCoursesByTitle(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Map<Long, Long> countRegistersByCourseId = courseRepository.countRegistersByCourse().stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],
                        arr -> (Long) arr[1]
                ));

        return courseRepository.findApprovedByKeyword(name, pageable)
                .stream()
                .map(course ->
                        SearchRes.of(course,
                                countRegistersByCourseId.get(course.getId()),
                                course.getCourseTagList().stream().map(ct -> ct.getTag().getName()).collect(Collectors.toList()))
                )
                .collect(Collectors.toList());
    }

    public List<SearchRes> getCoursesByTags(List<Long> tags, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Map<Long, Long> countRegistersByCourseId = courseRepository.countRegistersByCourse().stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],
                        arr -> (Long) arr[1]
                ));

        return courseRepository.findApprovedByTagIds(tags, Long.valueOf(tags.size()), pageable)
                .stream()
                .map(course ->
                        SearchRes.of(course,
                                countRegistersByCourseId.get(course.getId()),
                                course.getCourseTagList().stream().map(ct -> ct.getTag().getName()).collect(Collectors.toList()))
                )
                .collect(Collectors.toList());
    }

    public List<CoursesRes> getOfferingCourses(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Long tagSize = tagRepository.getTagSize();
        LocalDate today = LocalDate.now();
        Long randomTagId = today.getDayOfYear() % tagSize + 1;

        return courseRepository.findByTagId(randomTagId, Status.Approved, pageable)
                .stream()
                .map(CoursesRes::of)
                .collect(Collectors.toList());
    }

    public List<CoursesRes> getCoursesByView(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return courseRepository.findAllByStatusOrderByViewDesc(Status.Approved, pageable).stream().map(CoursesRes::of).collect(Collectors.toList());
    }

    public List<CoursesRes> getFreeCourses(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return courseRepository.findByPriceAndStatus(0L, Status.Approved, pageable)
                .stream()
                .map(CoursesRes::of)
                .collect(Collectors.toList());
    }

    public List<CoursesRes> getLatestCourses(int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return courseRepository.findAllByStatus(Status.Approved, pageable)
                .stream()
                .map(CoursesRes::of)
                .collect(Collectors.toList());
    }

    ////////////////////////////

    public List<CoursesRes> getRegisteredCourses(Long id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return courseRepository.findByRegisteredMemberId(id, pageable)
                .stream()
                .map(CoursesRes::of)
                .collect(Collectors.toList());
    }

    public List<CoursesRes> getCoursesImade(Long id) {
        Instructor instructor = instructorRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Not Found Instructor : Instructor_id is " + id)
        );

        return courseRepository.findByInstructor(instructor).stream()
                .map(CoursesRes::of)
                .collect(Collectors.toList());
    }

    public List<CoursesRes> getCoursesIregistered(Long id) {
        return courseRepository.findByRegisteredMemberId(id)
                .stream().map(CoursesRes::of).collect(Collectors.toList());
    }

    public CourseRes getCourseById(Long id) {
        Course course = courseRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Not Found Course : Course_id is " + id)
        );
        course.upView();
        return CourseRes.of(course);
    }

    public Boolean existsCourse(Long courseId) {
        return (courseRepository.existsById(courseId)) ? true : false;
    }

    public Boolean instructorInCourse(Long courseId, Long memberId) {
        Long instructorId = instructorRepository.findInstructorIdByCourseId(courseId).orElseThrow(
                () -> new NotFoundException("Not Found Course : Course_id is " + courseId));
        ;
        return instructorId.equals(memberId);
    }

    public boolean existRegister(Long memberId, Long courseId) {
        return courseRepository.existsRegisterByMemberIdAndCourseId(memberId, courseId) != null;
    }
}
