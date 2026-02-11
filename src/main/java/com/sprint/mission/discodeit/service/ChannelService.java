package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelResult;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    // create 분리
    ChannelResult createPublic(PublicChannelCreateRequest request);
    ChannelResult createPrivate(PrivateChannelCreateRequest request);

    // find
    ChannelResult find(UUID channelId);

    // findAll -> findAllByUserId (Public 전체 + Private는 참여한 것만)
    List<ChannelResult> findAllByUserId(UUID userId);

    // update (Private는 수정 불가)
    ChannelResult update(ChannelUpdateRequest request);

    // delete (연관 삭제: Message, ReadStatus)
    void delete(UUID channelId);
}
