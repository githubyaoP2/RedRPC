package com.red.api.transport;

import com.red.api.config.ServiceConfig;

public interface Server {
    void publish(ServiceConfig serviceConfig);
}
