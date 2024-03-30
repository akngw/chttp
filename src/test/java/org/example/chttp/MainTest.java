package org.example.chttp;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(ExitTrapperExtension.class)
class MainTest {
    @RegisterExtension
    StandardOutCaptorExtension standardOutCaptorExtension = new StandardOutCaptorExtension();

    @RegisterExtension
    StandardErrorCaptorExtension standardErrorCaptorExtension = new StandardErrorCaptorExtension();

    @RegisterExtension
    MockWebServerExtension mockWebServerExtension = new MockWebServerExtension();

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
