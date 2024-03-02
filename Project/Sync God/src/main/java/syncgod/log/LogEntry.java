package syncgod.log;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import syncgod.config.Config;
import syncgod.config.ConfigValue;

/**
 * Wrapper for a log entry.
 *
 * @author tj18b
 */
public class LogEntry {

    private static final DateTimeFormatter TIME_STAMP_FORMATTER
        = DateTimeFormatter.ofPattern("HH:mm:ss");
    private LocalTime playTime;
    private final String message;

    /**
     * Constructs an entry.
     * @param dateTime with pattern: "yyyy.M.d-HH.mm.ss"
     * @param entryText actual message of entry
     */
    public LogEntry(final String dateTime, final String entryText) {
        this.playTime = LocalTime
            .parse(dateTime.replace(".", ":"), TIME_STAMP_FORMATTER);
        this.message = entryText;
    }

    /**
     * Getter entry message.
     *
     * @return message from entry
     */
    public String message() {
        return message;
    }

    /**
     * Getter getTime.
     *
     * @return playHour from Entry
     */
    public LocalTime getTime() {
        return playTime;
    }

    /**
     * Calculates the first frame where the event occurs.
     *
     * @return frame
     */
    public Long getFrame() {
        return (long) (playTime.toSecondOfDay() * Config.get(ConfigValue.Fps));
    }

    /**
     * Setter for play time.
     *
     * @param time to set
     */
    public void setTime(final LocalTime time) {
        this.playTime = time;
    }

    /**
     * Returns message of log entry.
     *
     * @return LogEntry
     */
    public String toString() {
        return playTime.format(TIME_STAMP_FORMATTER) + " - " + message;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.playTime, this.message);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LogEntry)) {
            return false;
        }

        LogEntry entry = (LogEntry) obj;
        return entry.playTime.equals(this.playTime)
                && entry.message.equals(this.message);
    }
}
