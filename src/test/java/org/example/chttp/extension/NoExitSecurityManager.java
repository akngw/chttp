package org.example.chttp.extension;

import java.security.Permission;

class NoExitSecurityManager extends SecurityManager {
    @Override
    public void checkExit(int status) {
        throw new ExitException(status);
    }

    @Override
    public void checkPermission(Permission perm) {
        // do nothing.
    }

    @Override
    public void checkPermission(Permission perm, Object context) {
        // do nothing.
    }
}
