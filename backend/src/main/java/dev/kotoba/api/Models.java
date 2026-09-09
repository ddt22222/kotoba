package dev.kotoba.api;
import dev.kotoba.domain.*;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.*;
public final class Models {
 private Models(){}
 public record ItemView(UUID id,String type,String word,String reading,String meaningVi,String jlptLevel,String partOfSpeech,String exampleSentence,String exampleReading,String exampleMeaningVi,String notes,String formation,String explanation,String onyomi,String kunyomi,Integer strokeCount,String mnemonic,String extraExamples,Instant createdAt,Instant updatedAt,String status,boolean favorite,Instant nextReviewAt,long version) {
  public static ItemView of(Item i,Progress p){return new ItemView(i.id,i.type,i.word,Objects.requireNonNullElse(i.reading,""),i.meaningVi,i.jlptLevel,Objects.requireNonNullElse(i.partOfSpeech,""),Objects.requireNonNullElse(i.exampleSentence,""),Objects.requireNonNullElse(i.exampleReading,""),Objects.requireNonNullElse(i.exampleMeaningVi,""),Objects.requireNonNullElse(i.notes,""),Objects.requireNonNullElse(i.formation,""),Objects.requireNonNullElse(i.explanation,""),Objects.requireNonNullElse(i.onyomi,""),Objects.requireNonNullElse(i.kunyomi,""),i.strokeCount,Objects.requireNonNullElse(i.mnemonic,""),Objects.requireNonNullElse(i.extraExamples,""),i.createdAt,i.updatedAt,p==null?"NEW":p.status,p!=null&&p.favorite,p==null?null:p.nextReviewAt,i.version);}
 }
 public record PersonalInput(@NotBlank @Size(max=200) String word,@NotBlank @Size(max=1000) String meaningVi,@Size(max=2000) String exampleSentence,@Size(max=300) String reading,@Pattern(regexp="N[1-5]") String jlptLevel,@Size(max=8000) String notes,@NotNull Long version) {}
 public record GradeInput(@NotNull UUID requestId,@NotNull Grade grade){}
 public enum Grade {AGAIN,HARD,GOOD,EASY}
 public record StateInput(@NotNull State status,boolean favorite){}
 public enum State {NEW,LEARNING,REVIEW,MASTERED}
 public record SettingsInput(@NotBlank @Size(max=80) String timezone,@Min(1) @Max(100) int dailyGoal,@Pattern(regexp="light|dark|system") @NotNull String theme,@Pattern(regexp="N[1-5]") @NotNull String targetLevel){}
 public record SettingsView(String timezone,int dailyGoal,String theme,String targetLevel){}
 public record PageView<T>(List<T> content,long totalElements,int totalPages,int number,int size){}
 public record LevelStats(String level,String type,long total,long learned){}
 public record DayStats(String date,long reviews,long correct){}
 public record Stats(long vocabularyLearned,long grammarLearned,long kanjiLearned,long personalLearned,long totalReviews,double accuracy,int streak,long todayReviews,long due,int dailyGoal,List<LevelStats> levels,List<DayStats> days,List<ItemView> recent){}
}
