package com.red.api.transport;

public interface Client<T> {
    T getRef(String protocolName);
}
