package org.example.chttp;

import okhttp3.Headers;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.commons.cli.ParseException;
import org.example.chttp.extension.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(ExitTrapperExtension.class)
class MainTest {
    @NotNull
    @RegisterExtension
    StandardOutCaptorExtension standardOutCaptorExtension = new StandardOutCaptorExtension();

    @NotNull
    @RegisterExtension
    StandardErrorCaptorExtension standardErrorCaptorExtension = new StandardErrorCaptorExtension();

    @NotNull
    @RegisterExtension
    MockWebServerExtension mockWebServerExtension = new MockWebServerExtension();

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("URLが指定された場合そのURLへ接続しレスポンスを出力する")
    void whenUrlIsSpecified() {
        MockWebServer server = mockWebServerExtension.getServer();
        String url = server.url("/hello/world").toString();
        server.enqueue(new MockResponse().setBody("こんにちは世界"));
        try {
            Main.main(new String[]{url});
        } catch (ExitException e) {
            assertThat(e.getStatus(), is(equalTo(0)));
        } catch (ParseException | IOException e) {
            fail();
        }
        String out = standardOutCaptorExtension.getOut();
        assertThat(out, containsString("こんにちは世界"));
        String err = standardErrorCaptorExtension.getErr();
        assertThat(err, is(emptyString()));
    }

    @Test
    @DisplayName("--outputオプションが指定された場合指定されたファイルに出力する")
    void whenOutputOptionSpecified() {
        MockWebServer server = mockWebServerExtension.getServer();
        String url = server.url("/hello/world").toString();
        String exampleBody = "こんにちは世界";
        server.enqueue(new MockResponse().setBody(exampleBody));
        Path outputPath = tempDir.resolve("test.txt");
        try {
            Main.main(new String[]{"--output", outputPath.toString(), url});
        } catch (ExitException e) {
            assertThat(e.getStatus(), is(equalTo(0)));
        } catch (ParseException | IOException e) {
            fail();
        }
        String actualOutput = null;
        try {
            actualOutput = new String(Files.readAllBytes(outputPath));
        } catch (IOException e) {
            fail();
        }
        assertThat(actualOutput, is(exampleBody));
        String out = standardOutCaptorExtension.getOut();
        assertThat(out, is(emptyString()));
        String err = standardErrorCaptorExtension.getErr();
        assertThat(err, is(emptyString()));
    }

    @Test
    @DisplayName("--headerオプションが指定された場合リクエストヘッダを追加する")
    void whenHeaderOptionSpecified() {
        MockWebServer server = mockWebServerExtension.getServer();
        String url = server.url("/hello/world").toString();
        String exampleBody = "こんにちは世界";
        server.enqueue(new MockResponse().setBody(exampleBody));
        try {
            Main.main(new String[]{"--header", "MyHeader: My header value", url});
        } catch (ExitException e) {
            assertThat(e.getStatus(), is(equalTo(0)));
        } catch (ParseException | IOException e) {
            fail();
        }
        String out = standardOutCaptorExtension.getOut();
        assertThat(out, is(exampleBody));
        String err = standardErrorCaptorExtension.getErr();
        assertThat(err, is(emptyString()));
        RecordedRequest request = null;
        try {
            request = server.takeRequest();
        } catch (InterruptedException e) {
            fail();
        }
        Headers headers = request.getHeaders();
        assertThat(headers.get("MyHeader"), is("My header value"));
    }

    @Test
    @DisplayName("--headerオプションに不正な値が指定された場合例外が発生する")
    void whenInvalidHeaderOptionSpecified() {
        MockWebServer server = mockWebServerExtension.getServer();
        String url = server.url("/hello/world").toString();
        String exampleBody = "こんにちは世界";
        server.enqueue(new MockResponse().setBody(exampleBody));
        try {
            Main.main(new String[]{"--header", "Invalid header string", url});
        } catch (ExitException | ParseException | IOException e) {
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
        }
    }

    @Test
    @DisplayName("引数がない場合エラーを出力する")
    void whenNoCommandLineArgumentsProvided() {
        try {
            Main.main(new String[0]);
        } catch (ExitException e) {
            assertThat(e.getStatus(), is(equalTo(64)));// 64 means EX_USAGE
        } catch (ParseException | IOException e) {
            fail();
        }
        String out = standardOutCaptorExtension.getOut();
        assertThat(out, is(emptyString()));
        String err = standardErrorCaptorExtension.getErr();
        assertThat(err, containsString("no URL specified"));
    }

    @Test
    @DisplayName("--helpオプションが指定された場合ヘルプを出力する")
    void whenHelpOptionSpecified() {
        try {
            Main.main(new String[]{"--help"});
        } catch (ExitException e) {
            assertThat(e.getStatus(), is(equalTo(0)));
        } catch (ParseException | IOException e) {
            fail();
        }
        String out = standardOutCaptorExtension.getOut();
        assertThat(out, containsString("java -jar /path/to/chttp.jar [<options> ...] <url>"));
        String err = standardErrorCaptorExtension.getErr();
        assertThat(err, is(emptyString()));
    }
}
