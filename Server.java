/**
 * author Daemon Macklin
 */

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.Date;

/**
 * Class to Handle requests from the clients
 */
public class Server {

    // Text area to display incoming requests and out going responses
    private static JTextArea display = new JTextArea();

    public static void main(String[] args){
        Server server = new Server();
        server.startServer();
    }

    /**
     * Method to start the server
     */
    private void startServer() {

        // Show the gui
        JFrame frame = new JFrame("Student View Server");
        frame.add(display);
        frame.pack();
        frame.setSize(800, 400);
        frame.setVisible(true);

        try {

            // Start the server on port 8000
            ServerSocket serverSocket = new ServerSocket(8000);
            display.append("Server started at" + new Date() + "\n");

            // Infinite Loop waiting for clients to attempt to connect
            while (true) {

                // Wait for a connection
                Socket newSocket = serverSocket.accept();

                // When a client connects create a new thread
                ClientHandler newClient = new ClientHandler(newSocket, display);

                // Start the thread
                newClient.start();

                // Log the new thread
                display.append("New Client at " + newSocket.toString() + "\n");
            }
        } catch (Exception e){
            System.out.println(e);
        }
    }
}

/**
 * Threaded class to handle client requests
 */
 class ClientHandler extends Thread {

    private Socket socket;
    private InetAddress address;
    private DataInputStream inputFromClient;
    private DataOutputStream outputToClient;
    private JTextArea display;
    private DBController dbController;

    /**
     * Class constructor
     * @param socket connection socket used to communicate with client
     * @param display textarea info is logged to
     * @throws IOException
     */
    public ClientHandler(Socket socket, JTextArea display) throws IOException {
        this.inputFromClient = new DataInputStream(socket.getInputStream());
        this.outputToClient = new DataOutputStream(socket.getOutputStream());
        this.socket = socket;
        this.address = socket.getInetAddress();
        this.display = display;

        // Connect to the database when the thread is created
        this.dbController = new DBController();
        dbController.connectToMySql();
    }

    /**
     * Main run method to listen for an handle client requests
     */
    public void run() {

        // Infinite Loop to listen for user requests
        while (true) {
            try {

                // Create a String we will return to the user
                String response = "";

                // Listen out for the clients requests which will be comma separated values
                String message = inputFromClient.readUTF();

                // Split the comma separated values
                String[] data = message.split(",");


                // Check the first item in the list
                if (data[0].equals("login")) {

                    // If it is login it is a login request
                    // Log the attempt
                    writeMessage("Sign in request");

                    // Call the database login method with the user id
                    ResultSet loginResult = dbController.login(data[1]);

                    // Check if the result is null
                    if (loginResult != null) {

                        // If it is not there is a user with that id
                        loginResult.first();

                        // Respond with the username - this means successful login - and log the sign in
                        response = loginResult.getString("UNAME");
                        writeMessage(response + " signed in");
                    } else {

                        // If it is null there is no user with that UID
                        writeMessage("Sign in Failed");
                        response = "Invalid UID";
                    }

                } else if (data[0].equals("search")) {

                    // If it is search the client wants to search for users
                    writeMessage("Searching Users");

                    // Call the database search method with
                    ResultSet searchUsers = dbController.search(data[1], data[2]);

                    // Check if result is null
                    if(searchUsers != null){

                        // If it is not then there is data to encode before sending
                        String searchData = encodeSQLData(searchUsers);

                        // If the encoded data is not null
                        if(searchData != null){

                            // Set the response to be the encoded data
                            response = searchData;

                            // Log the search
                            writeMessage("Retuning Search Data");

                        } else{

                            // Else there was an error encoding the data, tell the client and log the error
                            response = "Error Searching User Data";
                            writeMessage("Error Searching Data");
                        }
                    } else{

                        // Else there was no users, tell the client, log the search
                        response = "No User data";
                        writeMessage("No User data");
                    }

                } else if (data[0].equals("allUserData")) {

                    // If it is allUserData the client wants all of the user data
                    writeMessage("Get all User data");

                    // Call the database get all usera function
                    ResultSet allUsers = dbController.getAllUserData();

                    // Check if the result is null
                    if (allUsers != null) {

                        // If it is not there is data to encode it before sending
                        String userData = encodeSQLData(allUsers);

                        // Check if the result is null
                        if (userData != null) {

                            // If it is not send the client the encoded data and log the search
                            response = userData;
                            writeMessage("Returning User Data");
                        } else {

                            // Else tell the client and log the error
                            response = "Error Getting User Data";
                            writeMessage("Error Getting Data");
                        }
                    } else {

                        // If it is null there is not data, tell the user and log
                        response = "No User data";
                        writeMessage("No User data");
                    }
                } else {

                    // Any other situation is a 404, tell the client and log the error
                    response = "404";
                    writeMessage("404");
                }

                // Send the response to the client
                outputToClient.writeUTF(response);
            }catch (IOException ex) {

                // If the user disconnects log the disconnection and kill the thread
                writeMessage("Connection Terminated");
                this.interrupt();
            }catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    // Method to write log messages with the date and source on it
    private void writeMessage(String message) {
        display.append(new Date() + ":" + message + ", From: "+ this.address + "\n");
    }

    /**
     * Method to encode a result set into a comma separated string string so it can be sent with a dataoutputstream
     * @param rs result set to convert
     * @return The converted string ready for sending
     */
    private String encodeSQLData(ResultSet rs) {

        // Set the first item in the string to be userData so the client can tell there is data coming
        String str = "userData,";

        try {
            // For each item in the result set, add each of the database fields to the string separated by commas
            rs.first();
            do {
                str += rs.getInt("SID") + ",";
                str += rs.getInt("STUD_ID") + ",";
                str += rs.getString("FNAME") + ",";
                str += rs.getString("SNAME") + ",";
            } while (rs.next());

            // Return the string
            return str;
        } catch (Exception e) {
            System.out.println(e);
        }

        // If there is an error return null
        return null;
    }
}

/**
 * Class to manage the connection between the server and the xxamp database
 */
class DBController {

    private Connection dbCon;

    /**
     * Method to connect to the database
     */
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

    /**
     * Method to login the user
     * @param userid userid the client entered
     * @return A reuslt set if there is a user to login, null if no user if found
     */
    public ResultSet login(String userid){

        ResultSet rs;
        try {

            // Create a prepared statement
            PreparedStatement pstmt = dbCon.prepareStatement("SELECT * FROM users WHERE UID=?");

            // Add the userid
            pstmt.setString(1, userid);

            // Execute the querry
            rs = pstmt.executeQuery();

            // If there is an item in the result set
            if(rs.first()) {

                // return the result set
                return rs;
            }
        } catch (Exception e){
            System.out.println(e);
        }
        // If there are no items in the result set return null
        return null;
    }

    /**
     * Method that returns all user data
     * @return Result set of all user data
     */
    public ResultSet getAllUserData(){
        ResultSet rs;
        try {

            // Connect to the database
            Statement s = dbCon.createStatement();

            // Execute SQL command
            s.executeQuery("Select * from students");

            // Get the results
            rs = s.getResultSet();

            // If there is data in the result set
            if(rs.first()){

                // Return the result set
                return rs;
            }
        } catch (Exception e) {
            // If it fails print message to console
            System.out.println(e);
        }

        // If there is no data in the result set return null
        return null;
    }

    /**
     * Method to search though users
     * @param field Field to search though
     * @param parameter Search parameters
     * @return Result set of users found
     */
    public ResultSet search(String field, String parameter) {

        String sql_command = "Select * from students WHERE " + field.replaceAll("\\s+","") + "=?";
        ResultSet rs;
        try {
            // Create prepared statement to insert the data into the database without the vulnerability of an sql injection attack.
            PreparedStatement pstmt = dbCon.prepareStatement(sql_command);
            pstmt.setString(1, parameter);

            System.out.println(pstmt.toString());
            rs = pstmt.executeQuery();

            // If there is data in the result set
            if(rs.first()){

                // Return the result set
                return rs;
            }
        } catch (Exception e) {
            // If it fails print message to console
            System.out.println(e);
        }

        // If there is no data in the result set return null
        return null;
    }
}