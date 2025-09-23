package com.micropay.security.mapper;

public interface Mapper<S, T> {

    T map(S source);

}

