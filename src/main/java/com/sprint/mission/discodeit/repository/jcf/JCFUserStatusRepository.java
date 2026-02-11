package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.*;

public class JCFUserStatusRepository implements UserStatusRepository {

    private final Map<UUID, UserStatus> store = new HashMap<>();

    @Override
    public UserStatus save(UserStatus userStatus) {
        store.put(userStatus.getId(), userStatus);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(UUID id) {
        store.remove(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return store.containsKey(id);
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        for (UserStatus us : store.values()) {
            if (userId.equals(us.getUserId())) return Optional.of(us);
        }
        return Optional.empty();
    }
}
