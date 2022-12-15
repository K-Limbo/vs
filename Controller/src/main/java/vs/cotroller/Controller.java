package vs.controller;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import vs.shared.*;

public class Controller {
    private UDPConnection udp_connection;
    private TCPConnection tcp_connection;
    private List<Worker> workers = new ArrayList<Worker>();
    private int expected_workers = 0;
    private int udp_port;
    private int tcp_port;
    private boolean workers_active = true;
    private Logger logger;

    public Controller() {
        expected_workers = Helper.getEnv("WORKER_COUNT", 5);
        udp_port = Helper.getEnv("CONTROLLER_UDP_PORT", 4000);
        tcp_port = Helper.getEnv("CONTROLLER_TCP_PORT", 4001);

        logger = new Logger("CONTROLLER");

        udp_connection = new UDPConnection(this);
        tcp_connection = new TCPConnection(this);
    }

    public boolean registerWorker(Worker worker) {
        if(workers.size() >= expected_workers) return false;
        if(!workers.contains(worker)) {
            workers.add(worker);
            logger.log("Worker " + workers.size() + " registered (" + worker.getIp() + ":" + worker.getPort() + ")");
        }
        return true;
    }

    public void registration(){
        registration(10000);
    }

    public void registration(long maxTime) {
        long start = System.currentTimeMillis();
        boolean shouldRun = true;

        while(shouldRun) {
            if(System.currentTimeMillis() - start > maxTime) {
                shouldRun = false;
            } else if (workers.size() >= expected_workers) {
                shouldRun = false;
            } else {
                udp_connection.receive(2000);
            }
        }

        logger.log("Registered " + workers.size() + " workers.");
    }

    public void ping() {
        logger.log("Pinging workers...");
        int counter = 1;
        long[] times = new long[workers.size()];
        for (Worker worker : workers) {
            long start = System.currentTimeMillis();

            udp_connection.send(worker, "ping");
            udp_connection.receive(2000);
            long end = System.currentTimeMillis();
            times[counter - 1] = end - start;
            logger.log("Ping " + counter + ": " + (end-start) + "ms");
            counter++;
        }
        logger.log("Average ping: " + Helper.average(times) + "ms");
    }

    public void alive() {
        while(workers_active) {
            for (Worker worker : workers) {
                if(!worker.canTryAgain()) continue;
                udp_connection.send(worker, "alive");
                udp_connection.receive(2000);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public void killWorkers() {
        workers_active = false;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("Error: " + e.getMessage());
        }
        logger.log("Killing workers...");
        for (Worker worker : workers) {
            udp_connection.send(worker, "kill");
        }
        workers.clear();
    }

    public void http() {
        while (workers_active) {
            tcp_connection.receive();
        }
    }

    public void shutdown() {
        logger.log("Shutting down...");
        udp_connection.close();
        tcp_connection.close();
    }

    public Worker getWorker(InetAddress ip, int port) {
        for(Worker worker : workers) {
            if(worker.getIp().equals(ip) && worker.getPort() == port) {
                return worker;
            }
        }
        return new Worker(ip, port);
    }

    public int getUdpPort() {
        return udp_port;
    }

    public int getTcpPort() {
        return tcp_port;
    }
}
