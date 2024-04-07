package org.example.chttp;

import lombok.Getter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class RequestOptions {
    @NotNull
    private final Options options;
    private final String url;
    private final boolean isRequestedToPrintHelp;
    private final String output;
    private final Map<String, String> headers;

    @NotNull
    public static RequestOptions fromArguments(@NotNull String[] args) throws ParseException {
        Options options = createOptions();
        CommandLine cl = parseCommandLine(args, options);
        return from(cl, options);
    }

    @NotNull
    private static RequestOptions from(@NotNull CommandLine commandLine, @NotNull Options options) {
        List<String> argList = commandLine.getArgList();
        String urlString = argList.stream().findFirst().orElse(null);
        boolean isRequestedToShowHelp = commandLine.hasOption("help");
        String output = null;
        if (commandLine.hasOption("output")) {
            output = commandLine.getOptionValue("output");
        }
        Map<String, String> headers = null;
        if (commandLine.hasOption("header")) {
            headers = headersFrom(commandLine.getOptionValues("header"));
        }
        return new RequestOptions(options, urlString, isRequestedToShowHelp, output, headers);
    }

    private static Map<String, String> headersFrom(String[] headers) {
        Map<String, String> map = new HashMap<>();
        for (String header : headers) {
            String[] parts = header.split(":\\s*", 2);
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid header specified");
            }
            map.put(parts[0], parts[1]);
        }
        return map;
    }

    private static CommandLine parseCommandLine(@NotNull String[] args,
                                                @NotNull Options options) throws ParseException {
        return new DefaultParser().parse(options, args);
    }

    @NotNull
    private static Options createOptions() {
        Options options = new Options();
        options.addOption("h", "help", false, "shows this help.");
        options.addOption("o", "output", true, "writes output to given file. Existing file will be overwritten.");
        options.addOption("H", "header", true, "adds a header to the request headers.");
        return options;
    }

    public RequestOptions(@NotNull Options options,
                          String url,
                          boolean isRequestedToPrintHelp,
                          String output,
                          Map<String, String> headers) {
        this.options = options;
        this.url = url;
        this.isRequestedToPrintHelp = isRequestedToPrintHelp;
        this.output = output;
        this.headers = headers;
    }
}
