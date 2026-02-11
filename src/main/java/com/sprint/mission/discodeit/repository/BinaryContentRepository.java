package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {

    // create
    BinaryContent save(BinaryContent bynaryContent);

    // find
    Optional<BinaryContent> findById(UUID id);

    // findAllByIdIn
    List<BinaryContent> findAllByIdIn(List<UUID> ids);

    // delete
    void deleteById(UUID id);
}
