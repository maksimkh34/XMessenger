import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Logger {
    FileWriter writer;

    public Logger(){
        try {
            writer = new FileWriter("LOGS.log",true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void Log(String message, LogLevel level) throws IOException {
        String msg = null;
        String ANSI_RED = "\u001B[31m";
        String ANSI_PURPLE = "\u001B[35m";
        String ANSI_YELLOW = "\u001B[33m";
        String RESET = "\033[0m";
        Date current = new Date();
        if (level == LogLevel.Info){
            msg = String.format("%s " + ANSI_PURPLE + "[%s]:" + RESET + " %s", current, level.toString(), message);
        } else if (level == LogLevel.Warning) {
            msg = String.format("%s " + ANSI_YELLOW + "[%s]:" + RESET + " %s", current, level.toString(), message);
        } else if (level == LogLevel.Error) {
            msg = String.format("%s " + ANSI_RED + "[%s]:" + RESET + " %s", current, level.toString(), message);
        }

        System.out.print('\n' + msg);
        msg = String.format("%s [%s]: %s", current, level.toString(), message);
        writer.write(msg);
        writer.append('\n');

        writer.flush();

    }
}
