package syncgod.log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Parsing a GCC Log (with a defined pattern) to entries.
 *
 * @author tj18b
 */
public final class LogParser {

    /**
     * Private constructor.
     */
    private LogParser() {
    }

    /**
     * Expected pattern of the log.
     */
    private static final Pattern LOG_PATTERN
            = Pattern.compile("\\d{4}\\.\\d{1,2}\\.\\d{1,2}-(\\d{2}\\.\\d{2}\\.\\d{2}):\\s(.*)");

    /**
     * Reads a file, parsing the content to a list of LogEntry.
     *
     * @param path File path to GCC-Log
     * @return LogEntry List
     * @throws IOException when an input or output exception occurs
     */
    public static List<LogEntry> parse(final String path) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(path))) {
            return lines.map(LOG_PATTERN::matcher)
                    .filter(Matcher::matches)
                    .map(LogParser::toLogEntry)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Creates a new LogEntry from given Match.
     *
     * @param matcher from pattern
     * @return LogEntry
     */
    private static LogEntry toLogEntry(final Matcher matcher) {
        return new LogEntry(matcher.group(1), matcher.group(2));
    }
}
