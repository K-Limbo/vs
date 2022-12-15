package vs.worker;
import vs.shared.*;

public class Worker {

    enum Status {NOT_REGISTERED, READY, WORKING, DEAD}
    private final UDPConnection udp_connection;
    private final TCPConnection tcp_connection;
    private Status status = Status.NOT_REGISTERED;

    private final Logger logger;

    public Worker(Controller controller) {
        this.udp_connection = new UDPConnection(this, controller);
        this.tcp_connection = new TCPConnection(controller);
        this.logger = new Logger("WORKER");
    }

    public void register() {
        while (status.equals(Status.NOT_REGISTERED)) {
            udp_connection.send("register");
            udp_connection.receive(1000);
        }
    }

    public void listenUdp() {
        while (!status.equals(Status.DEAD)) {
            udp_connection.receive(0);
        }
    }
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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
}
