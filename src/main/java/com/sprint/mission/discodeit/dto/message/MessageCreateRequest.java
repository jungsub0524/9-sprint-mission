package com.sprint.mission.discodeit.dto.message;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequest(
        String content,
        UUID channelId,
        UUID authorId,
        List<UUID> attachmentIds // 선택(없으면 null 또는 빈 리스트)
) {
}