package com.red.api.rpc;

public enum RedVersion {

    VERSION1((byte)1,16),
    VERSION2((byte)2,17);

    private byte version;
    private int headLength;

    RedVersion(byte version, int headLength) {
        this.version = version;
        this.headLength = headLength;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public int getHeadLength() {
        return headLength;
    }

    public void setHeadLength(int headLength) {
        this.headLength = headLength;
    }
}
