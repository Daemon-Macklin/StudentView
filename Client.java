/**
 * author Daemon Macklin
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Class to manage GUI and User input
 */
public class Client {

    // Items to show user data
    private static JLabel userFirstName = new JLabel("First Name: ");
    private static JLabel userSurName = new JLabel("Sur Name:");
    private static JLabel userSID = new JLabel("SID:");
    private static JLabel userStudID = new JLabel("Stud ID:");
    private static JButton viewNext = new JButton("Next");
    private static JButton viewPrev = new JButton("Previous");
    private static JButton viewClear = new JButton("Clear");
    private static JLabel searchError = new JLabel();
    private static JButton logoutButton = new JButton("Logout");

    // Items for user to login
    private static JTextField userId = new JTextField();
    private static JButton loginButton = new JButton("Login");
    private static JLabel loginError = new JLabel("");

    // Items for search functionality
    private static JComboBox<String> searchUserField = new JComboBox<>();
    private static JLabel labelsearchUserData = new JLabel("Parameters");
    private static JTextField searchUserData = new JTextField();
    private static JButton searchUser = new JButton("Search");

    // Items for server view info
    private static JTextArea display = new JTextArea();
    private static JButton clearDisplay = new JButton("Clear");

    // Panels for data
    private static JPanel viewContents = new JPanel();
    private static JPanel loginContents = new JPanel();
    private static JPanel searchContents = new JPanel();
    private static JPanel serverViewContents = new JPanel();

    // IO connection to database
    private Socket socket;
    private DataOutputStream toServer;
    private DataInputStream fromServer;

    // Global data
    private ArrayList<Student> queryData;
    private int displayIndex = 0;

    public static void main(String[] args) {
        Client client = new Client();
        client.createFrontEnd();
    }

    /**
     * Method to generate the client frontend
     */
    private void createFrontEnd(){
        JFrame frame = new JFrame("Student View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loginContents.setLayout(new FlowLayout());

        userId.setPreferredSize(new Dimension(200, 20));
        loginContents.add(userId);

        loginButton.setPreferredSize(new Dimension(100,20));

        loginContents.add(loginButton);

        loginContents.add(loginError);

        loginContents.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                login(userId.getText());
            }
        });


        viewContents.setLayout(new BoxLayout(viewContents, BoxLayout.Y_AXIS));

        viewContents.add(userFirstName);
        viewContents.add(userSurName);
        viewContents.add(userSID);
        viewContents.add(userStudID);

        viewContents.add(Box.createVerticalStrut(10)); // Fixed width invisible separator.

        viewContents.add(viewNext);

        viewContents.add(Box.createVerticalStrut(5)); // Fixed width invisible separator.

        viewContents.add(viewPrev);

        viewContents.add(Box.createVerticalStrut(5)); // Fixed width invisible separator.

        viewContents.add(viewClear);

        viewContents.add(Box.createVerticalStrut(5)); // Fixed width invisible separator.

        viewContents.add(searchError);

        viewContents.add(Box.createVerticalStrut(5)); // Fixed width invisible separator.

        viewContents.add(logoutButton);

        // Create action listeners for the next, previous and clean buttons
        viewNext.addActionListener(actionEvent -> displayController("next"));

        viewPrev.addActionListener(actionEvent -> displayController("previous"));

        viewClear.addActionListener(actionEvent -> getAllUserData());

        logoutButton.addActionListener(actionEvent -> logout());

        // searchContents.setLayout(new BoxLayout(searchContents, BoxLayout.Y_AXIS));

        searchContents.add(searchUserField);
        searchUserField.setPreferredSize(new Dimension(150, 25));
        searchUserField.setMaximumSize(searchUserField.getPreferredSize());
        searchUserField.addItem("FNAME");
        searchUserField.addItem("SNAME");
        searchUserField.addItem("SID");
        searchUserField.addItem("STUD_ID");

        searchContents.add(labelsearchUserData);
        searchContents.add(searchUserData);
        searchUserData.setPreferredSize(new Dimension(150, 25));
        searchUserData.setMaximumSize(searchUserData.getPreferredSize());

        searchContents.add(searchUser);

        // Create action listener for search user button
        searchUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                searchError.setText("");
                searchUser();
            }
        });

        serverViewContents.setLayout(new BoxLayout(serverViewContents, BoxLayout.Y_AXIS));
        display.setEditable(false);
        display.setPreferredSize(new Dimension(500, 500));
        serverViewContents.add(display);
        serverViewContents.add(clearDisplay);
        clearDisplay.addActionListener(actionEvent -> clearServerDisplay());

        frame.add(viewContents, BorderLayout.CENTER);
        frame.add(loginContents, BorderLayout.NORTH);
        frame.add(searchContents, BorderLayout.SOUTH);
        frame.add(serverViewContents, BorderLayout.EAST);
        frame.pack();
        frame.setSize(700,600);
        frame.setVisible(true);

        // Set DB support contents to invisible until the user logs in
        viewContents.setVisible(false);
        searchContents.setVisible(false);
        serverViewContents.setVisible(false);

    }

    /**
     * Method to clear the Text area with all the server messages on
     */
    private void clearServerDisplay(){
        clearDisplay.setVisible(false);
        display.setText("");
    }

    /**
     * Method to control which item in the query data list is being displayed
     * @param option Used to control how to move the list
     */
    private void displayController(String option) {

        // Check the option
        if(option.equals("first")){

            // If it is first set the displayIndex to 0
            displayIndex = 0;

        } else if(option.equals("next")){

            // If it is next check if we are at the end of the list
            if(displayIndex == queryData.size() -1){

                // If we are at the end go to the first item
                displayIndex = 0;
            } else {

                // Other wise go to the next
                displayIndex += 1;
            }

        } else if(option.equals("previous")){

            // If it is previous check if we are at the beginning of the list
            if(displayIndex == 0){

                // If we are then go to the last item
                displayIndex = queryData.size()-1;
            } else {

                // Other wise go to the previous
                displayIndex -= 1;
            }
        }

        // Display the user at the new display index
        displayUser(queryData.get(displayIndex));
    }

    /**
     * Method to display the selected result set one the display
     */
    private void displayUser(Student student) {
        userFirstName.setText("First Name: " + student.getFname());
        userSurName.setText("Sur Name: " + student.getSname());
        userSID.setText("SID: " + student.getSid());
        userStudID.setText("STUD ID: " + student.getStud_id());
    }

    /**
     * Method that gets all of the user data
     */
    private void getAllUserData() {

        try {

            // Send a request to the server for allUserData
            toServer.writeUTF("allUserData");
            toServer.flush();

            // Wait for the response
            String response = fromServer.readUTF();
            //System.out.println(response);

            // The returning data is comma separated similar to a csv file so we split it by commas
            String[] userData = response.split(",");

            // If the first item in the list is userData we have data we can decode
            if(userData[0].equals("userData")){

                // Tell the user we got data and decode the data and reset the display to show the first item
                displayMessage("Getting User Data");
                queryData = decodeUserData(userData);
                displayController("first");
            } else{

                // Else tell the user that there was an error
                displayMessage("Error Getting all User Data");
            }

        } catch (java.net.SocketException e){
            displayMessage("Connection to Database Lost");
        }catch (Exception e){
            System.out.println(e);
            displayMessage("Error Getting User Data");
        }
    }

    /**
     * Method to take a list of strings and convert them to student objects
     * @param userData A list of Strings containing data from the sql result set
     * @return An Arraylist of students
     */
    private ArrayList<Student> decodeUserData(String[] userData){

        ArrayList<Student> newList = new ArrayList();

        // The encoded data will be: userData,sid,stud_id,fname,sname,sid,stud_id,fname,sname.....
        // To decode is and convert into Student objects iterate though the userData list and convert each
        // student into their own student Object
        for(int i = 1; i < userData.length; i+= 4){
            String sid = userData[i];
            String stud_id = userData[i+1];
            String fname = userData[i+2];
            String sname= userData[i+3];
            Student newStudent = new Student(sid, stud_id, fname, sname);
            newList.add(newStudent);
        }
        return newList;
    }

    /**
     * Method to login the user
     * @param userid userid of the user
     */
    private void login(String userid){

        // Set some variables we need
        Boolean canLogin = false;
        String response = "";

        // If the field is empty then tell the user
        if(userid.equals("")){
            loginError.setText("Please Input UID");
        } else {

            // Else try to connect to the server
            Boolean result = connectToServer();
            if (result) {
                try {

                    // If the connection object is created request the server to login
                    // By sending is comma separated variables requestType,userId
                    toServer.writeUTF("login," + userid);
                    toServer.flush();

                    // Wait for a response
                    response = fromServer.readUTF();

                    // Check the response
                    if (response.equals("Invalid UID")) {

                        // If the user has an invalid uid tell the user and stop
                        loginError.setText(response);
                        return;
                    } else {

                        // Else the user can login
                        canLogin = true;
                    }
                }catch (Exception e) {
                    System.out.println(e);
                }
            }

            // Check if the user can login
            if (canLogin) {

                // If they can show the DB support tools and hid the login screen
                serverViewContents.setVisible(true);
                loginContents.setVisible(false);
                viewContents.setVisible(true);
                searchContents.setVisible(true);
                serverViewContents.setVisible(true);

                // Give a welcome message to the user with their userName
                displayMessage("Welcome: " + response);

                // Get all the student data in the database
                this.getAllUserData();
            } else {
                loginError.setText("Error Logging in");
            }
        }
    }

    /**
     * Method to logout
     */
    private void logout() {
        try{
            // Close the socket
            socket.close();

            // Clear all the fields and hide the DB support tools
            userStudID.setText("STUD ID");
            userSID.setText("SID");
            userFirstName.setText("First Name");
            userSurName.setText("Sur Name");
            viewContents.setVisible(false);
            searchUserData.setText("");
            searchUserField.setSelectedIndex(0);
            searchContents.setVisible(false);
            display.setText("");
            serverViewContents.setVisible(false);
            loginContents.setVisible(true);

        } catch (java.net.SocketException e){
            displayMessage("Connection to Database Lost");
        } catch (IOException e){
            System.out.println(e);
        }
    }

    /**
     * Method to setup the connection to the database
     * @return True if connection is setup false if not
     */
    private Boolean connectToServer() {

        try {
            // Create a new Socket at 127.0.0.1(localhost) using port 8000 to connect to our server
            socket = new Socket("localhost", 8000);
            fromServer = new DataInputStream(socket.getInputStream());
            toServer = new DataOutputStream(socket.getOutputStream());
            return true;
        } catch (Exception e){
            System.out.println(e);
            loginError.setText("Error Connecting to Server");
        }
        return false;
    }

    /**
     * Helper function to display a message to the user with all of the server details and time
     * @param message String we want to display
     */
    private void displayMessage(String message){
        display.append(new Date() + message + ", From: " + socket.getInetAddress() + "\n");
        clearDisplay.setVisible(true);
    }

    /**
     * Method to search a user
     */
    private void searchUser(){

        // If the search data field is empty
        if(searchUserData.getText().equals("")){

            // Tell the user
            searchError.setText("Please Enter Data");
        }else {

            // Other wise add the data to a comma separated string
            String request = "search,";
            request += searchUserField.getSelectedItem().toString() + ",";
            request += searchUserData.getText() + ",";

            try {

                // Send the request and wait for the response
                toServer.writeUTF(request);
                toServer.flush();
                String response = fromServer.readUTF();

                // Split up the csv response
                String[] userData = response.split(",");

                // If the first item in the set it "UserData" then we have data to show the user
                if(userData[0].equals("userData")){

                    // Tell the user the search is successful, decode the response and display it
                    displayMessage("Search Successful");
                    queryData = decodeUserData(userData);
                    displayController("first");

                } else if(userData[0].equals("No User data")) {

                    // If the response is no user data tell the user
                    displayMessage(response);
                }else {

                    // Else the search failed
                    displayMessage("Search Failed");
                }

            } catch (java.net.SocketException e){
                displayMessage("Connection to Database Lost");
            } catch (Exception e){
                System.out.println(e);
            }
        }
    }
}


/**
 * Class to store the search data in the front end
 */
class Student {

    private String sid;
    private String stud_id;
    private String fname;
    private String sname;

    /**
     * Object constructor
     * @param sid
     * @param stud_id
     * @param fname
     * @param sname
     */
    public Student(String sid, String stud_id, String fname, String sname){
        this.setSid(sid);
        this.setStud_id(stud_id);
        this.setFname(fname);
        this.setSname(sname);
    }

    //-------------Getters and Setters-------------\\

    public void setSid(String sid){
        this.sid = sid;
    }

    public String getSid() {
        return sid;
    }

    public void setStud_id(String stud_id){
        this.stud_id = stud_id;
    }

    public String getStud_id() {
        return stud_id;
    }


    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }
}

