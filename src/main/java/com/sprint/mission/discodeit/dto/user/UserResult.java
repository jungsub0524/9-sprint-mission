package com.sprint.mission.discodeit.dto.user;

import java.time.Instant;
import java.util.UUID;

public record UserResult(
        UUID id,
        String username,
        String email,
        UUID profileId,
        boolean onlind,
        Instant createdAt,
        Instant updatedAt
) {

}
