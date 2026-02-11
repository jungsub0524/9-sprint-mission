package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class ReadStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    // 유저가 특정 채널에서 마지막으로 읽은 시각(lastReadAt) 을 기록하는 도메인
    // Serializable : FileRepository가 .ser로 저장할 때 필요
    // serialVersionUID : 직렬화 버전 관리용 메타 정보 (필드이긴 하지만 도메인 상태값은 아님)

    // 공통 필드
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    // 관계 필드
    private UUID userId;
    private UUID channelId;

    //핵심 시간 필드
    private Instant lastReadAt;

    //생성자
    public ReadStatus(UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();

        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = Instant.EPOCH;
    }

    public void updateLastReadAt(Instant lastReadAt) {
        this.lastReadAt = lastReadAt;
        this.updatedAt = Instant.now();
    }

    //getter


    public UUID getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public Instant getLastReadAt() {
        return lastReadAt;
    }
}
