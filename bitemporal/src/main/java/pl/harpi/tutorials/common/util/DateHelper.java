package pl.harpi.tutorials.common.util;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateHelper {
  public static LocalDateTime getCurrentDateTime() {
    return LocalDateTime.now().withNano(0);
  }
}
