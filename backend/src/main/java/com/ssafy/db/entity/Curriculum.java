package com.ssafy.db.entity;

import com.google.cloud.storage.Bucket;
import com.ssafy.api.request.CurriculumUpdateReq;
import com.ssafy.common.util.GCSUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Curriculum {
    @Id
    @Column(name = "curriculum_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column
    private String name;

    @Column(name = "index_no")
    private Long indexNo;

    @Column(length = 2000)
    private String description;

    @Column
    private Long time;

    @Column(name = "img_url", length = 1000)
    private String imgUrl;

    protected Curriculum() {
    }

    public void update(CurriculumUpdateReq curriculumUpdateReq, Bucket bucket){
        if (curriculumUpdateReq.getName() != null) {
            this.name=curriculumUpdateReq.getName();
        }
        if (curriculumUpdateReq.getDescription() != null) {
            this.description =curriculumUpdateReq.getDescription();
        }
        if (curriculumUpdateReq.getImg() != null) { // 이미지는 기존꺼 삭제 후 다시 저장.. 사진 첨부 안했으면 그냥 그대로 두기
            GCSUtil.updateCurrImg(this, bucket, curriculumUpdateReq.getImg());
        }
    }
}
