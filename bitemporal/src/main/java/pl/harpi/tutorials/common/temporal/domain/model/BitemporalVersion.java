package pl.harpi.tutorials.common.temporal.domain.model;

import pl.harpi.tutorials.common.base.domain.model.MutableEntity;

import java.io.Serializable;

public interface BitemporalVersion<I extends Serializable, V extends Serializable>
    extends MutableEntity<I, V> {}
