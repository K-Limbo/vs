package vs.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import vs.shared.*;

public class TCPConnection {
    private final Controller controller;
    private ServerSocket tcp_socket;

    private final Logger logger;

    public TCPConnection(Controller controller) {
        this.controller = controller;
        this.logger = new Logger("CONTROLLER-TCP");
        try {
            tcp_socket = new ServerSocket(controller.getTcpPort());
        } catch (IOException e) {
            logger.logError("Could not create TCP socket\n" + e.getMessage());
        }
    }

    public void receive() {
        try {
            Socket clientSocket = tcp_socket.accept();

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            StringBuilder request = new StringBuilder();
            String s;
            while ((s = in.readLine()) != null) {
                request.append(s);
                if (s.isEmpty()) {
                    break;
                }
            }

            String[] requestsLines = request.toString().split("\r\n");
            String[] requestLine = requestsLines[0].split(" ");
            String method = requestLine[0];
            String path = requestLine[1];

            process(method, path, clientSocket);
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            logger.logError("Could not receive TCP request\n" + e.getMessage());
        }
    }

    public void send(Socket socket, String message, int status, String contentType) {
        String NL = "\r\n"; //Line Separator String was shortened for convenience to NL -> New line

        logger.log(message + " (" + socket.getInetAddress().toString() + ":" + socket.getPort() + ")");

        try {
            OutputStream clientOutput = socket.getOutputStream();
            clientOutput.write(("HTTP/1.1 " + status + NL).getBytes());
            clientOutput.write(("Content-Type: " + contentType + NL).getBytes());
            clientOutput.write(NL.getBytes());
            clientOutput.write(message.getBytes());
            clientOutput.write( (NL + NL).getBytes() );
            clientOutput.flush();
            clientOutput.close();
        } catch (IOException e) {
            logger.logError("Could not send TCP response\n" + e.getMessage());
        }
    }

    private void process(String method, String path, Socket socket) {
        if(method.equals("GET")) {
            if(path.equals("/")) {
                Database db = new Database();
                String output = "Matrixmultiplikation: Matrix 1 * Matrix 2 = Ergebnis\n\n";
                output += "Matrix 1\n" + Helper.matrixToJson(db.get("matrix1")) + "\n\n";
                output += "Matrix 2\n" + Helper.matrixToJson(db.get("matrix2")) + "\n\n";
                output += "Ergebnis\n" + Helper.matrixToJson(db.get("result"));
                send(socket, output, 200, "application/json");
            } else {
                String paths[] = path.substring(1).split("/");
                if(paths.length == 1) {
                    if(paths[0].equals("matrix1") || paths[0].equals("matrix2") || paths[0].equals("result")) {
                        try {
                            Database db = new Database();
                            int[][] matrix = db.get(paths[0]);
                            String output = paths[0] + "\n" + Helper.matrixToJson(matrix);
                            send(socket, output, 200, "application/json");
                        } catch (Exception e) {
                            send(socket, "Invalid path", 404, "application/json");
                        }
                    } else {
                        send(socket, "Invalid path", 404, "application/json");
                    }
                } else if(paths.length == 2) {
                    if(paths[0].equals("matrix1") || paths[0].equals("matrix2") || paths[0].equals("result")) {
                        try {
                            Database db = new Database();
                            int[] matrix = db.get(paths[0], Integer.parseInt(paths[1]));
                            String output = paths[0] + " (" + paths[1] + "|X)\n" + Helper.matrixToJson(matrix);
                            send(socket, output, 200, "application/json");
                        } catch (Exception e) {
                            send(socket, "Invalid path", 404, "application/json");
                        }
                    } else {
                        send(socket, "Invalid path", 404, "text/plain");
                    }
                } else if(paths.length == 3) {
                    if(paths[0].equals("matrix1") || paths[0].equals("matrix2") || paths[0].equals("result")) {
                        try {
                            Database db = new Database();
                            int value = db.get(paths[0], Integer.parseInt(paths[1]), Integer.parseInt(paths[2]));
                            String output = paths[0] + " (" + paths[1] + "|" + paths[2] + ") = " + value;
                            send(socket, output, 200, "text/plain");
                        } catch (Exception e) {
                            send(socket, "Invalid path", 404, "application/json");
                        }
                    } else {
                        send(socket, "Invalid path", 404, "text/plain");
                    }
                } else {
                    send(socket, "Invalid path", 404, "text/plain");
                }
            }
        } else {
            send(socket, "405", 405, "text/plain");
        }
    }

    public void close() {
        try {
            tcp_socket.close();
        } catch (IOException e) {
            logger.logError("Could not close TCP socket\n" + e.getMessage());
        }
    }
}
