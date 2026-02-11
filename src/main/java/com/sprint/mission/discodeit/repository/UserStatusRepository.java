package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {

    // 메서드 선언
    // create/update (JCF/File 구형체에서 저장 또는 갱신)
    UserStatus save(UserStatus userStatus);

    // find
    Optional<UserStatus> findById(UUID id); //UserStatus 고유 id로 조회
    Optional<UserStatus> findByUserId(UUID userId);

    // findAll
    List<UserStatus> findAll();

    // delete
    void deleteById(UUID id);
    void deleteByUserId(UUID userId); //User 삭제 시 cascade 처리용


}
