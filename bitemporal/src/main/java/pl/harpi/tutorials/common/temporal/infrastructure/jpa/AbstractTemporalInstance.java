package pl.harpi.tutorials.common.temporal.infrastructure.jpa;

import jakarta.persistence.MappedSuperclass;
import pl.harpi.tutorials.common.base.infrastructure.jpa.ImmutablePersistentEntity;
import pl.harpi.tutorials.common.temporal.domain.model.TemporalInstance;

@MappedSuperclass
public abstract class AbstractTemporalInstance extends ImmutablePersistentEntity<Long>
    implements TemporalInstance<Long> {}
