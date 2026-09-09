package dev.kotoba.repository;
import dev.kotoba.domain.ReviewEvent;
import java.util.*;
import java.time.Instant;
import org.springframework.data.jpa.repository.*;
public interface ReviewRepository extends JpaRepository<ReviewEvent,UUID> {
 boolean existsByUserIdAndRequestId(UUID userId,UUID requestId);
 @EntityGraph(attributePaths="item") List<ReviewEvent> findByUserIdAndReviewedAtGreaterThanEqualOrderByReviewedAtDesc(UUID userId,Instant since);
 void deleteByItemId(UUID itemId);
}
