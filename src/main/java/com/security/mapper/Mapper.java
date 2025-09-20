package com.security.mapper;

public interface Mapper<S, T> {

    T map(S source);

}

