package com.demoim_backend.demoim_backend.dto;

import com.demoim_backend.demoim_backend.model.User;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ResponseUser {

    private Long userid;
    private String username;
    private String profileImage;

    @Builder
    public ResponseUser(Long userid, String username, String profileImage) {
        this.userid = userid;
        this.username = username;
        this.profileImage = profileImage;
    }

    public ResponseUser entityToDto(User user){
        return  ResponseUser.builder()
                .userid(user.getId())
                .username(user.getUsername())
                .profileImage(user.getProfileImage())
                .build();
    }

}