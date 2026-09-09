package dev.kotoba.domain;
import jakarta.persistence.*;
import java.util.UUID;
@Entity @Table(name="profiles") public class Profile {
 @Id public UUID id;
 public String timezone="Asia/Tokyo";
 public int dailyGoal=10;
 public String theme="system";
 public String targetLevel="N3";
}
