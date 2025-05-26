package pl.harpi.tutorials.common.temporal.infrastructure.entity;


import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import pl.harpi.tutorials.common.base.infrastructure.entity.ImmutablePersistentEntity;
import pl.harpi.tutorials.common.temporal.domain.model.TemporalIdentity;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractTemporalIdentity extends ImmutablePersistentEntity<Long> implements TemporalIdentity<Long, String> {
    @Column(name = "logicalId", nullable = false, updatable = false, unique = true, length = 40)
    private String logicalId;
}
