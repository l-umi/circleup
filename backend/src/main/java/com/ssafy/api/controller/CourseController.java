package com.ssafy.api.controller;

import com.ssafy.api.response.CourseRes;
import com.ssafy.api.response.CoursesRes;
import com.ssafy.api.response.InstructorRes;
import com.ssafy.api.response.TagRes;
import com.ssafy.api.service.CourseSerivce;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = {"강의"})
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CourseController {

    private final CourseSerivce courseService;

    @GetMapping("/tag")
    @ApiOperation(value = "태그 목록 조회")
    public ResponseEntity<List<TagRes>> taglist() {
        return ResponseEntity.ok().body(courseService.getTagList());
    }

    @GetMapping("/courses")
    @ApiOperation(value = "강의 목록 조회")
    public ResponseEntity<List<CoursesRes>> courselist(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false, value = "tag") List<Long> tags,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = true) int size) {
        List<CoursesRes> courseList;

        if (keyword != null) {
            // 강의 제목으로 검색
            return ResponseEntity.ok().body(courseService.getCoursesByTitle(keyword, page, size));
        } else if (tags != null) {
            // 태그 목록으로 검색
            return ResponseEntity.ok().body(courseService.getCoursesByTags(tags, page, size));
        } else if (type != null) {
            if (type.equals("offer")) { // 추천순
                return ResponseEntity.ok().body(courseService.getOfferingCourses(page, size));
            } else if (type.equals("hot")) { // 인기순
                return ResponseEntity.ok().body(courseService.getCoursesByView(page, size));
            } else if (type.equals("free")) { // 무료
                return ResponseEntity.ok().body(courseService.getFreeCourses(page, size));
            } else if (type.equals("latest")) { //최신순
                return ResponseEntity.ok().body(courseService.getLatestCourses(page, size));
            }
        }
        return ResponseEntity.badRequest().build();
    }


    @GetMapping("/courses/{course_id}")
    @ApiOperation(value = "강의 정보 상세 조회")
    @ApiResponses({
            @ApiResponse(code = 404, message = "해당 강의 없음")
    })
    public ResponseEntity<CourseRes> course(
            @PathVariable(name = "course_id") Long id
    ) {
        return ResponseEntity.ok().body(courseService.getCourseById(id));
    }

    @GetMapping("/courses/{course_id}/owner")
    @ApiOperation(value = "강사 정보 조회")
    @ApiResponses({
            @ApiResponse(code = 404, message = "강사 없음")
    })
    public ResponseEntity<InstructorRes> owner(
            @PathVariable(name = "course_id") Long id
    ) {
        return ResponseEntity.ok().body(courseService.getInstructorByCourseId(id));
    }

}
