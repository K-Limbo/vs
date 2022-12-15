package vs.worker;
import vs.shared.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPConnection {
    private DatagramSocket socket;
    private final Controller controller;
    private final Worker worker;

    private final Logger logger;

    UDPConnection(Worker worker, Controller controller) {
        this.worker = worker;
        this.controller = controller;
        this.logger = new Logger("WORKER-UDP");
        try {
            this.socket = new DatagramSocket();
        } catch (SocketException e) {
            logger.logError("Could not create UDP socket\n" + e.getMessage());
        }
    }

    public void receive(int timeout) {
        byte[] buffer = new byte[1024];
        DatagramPacket dp = new DatagramPacket(buffer, buffer.length);

        try {
            socket.setSoTimeout(Math.max(timeout, 0));
            socket.receive(dp);
        } catch(Exception e){
            logger.logError("Could not receive UDP packet\n" + e.getMessage());
        }

        String message = new String(dp.getData(), 0, dp.getLength());
        logger.log(message + " (" + dp.getAddress() + ":" + dp.getPort() + ")");

        process(message);
    }

    public void send(String message) {
        logger.log(message + " (" + controller.getIp().toString() + ":" + controller.getUdpPort() + ")");

        DatagramPacket dp = new DatagramPacket(message.getBytes(), message.length(), controller.getIp(), controller.getUdpPort());
        try {
            socket.send(dp);
        } catch (IOException e) {
            logger.logError("Could not send UDP message\n" + e.getMessage());
        }
    }

    private void process(String message) {
        String[] messageParts = message.split(" ");
        switch (messageParts[0]) {
            case "registered":
                worker.setStatus(Worker.Status.READY);
                break;
            case "ping":
                send("pong");
                break;
            case "alive":
                send("alive " + worker.getStatus());
                break;
            case "kill":
                worker.setStatus(Worker.Status.DEAD);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Error: " + e.getMessage());
                }
                System.exit(0);
                break;
            default:
                logger.logError("Unknown message: " + message);
                break;
        }
    }
}
