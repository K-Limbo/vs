package vs.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import vs.shared.*;

public class Main {
    public static Map<String, int[][]> matrices = new HashMap<>();
    public static final int MAX_MATRIX_SIZE = 25;
    public static final int MAX_MATRIX_VALUE = 100;
    public static void main(String[] args) {
        generateMatrix();
        Controller controller = new Controller();

        controller.registration();
        controller.ping();

        new Thread(controller::alive).start();

        new Thread(controller::http).start();

        //controller.killWorkers();
        //controller.shutdown();
    }

    public static void generateMatrix() {
        int rand1 = (int) Math.max(Math.random() * MAX_MATRIX_SIZE, 1);
        int rand2 = (int) Math.max(Math.random() * MAX_MATRIX_SIZE, 1);
        int rand3 = (int) Math.max(Math.random() * MAX_MATRIX_SIZE, 1);

        matrices.put("matrix1", new int[rand1][rand2]);
        matrices.put("matrix2", new int[rand2][rand3]);
        matrices.put("result", new int[rand1][rand3]);

        for (int i = 0; i < rand1; i++) {
            for (int j = 0; j < rand2; j++) {
                matrices.get("matrix1")[i][j] = (int) Math.max(Math.random() * MAX_MATRIX_VALUE, 1);
            }
        }

        for (int i = 0; i < rand2; i++) {
            for (int j = 0; j < rand3; j++) {
                matrices.get("matrix2")[i][j] = (int) Math.max(Math.random() * MAX_MATRIX_VALUE, 1);
            }
        }
    }
}