package vs.controller;

import java.io.*;
import vs.shared.*;
public class Database {
    private final Logger logger;
    private final File matrix1;
    private final File matrix2;
    private final File result;
    public Database() {
        logger = new Logger("CONTROLLER-DATABASE");
        matrix1 = new File("matrix1.csv");
        matrix2 = new File("matrix2.csv");
        result = new File("result.csv");

        fillFile(matrix1, Main.matrices.get("matrix1"));
        fillFile(matrix2, Main.matrices.get("matrix2"));
        fillFile(result, Main.matrices.get("result"));
    }

    public int[][] get(String file) {
        if (!file.equals("matrix1") && !file.equals("matrix2") && !file.equals("result")) throw new IllegalArgumentException("File not found");

        int matrix[][] = new int[Main.matrices.get(file).length][Main.matrices.get(file)[0].length];
        try (BufferedReader br = new BufferedReader(new FileReader(file + ".csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                matrix[Integer.parseInt(values[0])][Integer.parseInt(values[1])] = Integer.parseInt(values[2]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return matrix;
    }

    public int[] get(String file, int key) {
        if (!file.equals("matrix1") && !file.equals("matrix2") && !file.equals("result")) throw new IllegalArgumentException("File not found");
        if(key < 0 || key >= Main.matrices.get(file).length) throw new IllegalArgumentException("Key not found");

        int[] matrix = new int[Main.matrices.get(file)[0].length];

        try (BufferedReader br = new BufferedReader(new FileReader(file + ".csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (Integer.parseInt(values[0]) == key) {
                    matrix[Integer.parseInt(values[1])] = Integer.parseInt(values[2]);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return matrix;
    }

    public int get(String file, int key_1, int key_2) {
        if (!file.equals("matrix1") && !file.equals("matrix2") && !file.equals("result")) throw new IllegalArgumentException("File not found");
        if(key_1 < 0 || key_1 >= Main.matrices.get(file).length || key_2 < 0 || key_2 >= Main.matrices.get(file)[0].length) throw new IllegalArgumentException("Key not found");

        try (BufferedReader br = new BufferedReader(new FileReader(file + ".csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (Integer.parseInt(values[0]) == key_1 && Integer.parseInt(values[1]) == key_2) {
                    return Integer.parseInt(values[2]);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalArgumentException("Key not found");
    }

    public void result(int key_1, int key_2, int value) {
        if(key_1 < 0 || key_1 >= Main.matrices.get("result").length || key_2 < 0 || key_2 >= Main.matrices.get("result")[0].length) throw new IllegalArgumentException("Key not found");
        try {
            PrintWriter writer = new PrintWriter(result);

            writer.println(key_1 + "," + key_2 + "," + value);

            writer.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void fillFile(File file, int[][] matrix) {
        try {
            PrintWriter writer = new PrintWriter(file);

            if(file != result) {
                for (int i = 0; i < matrix.length; i++) {
                    for (int j = 0; j < matrix[i].length; j++) {
                        writer.println(i + "," + j + "," + matrix[i][j]);
                    }
                }
            }

            writer.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
