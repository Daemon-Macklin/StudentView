import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

public class Server {

    private static JTextArea display = new JTextArea();

    public static void main(String[] args){
        Server server = new Server();

        server.startServer();
    }

    private void startServer() {
        JFrame frame = new JFrame("Student View Server");
        frame.add(display);
        frame.pack();
        frame.setSize(400, 400);
        frame.setVisible(true);

        try {
            ServerSocket serverSocket = new ServerSocket(8000);
            display.append("Server started at" + new Date() + "\n");

            while (true) {
                Socket newSocket = serverSocket.accept();
                ClientHandler newClient = new ClientHandler(newSocket, display);
                newClient.start();
                display.append("New Client at " + newSocket.toString() + "\n");
            }
        } catch (Exception e){
            System.out.println(e);
        }
    }
}

 class ClientHandler extends Thread {

    private Socket socket;

    private InetAddress address;

     private DataInputStream inputFromClient;
     private DataOutputStream outputToClient;

     private JTextArea display;

    public ClientHandler(Socket socket, JTextArea display) throws IOException {
        this.inputFromClient = new DataInputStream(socket.getInputStream());
        this.outputToClient = new DataOutputStream(socket.getOutputStream());
        this.socket = socket;
        this.address = socket.getInetAddress();
        this.display = display;
    }

    public void run() {

        DBController dbController = new DBController();
        dbController.connectToMySql();

        try{
            String response = "";
            String message = inputFromClient.readUTF();
            System.out.println(message);
            String[] data = message.split(",");
            System.out.println(data);

            if(data[0].equals("login")){
                writeMessage("Sign in request");
                ResultSet result = dbController.login(data[1]);
                System.out.println(result);
                if(result != null){
                    result.first();
                    response = result.getString("UNAME");
                    writeMessage(response + " signed in");
                } else {
                    writeMessage("Sign in Failed");
                    response = "Invalid UID";
                }
            } else if(data[0].equals("search")){
                outputToClient.writeUTF("Search");
            }

            outputToClient.writeUTF(response);
        } catch (Exception e){
            System.out.println(e);
        }
    }

    private void writeMessage(String message) {
        display.append(new Date() + ":" + message + ", From: "+ this.address + "\n");
    }
}

class DBController {

    private Connection dbCon;

    public void connectToMySql() {

        Connection con;

        // Try to connect
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();

            // Try to connect to the "test" database with the GMT timezone with user name root and no password
            con = DriverManager.getConnection("jdbc:mysql://localhost/studentDataBase?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT", "root", "");

            // Check if it is connected
            if (!con.isClosed())

                // If connect print to console connected
                System.out.println("Connected to mysql server!");

            // return the connection object
            dbCon = con;

        } catch (Exception e) {

            // if it fails print the error to the console, inform the user and then return null
            System.err.println("Exception " + e.getMessage());
        }
    }

    public ResultSet login(String userid){

        ResultSet rs = null;
        try {
            PreparedStatement pstmt = dbCon.prepareStatement("SELECT * FROM users WHERE UID=?");

            pstmt.setString(1, userid);

            rs = pstmt.executeQuery();
            if(!rs.isBeforeFirst()) {
                rs = null;
            }
        } catch (Exception e){
            System.out.println(e);
        }

        return rs;
    }
}