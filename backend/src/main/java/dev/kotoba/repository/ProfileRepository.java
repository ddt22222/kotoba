package dev.kotoba.repository;
import dev.kotoba.domain.Profile;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
public interface ProfileRepository extends JpaRepository<Profile,UUID> {
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select p from Profile p where p.id=:id") Profile lock(UUID id);
}
