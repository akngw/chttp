package org.example.chttp;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class Main {
    public static final String PROGRAM_NAME = "cHTTP";
    public static final int EX_OK = 0;
    public static final int EX_USAGE = 64;
    public static final int BUFFER_SIZE = 8192;

    public static void main(@NotNull String[] args) throws ParseException, IOException {
        RequestOptions requestOptions = RequestOptions.fromArguments(args);
        if (requestOptions.isRequestedToPrintHelp()) {
            printHelp(requestOptions);
            System.exit(EX_OK);
        }
        if (requestOptions.getUrl() == null) {
            printNoUrlSpecified();
            System.exit(EX_USAGE);
        }
        request(requestOptions);
        System.exit(EX_OK);
    }

    private static void request(@NotNull RequestOptions requestOptions) throws IOException {
        String url = requestOptions.getUrl();
        Request.Builder builder = new Request.Builder().url(url);
        Map<String, String> headers = requestOptions.getHeaders();
        if (headers != null) {
            headers.forEach(builder::addHeader);
        }
        Request request = builder.build();
        try (Response response = new OkHttpClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response " + response);
            }
            ResponseBody body = response.body();
            assert body != null;
            String output = requestOptions.getOutput();
            if (output == null) {
                outputResponseBody(body, System.out);
            } else {
                try (OutputStream out = Files.newOutputStream(Paths.get(output))) {
                    outputResponseBody(body, out);
                }
            }
        }
    }

    private static void outputResponseBody(ResponseBody body, OutputStream out) throws IOException {
        try (BufferedSource source = body.source()) {
            byte[] buffer = new byte[BUFFER_SIZE];
            while (!source.exhausted()) {
                int bytesRead = source.read(buffer);
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    private static void printNoUrlSpecified() {
        System.err.printf("%s: no URL specified%n", PROGRAM_NAME);
        System.err.printf("%s: try '--help' option for more information%n", PROGRAM_NAME);
    }

    private static void printHelp(@NotNull RequestOptions requestOptions) {
        HelpFormatter formatter = new HelpFormatter();
        Options options = requestOptions.getOptions();
        formatter.printHelp("java -jar /path/to/chttp.jar [<options> ...] <url>", options);
    }
}
