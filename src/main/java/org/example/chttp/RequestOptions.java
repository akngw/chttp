package org.example.chttp;

import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class RequestOptions {
    private final Options options;
    private final String url;
    private final boolean isRequestedToPrintHelp;

    @NotNull
    public static RequestOptions fromArguments(@NotNull @NonNull String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("h", "help", false, "show this help.");
        DefaultParser parser = new DefaultParser();
        CommandLine cl = parser.parse(options, args);
        List<String> argList = cl.getArgList();
        String urlString = null;
        if (!argList.isEmpty()) {
            urlString = argList.get(0);
        }
        boolean isRequestedToShowHelp = cl.hasOption("help");
        return new RequestOptions(options, urlString, isRequestedToShowHelp);
    }

    public RequestOptions(@NotNull @NonNull Options options, String url, boolean isRequestedToPrintHelp) {
        this.options = options;
        this.url = url;
        this.isRequestedToPrintHelp = isRequestedToPrintHelp;
    }
}
