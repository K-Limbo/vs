package vs.worker;

import java.net.InetAddress;
import java.net.UnknownHostException;

import vs.shared.*;

public class Controller {

    private InetAddress ip;
    private final int udp_port;
    private final int tcp_port;

    Controller() {
        Logger logger = new Logger("WORKER-CONTROLLER");
        try {
            this.ip = InetAddress.getByName(Helper.getEnv("CONTROLLER_IP", "localhost"));
        } catch (UnknownHostException e) {
            logger.log("Unknown Host\n" + e.getMessage());
        }
        this.udp_port = Helper.getEnv("CONTROLLER_UDP_PORT", 5000);
        this.tcp_port = Helper.getEnv("CONTROLLER_TCP_PORT", 5001);
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getUdpPort() {
        return udp_port;
    }

    public int getTcpPort() {
        return tcp_port;
    }
}
