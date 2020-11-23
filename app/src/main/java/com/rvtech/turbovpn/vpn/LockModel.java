package com.rvtech.turbovpn.vpn;

public class LockModel {

    private boolean lock;

    public LockModel() {
        lock = false;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }
}
