package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private String content;
    private UUID channelId;
    private UUID authorId;

    private List<UUID> attachmentIds = new ArrayList<>();

    public Message(String content, UUID channelId, UUID authorId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
    }

    public UUID getId() { return id; }

    public Instant getCreatedAt() { return createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }

    public String getContent() { return content; }

    public UUID getChannelId() { return channelId; }

    public UUID getAuthorId() { return authorId; }

    public List<UUID> getAttachmentIds() { return attachmentIds; }

    public void setAttachmentIds(List<UUID> attachmentIds) {
        this.attachmentIds = (attachmentIds == null)
                ? new ArrayList<>()
                : new ArrayList<>(attachmentIds);
    }

    public void update(String newContent) {
        boolean anyValueUpdated = false;

        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}