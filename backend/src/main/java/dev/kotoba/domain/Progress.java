package dev.kotoba.domain;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="user_learning_progress",uniqueConstraints=@UniqueConstraint(columnNames={"user_id","item_id"})) public class Progress {
 @Id public UUID id=UUID.randomUUID();
 public UUID userId;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="item_id") public Item item;
 public String status="NEW";
 public boolean favorite;
 public int reviewCount;
 public int correctCount;
 public int incorrectCount;
 public int intervalDays;
 public double difficulty=2.5;
 public Instant lastReviewedAt;
 public Instant nextReviewAt;
 @Version public long version;
}
