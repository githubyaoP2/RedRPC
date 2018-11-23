package com.red.Model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "service")
public class ServiceModel {
    private String interfaceName;
    private String instanceName;
    private String methodName;

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
