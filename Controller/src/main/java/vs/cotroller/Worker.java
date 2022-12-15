package vs.controller;
import java.net.InetAddress;
import vs.shared.*;

public class Worker {
    enum Status {NOT_REGISTERED, READY, WORKING, DEAD}
    private final InetAddress ip;
    private final int port;
    private Status status = Status.NOT_REGISTERED;
    private long lastUpdate = System.currentTimeMillis();
    private final Logger logger;

    public Worker(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;

        logger = new Logger("CONTROLLER-WORKER");
    }

    public boolean canTryAgain() {
        return System.currentTimeMillis() - lastUpdate >= 10000;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate() {
        lastUpdate = System.currentTimeMillis();
    }

    public void setStatus(Status status) {
        setLastUpdate();
        boolean found = false;
        for (Status s : Status.values()) {
            if (s == status) {
                this.status = status;
                found = true;
                break;
            }
        }
        if (!found) {
            logger.logError("Status " + status + " not found");
        }
    }

    public Status getStatus() {
        return status;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
