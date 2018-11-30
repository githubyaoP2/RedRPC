package com.red.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "services")
public class ServicesModel {
    private List<ServiceModel> service;

    public List<ServiceModel> getService() {
        return service;
    }

    public void setService(List<ServiceModel> service) {
        this.service = service;
    }
}
