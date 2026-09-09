package dev.kotoba.repository;
import dev.kotoba.domain.Progress;
import java.util.*;
import org.springframework.data.jpa.repository.*;
public interface ProgressRepository extends JpaRepository<Progress,UUID> {
 Optional<Progress> findByUserIdAndItemId(UUID userId,UUID itemId);
 @EntityGraph(attributePaths="item") List<Progress> findByUserId(UUID userId);
 @EntityGraph(attributePaths="item") List<Progress> findByUserIdAndItemIdIn(UUID userId,List<UUID> ids);
 void deleteByItemId(UUID itemId);
}
