package com.red.api.registry;

import com.red.api.config.RegistryConfig;

import java.util.Map;

public interface Registry {
    void register(RegistryConfig registryConfig);

    void unRegister(RegistryConfig registryConfig);

    void subscribe(RegistryConfig registryConfig);

    void unsubscribe(RegistryConfig registryConfig);

}
