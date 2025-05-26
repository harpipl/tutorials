package pl.harpi.tutorials.common.base.domain.model;

import java.io.Serializable;

public interface ImmutableEntity<I extends Serializable> {
  I getId();

  void setId(I id);
}
