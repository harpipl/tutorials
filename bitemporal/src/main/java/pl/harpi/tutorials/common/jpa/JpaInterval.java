package pl.harpi.tutorials.common.jpa;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoField.YEAR;

@Getter
@Embeddable
@RequiredArgsConstructor(staticName = "of")
public class JpaInterval {
  public static final DateTimeFormatter PATH_DATE_TIME =
      new DateTimeFormatterBuilder()
          .appendValue(YEAR, 4)
          .appendValue(MONTH_OF_YEAR, 2)
          .appendValue(DAY_OF_MONTH, 2)
          .appendLiteral('_')
          .appendValue(HOUR_OF_DAY, 2)
          .appendValue(MINUTE_OF_HOUR, 2)
          .appendValue(SECOND_OF_MINUTE, 2)
          .toFormatter();

  public static final LocalDateTime MIN_DATE_TIME = LocalDateTime.of(1600, 1, 1, 0, 0).withNano(0);
  public static final LocalDateTime MAX_DATE_TIME =
      LocalDateTime.of(LocalDate.of(4000, 12, 31), LocalTime.of(23, 59, 59)).withNano(0);

  static final String TXT_START = "start";
  static final String TXT_END = "end";

  @Temporal(TemporalType.TIMESTAMP)
  private final LocalDateTime start;

  @Temporal(TemporalType.TIMESTAMP)
  private final LocalDateTime end;

  public JpaInterval() {
    this(MIN_DATE_TIME, MAX_DATE_TIME);
  }

  public JpaInterval copy() {
    return new JpaInterval(start, end);
  }

  public boolean hasCommonPart(JpaInterval with) {
    var start = MIN_DATE_TIME;
    var end = MAX_DATE_TIME;

    if (with != null) {
      start = (with.getStart() == null) ? MIN_DATE_TIME : with.getStart().withNano(0);
      end = (with.getEnd() == null) ? MAX_DATE_TIME : with.getEnd().withNano(0);
    }

    val thisFrom = (this.getStart() == null) ? MIN_DATE_TIME : this.getStart();
    val thisTo = (this.getEnd() == null) ? MAX_DATE_TIME : this.getEnd();

    return !thisFrom.isAfter(end) && !thisTo.isBefore(start);
  }
}
