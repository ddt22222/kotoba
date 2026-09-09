package dev.kotoba.service;
import dev.kotoba.domain.Progress;
import dev.kotoba.api.Models.Grade;
import java.time.*;
import org.springframework.stereotype.Component;
@Component public class ReviewScheduler {
 public void apply(Progress p,Grade grade,Instant now){
  p.reviewCount++;p.lastReviewedAt=now;
  if(grade==Grade.AGAIN){p.incorrectCount++;p.intervalDays=0;p.nextReviewAt=now.plusSeconds(600);p.difficulty=Math.max(1.3,p.difficulty-.2);p.status="LEARNING";return;}
  p.correctCount++;
  p.intervalDays=switch(grade){case HARD->Math.max(1,(int)Math.ceil(p.intervalDays*1.2));case GOOD->Math.max(1,(int)Math.ceil(p.intervalDays*p.difficulty));case EASY->Math.max(4,(int)Math.ceil(p.intervalDays*p.difficulty*1.3));default->1;};
  p.intervalDays=Math.min(365,p.intervalDays);
  if(grade==Grade.EASY)p.difficulty=Math.min(3,p.difficulty+.15);
  p.nextReviewAt=now.plus(Duration.ofDays(p.intervalDays));p.status=p.intervalDays>=21?"MASTERED":"REVIEW";
 }
}
