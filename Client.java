import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Client {

    // Items to show user data
    private static JLabel userFirstName = new JLabel("First Name: ");
    private static JLabel userSurName = new JLabel("Sur Name:");
    private static JLabel userPPSN = new JLabel("PPSN:");
    private static JLabel userSalary = new JLabel("Salary:");
    private static JLabel userJobTitle = new JLabel("JobTitle");
    private static JLabel userGender = new JLabel("Gender:");
    private static JLabel userDOB = new JLabel("DOB:");
    private static JButton viewNext = new JButton("Next");
    private static JButton viewPrev = new JButton("Previous");
    private static JButton viewClear = new JButton("Clear");
    private static JLabel searchError = new JLabel();

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

    // Panels for data
    private static JPanel viewContents = new JPanel();
    private static JPanel loginContents = new JPanel();
    private static JPanel searchContents = new JPanel();
    private static JPanel serverViewContents = new JPanel();

    // IO connection to database
    private DataOutputStream toServer;
    private DataInputStream fromServer;

    // Global data
    private ResultSet queryData = null;

    public static void main(String[] args) {
        Client client = new Client();
        client.createFrontEnd();
        //client.connectToServer();
    }

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

        viewContents.add(userPPSN);
        viewContents.add(userFirstName);
        viewContents.add(userSurName);
        viewContents.add(userJobTitle);
        viewContents.add(userSalary);
        viewContents.add(userDOB);
        viewContents.add(userGender);

        viewContents.add(Box.createVerticalStrut(10)); // Fixed width invisible separator.

        viewContents.add(viewNext);

        viewContents.add(Box.createVerticalStrut(5)); // Fixed width invisible separator.

        viewContents.add(viewPrev);

        viewContents.add(Box.createVerticalStrut(5)); // Fixed width invisible separator.

        viewContents.add(viewClear);

        viewContents.add(Box.createVerticalStrut(5)); // Fixed width invisible separator.

        viewContents.add(searchError);
        // Create action listeners for the next, previous and clean buttons
        viewNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //moveResultSet("next");
            }
        });

        viewPrev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                moveResultSet("previous");
            }
        });

        viewClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                getAllUserData();
            }
        });

        // searchContents.setLayout(new BoxLayout(searchContents, BoxLayout.Y_AXIS));

        searchContents.add(searchUserField);
        searchUserField.setPreferredSize(new Dimension(150, 25));
        searchUserField.setMaximumSize(searchUserField.getPreferredSize());
        searchUserField.addItem("First Name");
        searchUserField.addItem("Sur Name");
        searchUserField.addItem("Salary");
        searchUserField.addItem("Job Title");
        searchUserField.addItem("Gender");
        searchUserField.addItem("PPS");

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
                // searchUser();
            }
        });

        display.setEditable(false);
        serverViewContents.add(display);

        frame.add(viewContents, BorderLayout.CENTER);
        frame.add(loginContents, BorderLayout.NORTH);
        frame.add(searchContents, BorderLayout.SOUTH);
        frame.add(serverViewContents, BorderLayout.EAST);
        frame.pack();
        frame.setSize(500,300);
        frame.setVisible(true);
        viewContents.setVisible(false);
        searchContents.setVisible(false);
        serverViewContents.setVisible(false);

    }

    // Method to manage displaying the result set
    public void moveResultSet(String option) {

        // If the option is previous
        if (option.equals("previous")) {

            // Move the set result back one
            try {
                queryData.previous();
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if(option.equals("first")){

            // Else move the result to the first
            try {
                queryData.first();
            } catch (Exception e) {
                System.out.println(e);
            }
        }else{

            // Else move the result forward
            try {
                queryData.next();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        // Display the result set
        displayUser();
    }

    /**
     * Method to display the selected result set one the display
     */
    private void displayUser () {
        try {
            userPPSN.setText("PPSN: " + queryData.getString("PPS"));
            userFirstName.setText("First Nanme: " + queryData.getString("FirstName"));
            userSurName.setText("Sur Name: " + queryData.getString("SurName"));
            userDOB.setText("DOB: " + queryData.getDate("DOB").toString());
            userGender.setText("Gender: " + queryData.getString("Gender"));
            userSalary.setText("Salary: " + Integer.toString(queryData.getInt("Salary")));
            userJobTitle.setText("Job Title: " + queryData.getString("JobTitle"));
        } catch (Exception e){
            System.out.println(e);
        }
    }

    /**
     * Method that calls the get all user data method
     */
    private void getAllUserData(){

        // Get all of the user data from the dbController get all function
        ResultSet searchData = null;

        try {
            // Check if the result set is not null and it is not empty
            if (searchData != null && searchData.next()) {

                // Set the global result set to be the result of the query
                queryData = searchData;
                moveResultSet("first");
            } else {
                searchError.setText("No Users");
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private void login(String userid){
        System.out.println(userid);
        connectToServer();
        Boolean canLogin = false;
        String response = "";
        if(userid.equals("")){
            loginError.setText("Please Input UID");
        } else {
            try {
                toServer.writeUTF("login," + userid);
                toServer.flush();

                response = fromServer.readUTF();
                System.out.println(response);

                if(response.equals("Invalid UID")){
                    loginError.setText(response);
                    return;
                } else{
                    canLogin = true;
                }
            } catch (Exception e){
                System.out.println(e);
            }
        }
        if(canLogin) {
            serverViewContents.setVisible(true);
            loginContents.setVisible(false);
            viewContents.setVisible(true);
            searchContents.setVisible(true);
            serverViewContents.setVisible(true);
            display.append("Welcome: " + response + "\n");
        }
        else{
            loginError.setText("Error Logging in");
        }
    }

    private void connectToServer() {

        try{
            Socket socket = new Socket("localhost", 8000);
            fromServer = new DataInputStream(socket.getInputStream());
            toServer = new DataOutputStream(socket.getOutputStream());

        } catch (Exception e){
            System.out.println(e);
        }
    }
}

