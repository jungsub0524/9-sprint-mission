package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class User implements Serializable {
    //필드 (상태값)
    private static final long serialVersionUID = 1L;
    // 파일 저장(FileRepository) 에서 .ser로 직렬화하기 위해 필요

    private UUID id;
    private UUID profileId; // BinaryContent의 id (프로필 이미지)

    private Instant createdAt;
    private Instant updatedAt;

    private String username;
    private String email;
    private String password;

    // 생성자
    public User(String username, String email, String password) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.username = username;
        this.email = email;
        this.password = password;
    }


    // getter들
    public UUID getId() {
        return id;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }


    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    // update 메서드
    public void update(String newUsername, String newEmail, String newPassword) {
        boolean anyValueUpdated = false;
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
            anyValueUpdated = true;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
            anyValueUpdated = true;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
