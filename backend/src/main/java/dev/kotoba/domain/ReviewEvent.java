package dev.kotoba.domain;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="review_history") public class ReviewEvent {
 @Id public UUID id=UUID.randomUUID();
 public UUID userId;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="item_id") public Item item;
 public UUID requestId;
 public String grade;
 public Instant reviewedAt;
}
