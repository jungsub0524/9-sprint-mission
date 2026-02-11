package com.sprint.mission.discodeit.dto.user;

public record UserCreateRequest(
        String username,
        String email,
        String password,
        ProfileImageRequest profileImage //null이면 프로필 없음
) {
}
