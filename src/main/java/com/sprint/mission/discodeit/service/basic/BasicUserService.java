package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.ProfileImageRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResult;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    public BasicUserService(UserRepository userRepository,
                            BinaryContentRepository binaryContentRepository,
                            UserStatusRepository userStatusRepository) {
        this.userRepository = userRepository;
        this.binaryContentRepository = binaryContentRepository;
        this.userStatusRepository = userStatusRepository;
    }

    @Override
    public UserResult create(UserCreateRequest request) {
        // 0) 입력 검증 (helper 사용)
        validateCreate(request);

        // 1) username 중복 검사
        userRepository.findByUsername(request.username()).ifPresent(u -> {
            throw new IllegalStateException("Username already exists: " + request.username());
        });

        // 2) email 중복 검사
        userRepository.findByEmail(request.email()).ifPresent(u -> {
            throw new IllegalStateException("Email already exists: " + request.email());
        });

        // 3) 프로필 이미지 저장
        UUID profiledId = null;
        if (request.profileImage() != null) {
            profiledId = saveProfile(request.profileImage());
        }

        // 4) User 엔티티 생성 + 저장
        User user = new User(request.username(), request.email(), request.password());
        if (profiledId != null) {
            user.setProfileId(profiledId);
        }
        User savedUser = userRepository.save(user);

        // 5) UserStatus 같이 생성
        userStatusRepository.save(new UserStatus(savedUser.getId()));

        // 6) 결과 DTO 반환
        return toResult(savedUser, true);
    }

    @Override
    public UserResult find(UUID userID) {
        if (userID == null) throw new IllegalArgumentException("userId is null");

        // 1) User 조회
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userID));

        // 2) online 계산
        boolean online = userStatusRepository.findByUserId(userID)
                .map(UserStatus::isOnline)
                .orElse(false);

        // 3) DTO 변환
        return toResult(user, online);
    }

    @Override
    public List<UserResult> findAll() {
       return userRepository.findAll().stream()
               .map(user -> {
                   boolean online = userStatusRepository.findByUserId((user.getId()))
                           .map(UserStatus::isOnline)
                           .orElse(false);
                   return toResult(user, online);
               })
               .toList();
    }

    @Override
    public UserResult update(UserUpdateRequest request) {
        validateUpdate(request);

        // 입력 검증 + 대상 유저 조회
        User user = userRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("User not found: " + request.id()));
        // 유저가 없으면 update는 불가능.


        // username 중복 검사 (자기 자신 제외)
        if (request.username() != null && !request.username().equals(user.getUsername())) {
            userRepository.findByUsername(request.username()).ifPresent(found -> {
                if (!found.getId().equals(user.getId())) {
                    throw new IllegalStateException("Username already exists: " + request.username());
                }
            });
        }

        // email 중복 검사 (자기 자신 제외)
        if (request.email() != null && !request.email().equals(user.getEmail())) {
            userRepository.findByEmail(request.email()).ifPresent(found -> {
                if (!found.getId().equals(user.getId())) {
                    throw new IllegalStateException("Email already exists: " + request.email());
                }
            });
        }

        // 프로필 이미지 교체
        if (request.newProfileImage() != null) {
            UUID oldProfileId = user.getProfileId();
            if (oldProfileId != null) {
                binaryContentRepository.deleteById(oldProfileId);
            }

            UUID newProfileId = saveProfile(request.newProfileImage());
            user.setProfileId(newProfileId);
        }

        // 값 업데이트 + 저장 + 결과 반환 (online 포함)
        user.update(request.username(), request.email(), request.password());

        User saved = userRepository.save(user);

        boolean online = userStatusRepository.findByUserId((user.getId()))
                .map(UserStatus::isOnline)
                .orElse(false);

        return toResult(saved, online);
    }

    @Override
    public void delete(UUID userId) {

        // 입력 검증 + 대상 유저 조회
        if (userId == null) throw new IllegalArgumentException("userId is null");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        // 프로필 이미지 삭제
        UUID profileId = user.getProfileId();
        if (profileId != null) {
            binaryContentRepository.deleteById(profileId);
        }

        // UserStatus 삭제
        userStatusRepository.deleteByUserId(userId);

        // 마지막에 User 삭제
        userRepository.deleteById(userId);

    }


    // helper methods

    // (1) create 검증
    private void validateCreate(UserCreateRequest request) {
        if (request == null) throw new IllegalArgumentException("request is null");
        if (isBlank(request.username())) throw new IllegalArgumentException("username is blank");
        if (isBlank(request.email())) throw new IllegalArgumentException("email is blank");
        if (isBlank(request.password())) throw new IllegalArgumentException("password is blank");
    }

    // (2) update 검증
    private void validateUpdate(UserUpdateRequest request) {
        if (request == null) throw new IllegalArgumentException("request is null");
        if (request.id() == null) throw new IllegalArgumentException("id is null");

        if (request.username() != null && isBlank(request.username()))
            throw new IllegalArgumentException("username is blank");
        if (request.email() != null && isBlank(request.email()))
            throw new IllegalArgumentException("email is blank");
        if (request.password() != null && isBlank(request.password()))
            throw new IllegalArgumentException("password is blank");
    }

    // (3) 프로필 저장 (BinaryContent 생성 + 저장 + id 반환)
    private UUID saveProfile(ProfileImageRequest req) {
        if (req == null || req.bytes().length == 0)
            throw new IllegalArgumentException("profile bytes is empty");
        if (isBlank(req.contentType()))
            throw new IllegalArgumentException("profile content type is blank");
        if (isBlank(req.filename()))
            throw new IllegalArgumentException("profile filename is blank");

        BinaryContent content = new BinaryContent(req.bytes(), req.contentType(), req.filename());
        return binaryContentRepository.save(content).getId();
    }

    // (4) 엔티티 -> 결과 DTO 반환
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

    // (5) 문자열 공백 검사
    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
