import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class hospital_administration {
    private Connection connection;

    public hospital_administration() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospitaladministration", "root", "777@Smeasy");
            initializeLoginScreen();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeLoginScreen() {
        JFrame loginFrame = new JFrame("Admin Login");
        loginFrame.setSize(400, 300);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel idLabel = new JLabel("Admin ID:");
        JTextField idField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(e -> {
            try {
                int adminId = Integer.parseInt(idField.getText());
                String password = new String(passwordField.getPassword());
                if (checkPassword(adminId, password)) {
                    JOptionPane.showMessageDialog(loginFrame, "Login Successful!");
                    loginFrame.dispose();
                    initializeAdminDashboard(adminId);
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(loginFrame, "Invalid Admin ID. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(idLabel);
        panel.add(idField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(loginButton);

        loginFrame.add(panel);
        loginFrame.setVisible(true);
    }

    private void initializeAdminDashboard(int adminId) {
        JFrame dashboardFrame = new JFrame("Admin Dashboard");
        dashboardFrame.setSize(600, 400);
        dashboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));

        JButton addAdminButton = new JButton("Add New Admin");
        JButton addPatientButton = new JButton("Add New Patient");
        JButton fetchDetailsButton = new JButton("Fetch Patient Details");
        JButton logoutButton = new JButton("Logout");

        addAdminButton.addActionListener(e -> initializeAddAdminScreen());
        addPatientButton.addActionListener(e -> initializeAddPatientScreen());
        fetchDetailsButton.addActionListener(e -> initializeFetchPatientDetailsScreen());
        logoutButton.addActionListener(e -> {
            dashboardFrame.dispose();
            initializeLoginScreen();
        });

        panel.add(addAdminButton);
        panel.add(addPatientButton);
        panel.add(fetchDetailsButton);
        panel.add(logoutButton);

        dashboardFrame.add(panel);
        dashboardFrame.setVisible(true);
    }

    private void initializeAddAdminScreen() {
        JFrame addAdminFrame = new JFrame("Add New Admin");
        addAdminFrame.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel idLabel = new JLabel("Admin ID:");
        JTextField idField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JButton addButton = new JButton("Add");

        addButton.addActionListener(e -> {
            try {
                int adminId = Integer.parseInt(idField.getText());
                String password = new String(passwordField.getPassword());
                createUser(adminId, password);
                JOptionPane.showMessageDialog(addAdminFrame, "Admin added successfully!");
                addAdminFrame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addAdminFrame, "Invalid Admin ID. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(idLabel);
        panel.add(idField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(addButton);

        addAdminFrame.add(panel);
        addAdminFrame.setVisible(true);
    }

    private void initializeAddPatientScreen() {
        JFrame addPatientFrame = new JFrame("Add New Patient");
        addPatientFrame.setSize(400, 400);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));

        JLabel nameLabel = new JLabel("Patient Name:");
        JTextField nameField = new JTextField();
        JLabel phoneLabel = new JLabel("Phone:");
        JTextField phoneField = new JTextField();
        JLabel mailLabel = new JLabel("Mail ID:");
        JTextField mailField = new JTextField();
        JLabel roomLabel = new JLabel("Room:");
        JTextField roomField = new JTextField();
        JLabel idLabel = new JLabel("ID:");
        JTextField idField = new JTextField();
        JButton addButton = new JButton("Add");

        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String phone = phoneField.getText();
                String mail = mailField.getText();
                double room = Double.parseDouble(roomField.getText());
                int id = Integer.parseInt(idField.getText());
                createPatient(id, name, phone, mail, room);
                JOptionPane.showMessageDialog(addPatientFrame, "Patient added successfully!");
                addPatientFrame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addPatientFrame, "Invalid input! Please enter correct details.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(phoneLabel);
        panel.add(phoneField);
        panel.add(mailLabel);
        panel.add(mailField);
        panel.add(roomLabel);
        panel.add(roomField);
        panel.add(idLabel);
        panel.add(idField);
        panel.add(new JLabel());
        panel.add(addButton);

        addPatientFrame.add(panel);
        addPatientFrame.setVisible(true);
    }

    private void initializeFetchPatientDetailsScreen() {
        JFrame fetchPatientFrame = new JFrame("Fetch Patient Details");
        fetchPatientFrame.setSize(400, 400);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));

        JLabel idLabel = new JLabel("Patient ID:");
        JTextField idField = new JTextField();
        JButton fetchButton = new JButton("Fetch Details");
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton addBillButton = new JButton("Add Bill");
        JButton payBillButton = new JButton("Pay Bill");

        // Initially disable the buttons
        addBillButton.setEnabled(false);
        payBillButton.setEnabled(false);

        fetchButton.addActionListener(e -> {
            try {
                int patientId = Integer.parseInt(idField.getText());
                boolean found = fetchPatientDetails(patientId, resultArea);
                if (found) {
                    addBillButton.setEnabled(true);
                    payBillButton.setEnabled(true);
                } else {
                    addBillButton.setEnabled(false);
                    payBillButton.setEnabled(false);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(fetchPatientFrame, "Invalid Patient ID. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        addBillButton.addActionListener(e -> {
            try {
                int patientId = Integer.parseInt(idField.getText());
                String amountStr = JOptionPane.showInputDialog(fetchPatientFrame, "Enter the bill amount to add:");
                if (amountStr != null) {
                    double amount = Double.parseDouble(amountStr);
                    addPatientBill(patientId, amount);
                    JOptionPane.showMessageDialog(fetchPatientFrame, "Bill added successfully!");
                    fetchPatientDetails(patientId, resultArea);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(fetchPatientFrame, "Invalid input! Please enter a valid bill amount.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        payBillButton.addActionListener(e -> {
            try {
                int patientId = Integer.parseInt(idField.getText());
                String amountStr = JOptionPane.showInputDialog(fetchPatientFrame, "Enter the bill amount to pay:");
                if (amountStr != null) {
                    double amount = Double.parseDouble(amountStr);
                    payPatientBill(patientId, amount);
                    JOptionPane.showMessageDialog(fetchPatientFrame, "Bill payment successful!");
                    fetchPatientDetails(patientId, resultArea);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(fetchPatientFrame, "Invalid input! Please enter a valid bill amount.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(addBillButton);
        buttonPanel.add(payBillButton);

        panel.add(idLabel);
        panel.add(idField);
        panel.add(fetchButton);

        fetchPatientFrame.add(panel, BorderLayout.NORTH);
        fetchPatientFrame.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        fetchPatientFrame.add(buttonPanel, BorderLayout.SOUTH);
        fetchPatientFrame.setVisible(true);
    }

    private boolean checkPassword(int adminId, String password) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT password FROM admin WHERE idLogin = ?")) {
            ps.setInt(1, adminId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return password.equals(rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void createUser(int id, String password) {
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO admin (idLogin, password) VALUES (?, ?)")) {
            ps.setInt(1, id);
            ps.setString(2, password);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createPatient(int id, String name, String phone, String mail, double room) {
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO patient_details (idpatient_details, patientname, patientphone, room, patientmail, bill) VALUES (?, ?, ?, ?, ?, 0.0)")) {
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, phone);
            ps.setDouble(4, room);
            ps.setString(5, mail);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean fetchPatientDetails(int patientId, JTextArea resultArea) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM patient_details WHERE idpatient_details = ?")) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                resultArea.setText("ID: " + rs.getInt("idpatient_details") + "\n" +
                                   "Name: " + rs.getString("patientname") + "\n" +
                                   "Phone: " + rs.getString("patientphone") + "\n" +
                                   "Mail: " + rs.getString("patientmail") + "\n" +
                                   "Room: " + rs.getDouble("room") + "\n" +
                                   "Bill: " + rs.getDouble("bill"));
                return true; // Patient found
            } else {
                resultArea.setText("No patient found with ID: " + patientId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Patient not found
    }

    private void addPatientBill(int patientId, double amount) {
        try (PreparedStatement ps = connection.prepareStatement("UPDATE patient_details SET bill = bill + ? WHERE idpatient_details = ?")) {
            ps.setDouble(1, amount);
            ps.setInt(2, patientId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void payPatientBill(int patientId, double amount) {
        try (PreparedStatement ps = connection.prepareStatement("UPDATE patient_details SET bill = bill - ? WHERE idpatient_details = ?")) {
            ps.setDouble(1, amount);
            ps.setInt(2, patientId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new hospital_administration();
    }
}
