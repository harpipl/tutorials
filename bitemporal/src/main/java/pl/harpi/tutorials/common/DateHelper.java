package pl.harpi.tutorials.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateHelper {
  public static LocalDateTime getCurrentDateTime() {
    return LocalDateTime.now().withNano(0);
  }
}
