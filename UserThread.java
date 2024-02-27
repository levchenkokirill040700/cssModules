import java.io.*;
import java.net.Socket;

public class UserThread extends Thread {
    private Socket socket;
    private Server server;
    private PrintWriter writer;

    public UserThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
            printUsers();

            String userName = reader.readLine();
            server.addUserName(userName);

            String serverMessage = "New user connected: " + userName;
            server.broadcast(serverMessage, this);

            String clientMessage;

            do {
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;
                server.broadcast(serverMessage, this);
            } while (!clientMessage.equals("quit"));

            server.removeUser(userName, this);
            socket.close();

            serverMessage = userName + " has quited.";
            server.broadcast(serverMessage, this);

        } catch (IOException e) {
            System.out.println("Error in UserThread: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void printUsers() {
        if (server.hasUser()) {
            writer.println("Connected user: " + server.getUserName());
        } else {
            writer.println("No other users connected");
        }
    }

    public void sendMessage(String serverMessage) {
        writer.println(serverMessage);
    }
}
