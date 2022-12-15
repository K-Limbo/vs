package vs.controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import vs.shared.*;

public class UDPConnection {
    private DatagramSocket socket;
    private final Controller controller;
    private final Logger logger;

    public UDPConnection(Controller controller) {
        this.controller = controller;
        this.logger = new Logger("CONTROLLER-UDP");

        try {
            socket = new DatagramSocket(controller.getUdpPort());
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

            String message = new String(dp.getData(), 0, dp.getLength());
            logger.log(message + " (" + dp.getAddress().toString() + ":" + dp.getPort() + ")");

            process(message, controller.getWorker(dp.getAddress(), dp.getPort()));
        } catch(IOException e){
            logger.logError("Could not receive UDP request\n" + e.getMessage());
        }
    }

    public void send(Worker worker, String message) {
        logger.log(message + " (" + worker.getIp() + ":" + worker.getPort() + ")");

        DatagramPacket dp = new DatagramPacket(message.getBytes(), message.length(), worker.getIp(), worker.getPort());
        try {
            socket.send(dp);
        } catch (IOException e) {
            logger.logError("Could not send UDP request\n" + e.getMessage());
        }
    }

    public void process(String message, Worker worker) {
        String[] messageParts = message.split(" ");
        switch (messageParts[0]) {
            case "register":
                if(controller.registerWorker(worker)) {
                    send(worker, "registered");
                    worker.setStatus(Worker.Status.READY);
                } else {
                    send(worker, "die");
                }
                break;
            case "pong":
                worker.setLastUpdate();
                break;
            case "alive":
                worker.setStatus(Worker.Status.valueOf(messageParts[1]));
                break;
            default:
                logger.logError("Unknown message: " + message);
        }
    }

    public void close() {
        socket.close();
    }
}
