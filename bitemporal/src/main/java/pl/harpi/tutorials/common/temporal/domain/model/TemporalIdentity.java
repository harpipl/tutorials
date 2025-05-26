package pl.harpi.tutorials.common.temporal.domain.model;

import pl.harpi.tutorials.common.base.domain.model.ImmutableEntity;

import java.io.Serializable;

public interface TemporalIdentity<I extends Serializable, L extends Serializable>
    extends ImmutableEntity<I> {
  L getLogicalId();

  void setLogicalId(L logicalId);
}
