package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {

    // create / update
    ReadStatus save(ReadStatus readStatus);

    // find
    Optional<ReadStatus> findById(UUID id);
    // ReadStatus의 고유 id로 조회

    // (userId, channelId) 조합 중복 체크용
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

    // channelId 기준 조회
    List<ReadStatus> findAllByChannelId(UUID channelId);

    // findAllByUserId
    List<ReadStatus> findAllByUserId(UUID userId);

    // delete (cascade)
    void deleteAllByUserId(UUID userId);
    void deleteAllByChannelId(UUID channelId);



}
