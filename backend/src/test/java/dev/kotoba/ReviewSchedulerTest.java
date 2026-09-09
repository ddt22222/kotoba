package dev.kotoba;
import dev.kotoba.domain.Progress;
import dev.kotoba.service.ReviewScheduler;
import dev.kotoba.api.Models.Grade;
import org.junit.jupiter.api.Test;
import java.time.*;
import static org.assertj.core.api.Assertions.*;
class ReviewSchedulerTest {
 final ReviewScheduler scheduler=new ReviewScheduler();final Instant now=Instant.parse("2026-09-09T00:00:00Z");
 @Test void failureRelearnsSoonAndCountsOnce(){var p=new Progress();p.intervalDays=30;p.status="MASTERED";scheduler.apply(p,Grade.AGAIN,now);assertThat(p.nextReviewAt).isEqualTo(now.plusSeconds(600));assertThat(p.status).isEqualTo("LEARNING");assertThat(p.correctCount).isZero();assertThat(p.incorrectCount).isEqualTo(1);}
 @Test void intervalsIncreaseAndAreCapped(){var p=new Progress();scheduler.apply(p,Grade.EASY,now);assertThat(p.intervalDays).isEqualTo(4);scheduler.apply(p,Grade.GOOD,now);assertThat(p.intervalDays).isGreaterThan(4);for(int n=0;n<30;n++)scheduler.apply(p,Grade.EASY,now);assertThat(p.intervalDays).isEqualTo(365);assertThat(p.status).isEqualTo("MASTERED");assertThat(p.correctCount).isEqualTo(p.reviewCount);}
 @Test void hardSchedulesAtLeastTomorrow(){var p=new Progress();scheduler.apply(p,Grade.HARD,now);assertThat(p.nextReviewAt).isEqualTo(now.plus(Duration.ofDays(1)));}
}
