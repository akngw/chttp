package org.example.chttp;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ExitTrapperExtension implements BeforeEachCallback, AfterEachCallback {

    private SecurityManager originalSecurityManager;

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        System.setSecurityManager(originalSecurityManager);
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        originalSecurityManager = System.getSecurityManager();
        System.setSecurityManager(new NoExitSecurityManager());
    }

}
