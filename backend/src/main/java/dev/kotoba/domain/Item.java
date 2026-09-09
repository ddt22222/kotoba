package dev.kotoba.domain;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="learning_items") public class Item {
 @Id public UUID id=UUID.randomUUID();
 public UUID ownerId;
 @Column(nullable=false) public String type;
 @Column(nullable=false,length=200) public String word;
 @Column(length=300) public String reading="";
 @Column(nullable=false,length=1000) public String meaningVi;
 public String jlptLevel;
 public String partOfSpeech="";
 @Column(length=2000) public String exampleSentence="";
 @Column(length=2000) public String exampleReading="";
 @Column(length=2000) public String exampleMeaningVi="";
 @Column(length=8000) public String notes="";
 @Column(length=2000) public String formation="";
 @Column(length=8000) public String explanation="";
 public String onyomi="";
 public String kunyomi="";
 public Integer strokeCount;
 @Column(length=2000) public String mnemonic="";
 @Column(length=4000) public String extraExamples="";
 public Instant createdAt=Instant.now();
 public Instant updatedAt=Instant.now();
 @Version public long version;
}
