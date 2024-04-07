package org.example.chttp;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyArray;
import static org.junit.jupiter.api.Assertions.*;

class RequestOptionsTest {

    @Test
    @DisplayName("--headerオプションが指定された場合その値が取得できる")
    void whenHeaderOptionSpecified() {
        RequestOptions requestOptions = null;
        try {
            requestOptions = RequestOptions.fromArguments(new String[]{"--header", "MyHeader: my header value"});
        } catch (ParseException e) {
            fail();
        }
        Map<String, String> headers = requestOptions.getHeaders();
        assertThat(headers.get("MyHeader"), is("my header value"));
    }

    @Test
    @DisplayName("--headerオプションが複数指定された場合その値が取得できる")
    void whenMultipleHeaderOptionsSpecified() {
        RequestOptions requestOptions = null;
        try {
            requestOptions = RequestOptions.fromArguments(
                    new String[]{"--header", "MyHeader1: my header value 1", "-H", "MyHeader2: my header value 2"});
        } catch (ParseException e) {
            fail();
        }

        Map<String, String> headers = requestOptions.getHeaders();
        assertThat(headers.get("MyHeader1"), is("my header value 1"));
        assertThat(headers.get("MyHeader2"), is("my header value 2"));
    }

    @Test
    @DisplayName("--headerオプションが指定されない場合その値はnull")
    void whenHeaderOptionNotSpecified() {
        RequestOptions requestOptions = null;
        try {
            requestOptions = RequestOptions.fromArguments(new String[0]);
        } catch (ParseException e) {
            fail();
        }
        assertThat(requestOptions.getHeaders(), nullValue());
    }
}
