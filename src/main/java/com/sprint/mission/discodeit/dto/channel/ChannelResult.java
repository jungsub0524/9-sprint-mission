package com.sprint.mission.discodeit.dto.channel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResult(
        UUID id,
        String name,
        String description,
        String type,
        Instant LatestMessageAt,        // 가장 최근 메세지 시간
        List<UUID> participantIds,
        Instant createdAt,
        Instant updatedAt
) {
}
