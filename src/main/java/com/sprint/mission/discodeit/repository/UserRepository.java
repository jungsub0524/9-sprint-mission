package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    List<User> findAll();
    boolean existsById(UUID id);
    void deleteById(UUID id);

    // UserService create: username/email 중복 금지
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    // AutoService login: username+password 매칭 조회가 필요함
    // (선택1) 서비스에서 findByUsername 후 password 비교
    // (선택2) 레포에서 한 번에 찾기
    Optional<User> findByUsernameAndPassword (String username, String password);

}
