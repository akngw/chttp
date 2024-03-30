package org.example.chttp;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class StandardOutCaptorExtension implements BeforeEachCallback, AfterEachCallback {
    private PrintStream originalOut;
    private ByteArrayOutputStream out;

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        System.setOut(originalOut);
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        originalOut = System.out;
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    public String getOut() {
        return out.toString();
    }
}
