package com.demoim_backend.demoim_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateProfileSaveRequestDto {
    private String nickname;
    private String position;
    private String desc;
    private String profileImage;

}