package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class UserStatus implements Serializable {
    // 유저의 마지막 접속 시간(lastActiveAt) 을 기록해서, 온라인 여부를 판단하기 위한 도메인

    private static final long serialVersionUID = 1L;
    // 직렬화, 역직렬화

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private UUID userId;
    private Instant lastActiveAt;

    // 생성자
    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.userId = userId;
        this.lastActiveAt = Instant.now();
    }

    // 5분 이내면 온라인
    public boolean isOnline() {
        return lastActiveAt != null && lastActiveAt.isAfter(Instant.now().minusSeconds(300));
        // ( 마지막 접속 시간이 != null ) and (마지막 접속 시간이 "5분 전" 보다 최근이니? )
    }

    public void updateLastActiveAt(Instant lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
        this.updatedAt = Instant.now();
    }




}
