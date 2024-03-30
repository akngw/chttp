package org.example.chttp;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class StandardErrorCaptorExtension implements BeforeEachCallback, AfterEachCallback {
    private PrintStream originalErr;
    private ByteArrayOutputStream err;

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        System.setErr(originalErr);
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        originalErr = System.err;
        err = new ByteArrayOutputStream();
        System.setErr(new PrintStream(err));
    }

    public String getErr() {
        return err.toString();
    }
}
