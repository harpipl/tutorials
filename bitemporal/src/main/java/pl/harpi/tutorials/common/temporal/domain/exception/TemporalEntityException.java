package pl.harpi.tutorials.common.temporal.domain.exception;

public class TemporalEntityException extends RuntimeException {
  public TemporalEntityException(String message, Throwable cause) {
    super(message, cause);
  }
}
