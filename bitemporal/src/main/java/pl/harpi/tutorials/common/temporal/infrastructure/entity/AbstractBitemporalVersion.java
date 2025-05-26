package pl.harpi.tutorials.common.temporal.infrastructure.entity;

import static pl.harpi.tutorials.common.base.infrastructure.entity.JpaInterval.TXT_END;
import static pl.harpi.tutorials.common.base.infrastructure.entity.JpaInterval.TXT_START;

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
import pl.harpi.tutorials.common.base.infrastructure.entity.JpaInterval;
import pl.harpi.tutorials.common.base.infrastructure.entity.MutablePersistentEntity;
import pl.harpi.tutorials.common.temporal.domain.model.BitemporalVersion;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractBitemporalVersion<I extends AbstractTemporalInstance>
    extends MutablePersistentEntity<Long, Integer> implements BitemporalVersion<Long, Integer> {
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
