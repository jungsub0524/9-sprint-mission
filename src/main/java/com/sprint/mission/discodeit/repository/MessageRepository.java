package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    Optional<Message> findById(UUID id);
    List<Message> findAll();
    boolean existsById(UUID id);
    void deleteById(UUID id);

    // MessageService 요구사항: findAllByChannelId 로 변경
    List<Message> findAllByUserId(UUID id);

    // ChannelService find/findAll DTO에 "최근 메세지 시간" 포함
    Optional<Message> findLatestByChannelId(UUID channelId);

    // ChannelService delete: 채널 삭제 시 Message 같이 삭제
    void deleteAllByChannelId(UUID channelId);
}
