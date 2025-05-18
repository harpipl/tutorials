package pl.harpi.tutorials.common.jpa;

import static pl.harpi.tutorials.common.jpa.JpaInterval.TXT_END;
import static pl.harpi.tutorials.common.jpa.JpaInterval.TXT_START;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractVersion<I extends AbstractInstance>
    extends MutablePersistentEntity<Long, Integer> {
  @Embedded
  @AttributeOverrides(
      value = {
        @AttributeOverride(
            name = TXT_START,
            column = @Column(name = "RECORD_FROM", nullable = false)),
        @AttributeOverride(name = TXT_END, column = @Column(name = "RECORD_TO", nullable = false))
      })
  private JpaInterval recordInterval;

  @Embedded
  @AttributeOverrides(
      value = {
        @AttributeOverride(
            name = TXT_START,
            column = @Column(name = "VALID_FROM", nullable = false)),
        @AttributeOverride(name = TXT_END, column = @Column(name = "VALID_TO", nullable = false))
      })
  private JpaInterval validInterval;

  @Column(name = "IDENTITY_ID", nullable = false)
  private Long identityId;

  @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "INSTANCE_ID", nullable = false)
  private I objectInstance;

  public boolean isActive() {
    return getRecordInterval().getEnd().isEqual(JpaInterval.MAX_DATE_TIME);
  }
}
