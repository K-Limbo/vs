package vs.worker;
import vs.shared.*;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller();
        Worker worker = new Worker(controller);

        worker.register();
        worker.listenUdp();
    }
}