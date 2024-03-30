package org.example.chttp;

import lombok.NonNull;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Main {
    public static final String PROGRAM_NAME = "cHTTP";
    public static final int EX_OK = 0;
    public static final int EX_USAGE = 64;

    public static void main(String[] args) throws ParseException, IOException {
        RequestOptions requestOptions = RequestOptions.fromArguments(args);
        if(requestOptions.isRequestedToPrintHelp()) {
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

    private static void request(RequestOptions requestOptions) throws IOException {
        String url = requestOptions.getUrl();
        Request request = new Request.Builder().url(url).build();
        try (Response response = new OkHttpClient().newCall(request).execute()) {
            assert response.body() != null;
            System.out.print(response.body().string());
        }
    }

    private static void printNoUrlSpecified() {
        System.err.printf("%s: no URL specified%n", PROGRAM_NAME);
        System.err.printf("%s: try '--help' option for more information%n", PROGRAM_NAME);
    }

    private static void printHelp(@NotNull @NonNull RequestOptions requestOptions) {
        HelpFormatter formatter = new HelpFormatter();
        Options options = requestOptions.getOptions();
        formatter.printHelp("java -jar /path/to/chttp.jar [<options> ...] <url>", options);
    }
}
