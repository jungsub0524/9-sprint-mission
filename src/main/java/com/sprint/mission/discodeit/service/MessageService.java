package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResult;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    // create: DTO로 파라미터 그룹화 + (선택) 첨부파일
    MessageResult create(MessageCreateRequest request);

    // find: id로 조회
    MessageResult find(UUID messageId);

    // findAll -> findAllByChannelId: 특정 채널의 메시지만 조회
    List<MessageResult> findAllByChannelId(UUID channelId);

    // update: DTO로 파라미터 그룹화
    MessageResult update(MessageUpdateRequest request);

    // delete: 첨부파일(BinaryContent)도 같이 삭제
    void delete(UUID messageId);
}
