package org.example.chttp.extension;

import lombok.Getter;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

@Getter
public class MockWebServerExtension implements BeforeEachCallback, AfterEachCallback {
    private MockWebServer server;

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        server.shutdown();
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        server = new MockWebServer();
        server.start();
    }
}
