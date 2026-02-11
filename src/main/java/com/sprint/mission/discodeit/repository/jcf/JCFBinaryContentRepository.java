package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.util.*;

public class JCFBinaryContentRepository implements BinaryContentRepository {

    private final Map<UUID, BinaryContent> store = new HashMap<>();

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        store.put(binaryContent.getId(), binaryContent);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<BinaryContent> findAll() {
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
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        if (ids == null) return List.of();
        List<BinaryContent> result = new ArrayList<>();
        for (UUID id : ids) {
            BinaryContent bc = store.get(id);
            if (bc != null) result.add(bc);
        }
        return result;
    }
}
