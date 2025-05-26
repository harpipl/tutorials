package pl.harpi.tutorials.common.temporal.domain.model;

import java.io.Serializable;

public record RecordData<
    I extends Serializable,
    L extends Serializable,
    TD extends TemporalIdentity<I, L>,
    TI extends TemporalInstance<I>>(
    TD identity, TI instance) {}
