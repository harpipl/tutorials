package pl.harpi.tutorials.common.jpa;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AbstractObject<D extends AbstractIdentity, I extends AbstractInstance> {
  private final D identity;
  private final I instance;
}
