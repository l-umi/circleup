package com.ssafy.api.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CurriculumUpdateReq {
    private String name;
    private String description;
}
