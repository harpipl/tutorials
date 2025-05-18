package pl.harpi.tutorials.common.jpa;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractInstance extends ImmutablePersistentEntity<Long> {}
