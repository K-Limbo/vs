package vs.shared;

import java.text.SimpleDateFormat;

public class Logger {
    private final String prefix;
    private final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");

    public Logger(String prefix){
        this.prefix = prefix;
    }

    public void log(String logMessage){
        System.out.println("[" + prefix + ", " + formatter.format(System.currentTimeMillis()) + "] " + logMessage);
    }

    public void logError(String errorMessage){
        System.err.println("[" + prefix + "-" + "ERROR" + "] " + errorMessage);
    }

}
