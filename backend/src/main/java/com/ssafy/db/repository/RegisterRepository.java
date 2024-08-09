package com.ssafy.db.repository;

import com.ssafy.db.entity.Register;
import org.kurento.client.internal.server.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RegisterRepository extends JpaRepository<Register, Long> {
    @Query("SELECT count(*) FROM Register r WHERE r.course.id = :courseId")
    Long countByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(r)>0 FROM Register r WHERE r.course.id = :courseId and r.member.id = :memberId")
    boolean existsByCourseIdAndMemberId(@Param("courseId") Long courseId, @Param("memberId") Long memberId);
}
