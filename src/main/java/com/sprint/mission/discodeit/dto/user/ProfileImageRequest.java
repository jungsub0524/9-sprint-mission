package com.sprint.mission.discodeit.dto.user;

public record ProfileImageRequest(
        byte[] bytes,
        String contentType,
        String filename
) {
}
