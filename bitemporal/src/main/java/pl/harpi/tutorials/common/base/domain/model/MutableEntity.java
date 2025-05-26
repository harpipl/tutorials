package pl.harpi.tutorials.common.base.domain.model;

import java.io.Serializable;

public interface MutableEntity<I extends Serializable, V extends Serializable> extends ImmutableEntity<I> {
  V getVersion();

  void setVersion(V version);
}
