package vs.shared;

/** EnvReader
 *  A Simple Helper Function to read from Environment Variables.
 */

public class Helper {

    /*
     *  Get environment variable or default value
     */

    public static String getEnv(String name, String defaultValue){
        String value = System.getenv(name);
        if(value == null) return defaultValue;
        return value;
    }

    public static int getEnv(String name, int defaultValue){
        String value = System.getenv(name);
        if(value == null) return defaultValue;
        return Integer.parseInt(value);
    }
    public static long average(long[] array) {
        long sum = 0;
        for (long l : array) {
            sum += l;
        }
        return sum / array.length;
    }

    public static String matrixToJson(int[] array) {
        String output = "[";
        for(int i = 0; i < array.length; i++) {
            output += array[i];
            if(i < array.length - 1) {
                output += " ";
            }
        }
        output += "]";
        return output;
    }

    public static String matrixToJson(int[][] array) {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < array.length; i++) {
            json.append("\t[");
            for (int j = 0; j < array[i].length; j++) {
                json.append(array[i][j]);
                if (j < array[i].length - 1) {
                    json.append(" ");
                }
            }
            json.append("]\n");
        }
        json.append("\n]");
        return json.toString();
    }
}