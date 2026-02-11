package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResult;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    // create: 유저 생성 파라미터 + (선택)프로필 이미지까지 DTO로 묶어서 받기
    User create(UserCreateRequest request);

    // find/findAll: password 제외 + online 포함한 결과 DTO로 반환
    User find(UUID userId);
    List<UserResult> findAll();

    // update: 수정 대상 id + 변경 값 + (선택)프로필 교체도 다음 DTO로 반환
    User update(UserUpdateRequest request);

    // delete: userId로 삭제 (연관 삭제는 구현체에서)
    void delete(UUID userId);
}
