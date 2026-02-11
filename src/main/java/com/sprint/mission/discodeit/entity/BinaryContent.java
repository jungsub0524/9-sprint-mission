package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;
    // 파일 이미지 같은 '바이너리 데이터'를 저장하기 위한 엔티티


    // 공통 필드
    private UUID id;
    private Instant createdAt;

    // 실제 데이터 필드: byte[] bytes
    private byte[] bytes;
    // 파일/이미지 내용 자체가 들어가는 곳
    // 텍스트가 아니라 바이너리라서 byte[]가 정석

    // 메타데이터 필드: contentType, filename
    private String contentType;
    private String filename;

    //생성자
    public BinaryContent(byte[] bytes, String contentType, String filename) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.bytes = bytes;
        this.contentType = contentType;
        this.filename = filename;
        // contentType/filename : 파일 설명용 정보
    }

    // getter
    public UUID getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFilename() {
        return filename;
    }


}
