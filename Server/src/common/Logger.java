package common;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.time.format.DateTimeFormatter;

public class Logger {
    static FileWriter writer;
    public Boolean skipFileWrite;
    // Regular text colors
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_RESET = "\033[0m";
    private static final String ANSI_BRIGHT_BG_YELLOW = "\u001B[43;1m";


    public Logger(Boolean skipFileWrite){
        try {
            this.skipFileWrite = skipFileWrite;
            if(this.skipFileWrite) return;
            LocalDateTime dateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm");
            writer = new FileWriter(formatter.format(dateTime) + ".log",true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void Log(String message, LogLevel level) {
        Date current = new Date();
        var method = Thread.currentThread().getStackTrace()[2];
        String methodName = "{" + method.getFileName() + ": " + method.getLineNumber() + "} " + method.getMethodName();
        String formattedMessage = formatMessage(current, methodName, level, message);
        String consoleMessage = formatConsoleMessage(current, methodName, level, message);

        System.out.print('\n' + consoleMessage);
        if(this.skipFileWrite) return;
        try {
            writer.write(formattedMessage);
            writer.append('\n');
            writer.flush();
        } catch (IOException ignored) {}
    }

    private String formatMessage(Date current, String methodName, LogLevel level, String message) {
        return String.format("%s (%s) [%s]: %s", current, methodName, level, message);
    }

    private String formatConsoleMessage(Date current, String methodName, LogLevel level, String message) {
        String color = getColorForLevel(level);
        String msg = String.format("%s%s%s %s%s%s %s%s%s\t%s%s",
                ANSI_CYAN, current, ANSI_RESET,
                ANSI_PURPLE, "(" + methodName + ")", ANSI_RESET,
                color, "[" + level + "]:", ANSI_RESET,
                message, ANSI_RESET);
        if(level == LogLevel.Info) msg = msg.replace("\t", "\t\t");
        return msg;
    }

    private String getColorForLevel(LogLevel level) {
        return switch (level) {
            case Info -> ANSI_GREEN;
            case Warning -> ANSI_YELLOW;
            case Error -> ANSI_RED;
            case Critical -> ANSI_BRIGHT_BG_YELLOW + ANSI_RED;
            case Other -> ANSI_BLUE;
        };
    }
}
