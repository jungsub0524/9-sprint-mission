package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResult;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    public BasicMessageService(
            MessageRepository messageRepository,
            ChannelRepository channelRepository,
            UserRepository userRepository,
            BinaryContentRepository binaryContentRepository
    ) {
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.binaryContentRepository = binaryContentRepository;
    }

    // --------------------
    // create (DTO 파라미터 + (선택) 첨부파일)
    // --------------------
    @Override
    public MessageResult create(MessageCreateRequest request) {
        validateCreate(request);

        if (!channelRepository.existsById(request.channelId())) {
            throw new NoSuchElementException("channel not found: " + request.channelId());
        }
        if (!userRepository.existsById(request.authorId())) {
            throw new NoSuchElementException("user not found: " + request.authorId());
        }

        // 첨부파일(선택): null이면 빈 리스트로 통일
        List<UUID> attachmentIds = (request.attachmentIds() == null)
                ? List.of()
                : new ArrayList<>(request.attachmentIds());

        Message message = new Message(request.content(), request.channelId(), request.authorId());
        message.setAttachmentIds(attachmentIds);

        Message saved = messageRepository.save(message);
        return toResult(saved);
    }

    // --------------------
    // find (id로 조회)
    // --------------------
    @Override
    public MessageResult find(UUID messageId) {
        if (messageId == null) throw new IllegalArgumentException("messageId is null");

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("message not found: " + messageId));

        return toResult(message);
    }

    // --------------------
    // findAllByChannelId (특정 채널의 메시지만)
    // --------------------
    @Override
    public List<MessageResult> findAllByChannelId(UUID channelId) {
        if (channelId == null) throw new IllegalArgumentException("channelId is null");

        return messageRepository.findAll().stream()
                .filter(m -> channelId.equals(m.getChannelId()))
                .map(this::toResult)
                .collect(Collectors.toList());
    }

    // --------------------
    // update (DTO로 id + 변경값 그룹화)
    // --------------------
    @Override
    public MessageResult update(MessageUpdateRequest request) {
        validateUpdate(request);

        Message message = messageRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("message not found: " + request.id()));

        // content가 null이면 수정하지 않음
        if (request.content() != null) {
            message.update(request.content());
        }

        Message saved = messageRepository.save(message);
        return toResult(saved);
    }

    // --------------------
    // delete (연관 삭제: 첨부파일 BinaryContent)
    // --------------------
    @Override
    public void delete(UUID messageId) {
        if (messageId == null) throw new IllegalArgumentException("messageId is null");

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("message not found: " + messageId));

        List<UUID> attachmentIds = message.getAttachmentIds();
        if (attachmentIds != null) {
            for (UUID id : attachmentIds) {
                if (id != null) {
                    binaryContentRepository.deleteById(id);
                }
            }
        }

        messageRepository.deleteById(messageId);
    }

    // --------------------
    // helper methods
    // --------------------
    private void validateCreate(MessageCreateRequest request) {
        if (request == null) throw new IllegalArgumentException("request is null");
        if (isBlank(request.content())) throw new IllegalArgumentException("content is blank");
        if (request.channelId() == null) throw new IllegalArgumentException("channelId is null");
        if (request.authorId() == null) throw new IllegalArgumentException("authorId is null");
    }

    private void validateUpdate(MessageUpdateRequest request) {
        if (request == null) throw new IllegalArgumentException("request is null");
        if (request.id() == null) throw new IllegalArgumentException("id is null");
        if (request.content() != null && isBlank(request.content())) {
            throw new IllegalArgumentException("content is blank");
        }
    }

    private MessageResult toResult(Message message) {
        return new MessageResult(
                message.getId(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getAttachmentIds(),
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}