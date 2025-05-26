package pl.harpi.tutorials.common.temporal.domain.model;

import pl.harpi.tutorials.common.base.domain.model.ImmutableEntity;

import java.io.Serializable;

public interface TemporalInstance<I extends Serializable> extends ImmutableEntity<I> {}
