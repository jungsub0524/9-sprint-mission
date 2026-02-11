package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResult;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.time.Instant;
import java.util.*;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;

    public BasicChannelService(ChannelRepository channelRepository,
                               MessageRepository messageRepository,
                               ReadStatusRepository readStatusRepository,
                               UserRepository userRepository) {
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
        this.readStatusRepository = readStatusRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ChannelResult createPublic(PublicChannelCreateRequest request) {
        if (request == null) throw new IllegalArgumentException("request is null");
        if (isBlank(request.name())) throw new IllegalArgumentException("name is blank");

        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
        Channel saved = channelRepository.save(channel);

        // public 생성 직후 최신 메세지 없음 / 참여자 목록 없음
        return toResult(saved, null, List.of());

    }

    @Override
    public ChannelResult createPrivate(PrivateChannelCreateRequest request) {
        if (request == null) throw new IllegalArgumentException("request is null");
        if (request.participantIds() == null || request.participantIds().isEmpty())
            throw new IllegalArgumentException("participantIds is empty");

        // 1) 참여 유저 존재 검증
        for (UUID userId : request.participantIds()) {
            if (userId == null) throw new IllegalArgumentException("userId is null");
            userRepository.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException("user not found: " +  userId));
        }

        // 2) private 채널 생성 (name/description 생략)
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel savedChannel = channelRepository.save(channel);

        // 3) 참여자별 ReadStatus 생성
        for (UUID userId : request.participantIds()) {
            ReadStatus rs = new ReadStatus(userId, savedChannel.getId());
            readStatusRepository.save(rs);
        }

        return toResult(savedChannel, null, new ArrayList<>(request.participantIds()));
    }

    @Override
    public ChannelResult find(UUID channelId) {
        if (channelId == null) throw new IllegalArgumentException("channelId is null");

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("channel not found: " +  channelId));

        Instant latestMessgeAt = computeLatestMessageAt(channelId);

        List<UUID> participantIds = (channel.getType() == ChannelType.PRIVATE)
                ? computeParticipantIds(channelId)
                : List.of();

        return toResult(channel, latestMessgeAt, participantIds);
    }

    @Override
    public List<ChannelResult> findAllByUserId(UUID userId) {
        if (userId == null) throw new IllegalArgumentException("userId is null");

        //user 가 참여한 private 채널 id들 (= ReadStatus 기반)
        Set<UUID> privateChannelIds = new HashSet<>();
        for (ReadStatus rs : readStatusRepository.findAllByUserId(userId)) {
            privateChannelIds.add(rs.getChannelId());
        }

        List<ChannelResult> results = new ArrayList<>();

        for (Channel channel : channelRepository.findAll()) {
            UUID channelId = channel.getId();

            if (channel.getType() == ChannelType.PUBLIC) {
                results.add(toResult(channel, computeLatestMessageAt(channelId), List.of()));
                continue;
            }

            // private: user가 참여한 채널만 노출
            if (privateChannelIds.contains(channelId)) {
                results.add(toResult(
                        channel,
                        computeLatestMessageAt(channelId),
                        computeParticipantIds(channelId)
                ));
            }
        }
        return results;

    }

    @Override
    public ChannelResult update(ChannelUpdateRequest request) {
        if (request == null) throw new IllegalArgumentException("request is null");
        if (request.id() == null) throw new IllegalArgumentException("id is null");

        Channel channel = channelRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("channel not found: " + request.id()));

        // PRIVATE 채널은 수정 불가
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new UnsupportedOperationException("PRIVATE channel cannot be updated");
        }

        // name은 null이면 유지, 빈 문자열이면 예외
        if (request.name() != null && isBlank(request.name())) {
            throw new IllegalArgumentException("name is blank");
        }

        // description은 null이면 유지
        if (request.description() != null) {
            channel.update(request.name(), request.description());
        }

        Channel saved = channelRepository.save(channel);

        // PUBLIC 채널이므로 participantIds는 빈 리스트로 반환
        return toResult(saved, computeLatestMessageAt(saved.getId()), List.of());

    }

    @Override
    public void delete(UUID channelId) {
        if (channelId == null) throw new IllegalArgumentException("channelId is null");

        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("channel not found: " + channelId);
        }

        // 1) Message 삭제
        for (var message : messageRepository.findAll()) {
            if (channelId.equals(message.getChannelId())) {
                messageRepository.deleteById(message.getId());
            }
        }

        // 2) ReadStatus 삭제
        readStatusRepository.deleteAllByChannelId(channelId);

        // 3) Channel 삭제
        channelRepository.deleteById(channelId);
    }


    //helper
    private ChannelResult toResult(Channel channel, Instant latestMessageAt, List<UUID> participantIds) {
        return new ChannelResult(
                channel.getId(),
                channel.getName(),
                channel.getDescription(),
                channel.getType().name(),
                latestMessageAt,
                participantIds,
                channel.getCreatedAt(),
                channel.getUpdatedAt()
        );
    }

    private Instant computeLatestMessageAt(UUID channelId) {
        return messageRepository.findAll().stream()
                .filter(m -> channelId.equals((m.getChannelId())))
                .map(m -> m.getCreatedAt())
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    private List<UUID> computeParticipantIds(UUID channelId) {
        return readStatusRepository.findAllByChannelId(channelId).stream()
                .map(ReadStatus::getUserId)
                .distinct()
                .toList();
    }



    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }


}
