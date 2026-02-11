package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserResult;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;

public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    public BasicAuthService(UserRepository userRepository,
                            UserStatusRepository userStatusRepository) {
        this.userRepository = userRepository;
        this.userStatusRepository = userStatusRepository;
    }

    @Override
    public UserResult login(LoginRequest request) {
        // 1) 입력 검증
        validate(request);

        // 2) username으로 유저 조회
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new NegativeArraySizeException("Username not found: " + request.username()));

        // 3) password 비교
        if (!user.getPassword().equals(request.password())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        // 4) online 계산
        boolean online = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(false);

        // 5) DTO 반환
        return toResult(user, online);
    }


    //helper

    // 로그인 입력이 비었는지 체크
    private void validate(LoginRequest request) {
        if (request == null) throw new IllegalArgumentException("request is null");
        if (isBlank(request.username())) throw new IllegalArgumentException("username is blank");
        if (isBlank(request.password())) throw new IllegalArgumentException("password is blank");
    }

    // User 엔티티를 UserResult로 바꿔서 반환
    private UserResult toResult(User user, boolean online) {
        return new UserResult(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                online,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    // null + 공백 같이 처리
    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
