package com.ssafy.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.ssafy.api.request.CourseCreatePostReq;
import com.ssafy.api.request.CourseModifyUpdateReq;
import com.ssafy.api.request.CurriculumPostReq;
import com.ssafy.api.request.CurriculumUpdateReq;
import com.ssafy.api.response.*;
import com.ssafy.common.custom.BadRequestException;
import com.ssafy.common.custom.ConflictException;
import com.ssafy.common.custom.NotFoundException;
import com.ssafy.db.entity.*;
import com.ssafy.db.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseSerivce {

    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final RegisterRepository registerRepository;
    private final TagRepository tagRepository;
    private final CurriculumRepository curriculumRepository;
    private final Bucket bucket;

    //////////////////////////////////////////////////////////////////////////
    public CourseRes createCourse(CourseCreatePostReq courseCreatePostReq, Long memberId) {
        try {
            // 1. 유효성 검증
            // 요청자가 강사가 아닐때
            Instructor instructor = instructorRepository.findById(memberId).orElseThrow(
                    () -> new NotFoundException("Instructor not found")
            );

            // 빈 파일일때
            if (courseCreatePostReq.getImg() == null) {
                throw new BadRequestException("Not File");
            }
            // 이미지 파일이 아닐때
            String contentType = courseCreatePostReq.getImg().getContentType();
            if (contentType == null || !contentType.startsWith("image/")) { // contentType 확인 >> img 아니면 예외처리
                throw new BadRequestException("Not Image File");
            }

            // 2. 서비스 로직 실행
            // 현재 시간
            LocalDateTime now = LocalDateTime.now();
            // 타입 변경
            Timestamp timestamp = Timestamp.valueOf(now);
            // 일단 url null로 course 생성 + 등록 >> course id 생성!
            Course newCourse = courseCreatePostReq.toEntity(instructor, timestamp, null);
            newCourse = courseRepository.save(newCourse);

            String tags = courseCreatePostReq.getTags();
            ObjectMapper objectMapper = new ObjectMapper();
            List<Long> tagIds = objectMapper.readValue(tags, new TypeReference<List<Long>>() {
            });

            List<CourseTag> courseTags = newCourse.getCourseTagList();

            List<Tag> tagsToAdd = tagRepository.findAllById(tagIds);
            for (Tag tag : tagsToAdd) {
                CourseTag courseTag = new CourseTag();
                courseTag.setTag(tag);
                courseTag.setCourse(newCourse);

                courseTags.add(courseTag);
            }

            // 이미지 파일 네이밍
            String blobName = "course_" + newCourse.getId() + "_banner";
            BlobInfo blobInfo = bucket.create(blobName, courseCreatePostReq.getImg().getBytes(), courseCreatePostReq.getImg().getContentType());
            // img_url 넣어주기
            newCourse.setImgUrl(blobInfo.getMediaLink());
            // 3. 업데이트된 정보로 다시 저장
            return CourseRes.of(courseRepository.save(newCourse));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create course", e);
        }
    }

    public CourseRes updateCourse(Long courseId, CourseModifyUpdateReq courseModifyUpdateReq, Long memberId) {
        try {
            // 1. 유효성 검증
            Instructor instructor = instructorRepository.findById(memberId).orElseThrow(
                    () -> new NotFoundException("Instructor not found")
            );

            Course course = courseRepository.findById(courseId).orElseThrow(
                    () -> new NotFoundException("Course not found")
            );

            if (!course.getInstructor().equals(instructor)) {
                throw new BadRequestException("Instructor doesn't own the course");
            }

            MultipartFile img = courseModifyUpdateReq.getImg();
            if (img != null) {
                String contentType = img.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) { // contentType 확인 >> img 아니면 예외처리
                    throw new BadRequestException("Not Image File");
                }
            }
            // 2. 서비스 로직
            // 변경사항 확인 후 적용
            if (courseModifyUpdateReq.getName() != null) {
                course.setName(courseModifyUpdateReq.getName());
            }
            if (courseModifyUpdateReq.getSummary() != null) {
                course.setSummary(courseModifyUpdateReq.getSummary());
            }
            if (courseModifyUpdateReq.getPrice() != null) {
                course.setPrice(Long.parseLong(courseModifyUpdateReq.getPrice()));
            }
            if (courseModifyUpdateReq.getDescription() != null) {
                course.setDescription(courseModifyUpdateReq.getDescription());
            }
            if (img != null) { // 이미지는 기존꺼 삭제 후 다시 저장.. 사진 첨부 안했으면 그냥 그대로 두기
                String blobName = "course_" + courseId + "_banner";

                Blob blob = bucket.get(blobName);
                blob.delete();

                BlobInfo blobInfo = bucket.create(blobName, img.getBytes(), img.getContentType());
                course.setImgUrl(blobInfo.getMediaLink());
            }

            List<CourseTag> courseTags = course.getCourseTagList();
            courseTags.clear();

            String tags = courseModifyUpdateReq.getTags();
            ObjectMapper objectMapper = new ObjectMapper();
            List<Long> tagIds = objectMapper.readValue(tags, new TypeReference<List<Long>>() {
            });


            List<Tag> tagsToAdd = tagRepository.findAllById(tagIds);
            for (Tag tag : tagsToAdd) {
                CourseTag courseTag = new CourseTag();
                courseTag.setTag(tag);
                courseTag.setCourse(course);

                courseTags.add(courseTag);
            }
            // 3. 정보 업데이트
            return CourseRes.of(courseRepository.save(course));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update course", e);
        }
    }

    public void deleteCourse(Long courseId, Long memberId) {
        // 1. 유효성 검증
        Instructor instructor = instructorRepository.findById(memberId).orElseThrow(
                () -> new NotFoundException("Instructor not found")
        );

        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (!courseOptional.isPresent()) {
            throw new NotFoundException("Course not found");
        }

        Course course = courseOptional.get();
        if (!course.getInstructor().equals(instructor)) {
            throw new BadRequestException("Instructor doesn't own the course");
        }

        Long students = registerRepository.countByCourseId(courseId);
        if (students > 0) {
            throw new BadRequestException("More than one registered");
        }

        String blobName = "course_" + courseId + "_banner";
        Blob blob = bucket.get(blobName);
        blob.delete();

        courseRepository.delete(course);
    }

    //////////////////////////////////////////////////////////////////////////
    public CourseRes createCurriculum(CurriculumPostReq curriculumPostReq, Long courseId, Long memberId) {
        try {
            // 요청자가 강사가 아닐 때
            Instructor instructor = instructorRepository.findById(memberId).orElseThrow(
                    () -> new NotFoundException("Instructor not found")
            );
            // 강의가 유효하지 않을 때
            Optional<Course> courseOptional = courseRepository.findById(courseId);
            if (!courseOptional.isPresent()) {
                throw new NotFoundException("Course not found");
            }
            // 강의의 강사가 아닐 때
            Course course = courseOptional.get();
            if (!course.getInstructor().equals(instructor)) {
                throw new BadRequestException("Instructor doesn't own the course");
            }
            // 빈 파일일때
            if (curriculumPostReq.getImg() == null) {
                throw new BadRequestException("Not File");
            }
            // 이미지 파일이 아닐때
            String contentType = curriculumPostReq.getImg().getContentType();
            if (contentType == null || !contentType.startsWith("image/")) { // contentType 확인 >> img 아니면 예외처리
                throw new BadRequestException("Not Image File");
            }

            // Curriculum 생성 및 추가
            Curriculum newCurr = curriculumPostReq.toEntity(course);
            newCurr = curriculumRepository.save(newCurr);

            String blobName = "curriculum_" + newCurr.getId() + "_banner";
            BlobInfo blobInfo = bucket.create(blobName, curriculumPostReq.getImg().getBytes(), curriculumPostReq.getImg().getContentType());

            newCurr.setImgUrl(blobInfo.getMediaLink());
            curriculumRepository.save(newCurr);

            return CourseRes.of(course);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create curriculum", e);
        }
    }

    public CourseRes updateCurriculum(CurriculumUpdateReq curriculumUpdateReq, Long courseId, Long curriculumId, Long memberId) {
        try {
            // 요청자가 강사가 아닐 때
            Instructor instructor = instructorRepository.findById(memberId).orElseThrow(
                    () -> new NotFoundException("Instructor not found")
            );
            // 강의가 유효하지 않을 때
            Optional<Course> courseOptional = courseRepository.findById(courseId);
            if (!courseOptional.isPresent()) {
                throw new NotFoundException("Course not found");
            }
            // 강의의 강사가 아닐 때
            Course course = courseOptional.get();
            if (!course.getInstructor().equals(instructor)) {
                throw new BadRequestException("Instructor doesn't own the course");
            }
            // 커리큘럼이 유효하지 않을 때
            Optional<Curriculum> curriculumOptional = curriculumRepository.findById(curriculumId);
            if (!curriculumOptional.isPresent()) {
                throw new NotFoundException("Curriculum not found");
            }
            // 이미지가 유효하지 않을 때
            MultipartFile img = curriculumUpdateReq.getImg();
            if (img != null) {
                String contentType = img.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) { // contentType 확인 >> img 아니면 예외처리
                    throw new BadRequestException("Not Image File");
                }
            }

            Curriculum curriculum = curriculumOptional.get();
            // 변경사항 확인 후 적용
            if (curriculumUpdateReq.getName() != null) {
                curriculum.setName(curriculumUpdateReq.getName());
            }
            if (curriculumUpdateReq.getDescription() != null) {
                curriculum.setDescription(curriculumUpdateReq.getDescription());
            }
            if (img != null) { // 이미지는 기존꺼 삭제 후 다시 저장.. 사진 첨부 안했으면 그냥 그대로 두기
                String blobName = "curriculum_" + curriculum.getId() + "_banner";

                Blob blob = bucket.get(blobName);
                blob.delete();

                BlobInfo blobInfo = bucket.create(blobName, img.getBytes(), img.getContentType());
                curriculum.setImgUrl(blobInfo.getMediaLink());
            }
            curriculumRepository.save(curriculum);

            return CourseRes.of(course);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update curriculum", e);
        }
    }

    public void deleteCurriculum(Long courseId, Long curriculumId, Long memberId) {
        // 1. 유효성 검증
        Instructor instructor = instructorRepository.findById(memberId).orElseThrow(
                () -> new NotFoundException("Instructor not found")
        );

        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (!courseOptional.isPresent()) {
            throw new NotFoundException("Course not found");
        }

        Course course = courseOptional.get();
        if (!course.getInstructor().equals(instructor)) {
            throw new BadRequestException("Instructor doesn't own the course");
        }

        Optional<Curriculum> curriculumOptional = curriculumRepository.findById(curriculumId);
        if (!curriculumOptional.isPresent()) {
            throw new NotFoundException("Curriculum not found");
        }

        Curriculum curriculum = curriculumOptional.get();
        if (curriculum.getTime() != null && curriculum.getTime() > 0) {
            throw new BadRequestException("Curriculum already done");
        }

        String blobName = "curriculum_" + curriculumId + "_banner";
        Blob blob = bucket.get(blobName);
        blob.delete();

        curriculumRepository.delete(curriculum);
    }

    public List<CurriculumRes> getCurriculumById(List<Long> ids) {
        return curriculumRepository.findAllById(ids)
                .stream()
                .map(CurriculumRes::of)
                .collect(Collectors.toList());
    }

    //////////////////////////////////////////////////////////////////////////
    public List<TagRes> getTagList() {
        return tagRepository.findAll().stream().map(tag -> new TagRes(tag.getId(), tag.getName())).collect(Collectors.toList());
    }

    //////////////////////////////////////////////////////////////////////////
    public List<SearchRes> getCoursesByTitle(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Map<Long, Long> countRegistersByCourseId = courseRepository.countRegistersByCourse().stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],
                        arr -> (Long) arr[1]
                ));

        return courseRepository.findByKeyword(name, pageable)
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

        return courseRepository.findByTagIds(tags, Long.valueOf(tags.size()), pageable)
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

        return courseRepository.findByTagId(randomTagId, pageable)
                .stream()
                .map(CoursesRes::of)
                .collect(Collectors.toList());
    }

    public List<CoursesRes> getCoursesByView(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return courseRepository.findAllByOrderByViewDesc(pageable).stream().map(CoursesRes::of).collect(Collectors.toList());
    }

    public List<CoursesRes> getFreeCourses(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return courseRepository.findByPrice(0L, pageable)
                .stream()
                .map(CoursesRes::of)
                .collect(Collectors.toList());
    }

    public List<CoursesRes> getLatestCourses(int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return courseRepository.findAll(pageable)
                .stream()
                .map(CoursesRes::of)
                .collect(Collectors.toList());
    }

    public List<CoursesRes> getRegisteredCourses(Long id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return courseRepository.findByRegisteredMemberId(id, pageable)
                .stream()
                .map(CoursesRes::of)
                .collect(Collectors.toList());
    }

    //////////////////////////////////////////////////////////////////////////

    @Transactional
    public CourseRes getCourseById(Long id) {
        Course course = courseRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Not Found Course : Course_id is " + id)
        );
        course.upView();
        return CourseRes.of(course);
    }


    public InstructorRes getInstructorByCourseId(Long id) {
        return instructorRepository.findInstructorByCourseId(id).orElseThrow(
                () -> new NotFoundException("Not Found Instructor of Course : Course_id is " + id)
        );
    }

    //////////////////////////////////////////////////////////////////////////

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

    public Long roleRegister(Long memberId, Long courseId) {
        return courseRepository.checkRegisterStatus(memberId, courseId);
    }

    public boolean existRegister(Long memberId, Long courseId) {
        return courseRepository.existsRegisterByMemberIdAndCourseId(memberId, courseId) != null;
    }

    public void doRegister(Long memberId, Long courseId) {
        // 이미 수강중이면
        if (existRegister(memberId, courseId)) {
            throw new ConflictException("Already registered");
        }

        // 수강등록 성공여부
        if (!courseRepository.postRegister(memberId, courseId)) {
            throw new NotFoundException("Not Found Course or Member");
        }
    }

    public void cancelRegister(Long memberId, Long courseId) {
        // 수강취소
        if (!courseRepository.deleteRegister(memberId, courseId)) {
            throw new BadRequestException("Not Found Course or Member or Register");
        }
    }
}
