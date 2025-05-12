import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentManagementSystem {
    public static void main(String[] args) {
        new MainMenu();
    }
}

class MainMenu extends JFrame {
    public MainMenu() {
        setTitle("Student Management System");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton addButton = new JButton("Add Student");
        JButton searchButton = new JButton("Search Student");

        addButton.addActionListener(e -> new AddStudentWindow());
        searchButton.addActionListener(e -> new SearchStudentWindow());

        setLayout(new GridLayout(2, 1));
        add(addButton);
        add(searchButton);

        setVisible(true);
    }
}

class AddStudentWindow extends JFrame {
    JTextField idField, nameField, gpaField, subjectField, dateField;

    public AddStudentWindow() {
        setTitle("Add Student");
        setSize(300, 300);
        setLayout(new GridLayout(6, 2));
        setLocationRelativeTo(null);

        idField = new JTextField();
        nameField = new JTextField();
        gpaField = new JTextField();
        subjectField = new JTextField();
        dateField = new JTextField();

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveStudent());

        add(new JLabel("Student ID:")); add(idField);
        add(new JLabel("Name:")); add(nameField);
        add(new JLabel("GPA:")); add(gpaField);
        add(new JLabel("Subject:")); add(subjectField);
        add(new JLabel("Enrollment Date (YYYY-MM-DD):")); add(dateField);
        add(saveButton);

        setVisible(true);
    }

    private void saveStudent() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/studentms", "root", "")) {
            String sql = "INSERT INTO student VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(idField.getText()));
            ps.setString(2, nameField.getText());
            ps.setDouble(3, Double.parseDouble(gpaField.getText()));
            ps.setString(4, subjectField.getText());
            ps.setDate(5, Date.valueOf(dateField.getText()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student added successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}

class SearchStudentWindow extends JFrame {
    JTextField searchField;
    JComboBox<String> fieldSelector;
    JTable table;
    DefaultTableModel model;
    JButton deleteButton;
    int selectedStudentId = -1;

    public SearchStudentWindow() {
        setTitle("Search Student");
        setSize(800, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel();
        searchField = new JTextField(20);
        String[] fields = {"All", "student_id", "name", "gpa", "subject", "enrollment_date"};
        fieldSelector = new JComboBox<>(fields);
        JButton searchButton = new JButton("Search");
        deleteButton = new JButton("Delete");
        deleteButton.setVisible(false);

        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(fieldSelector);
        topPanel.add(searchButton);
        topPanel.add(deleteButton);

        model = new DefaultTableModel(new String[]{"ID", "Name", "GPA", "Subject", "Enrollment Date"}, 0);
        table = new JTable(model);
        table.setVisible(false);

        searchButton.addActionListener(e -> searchStudents());
        deleteButton.addActionListener(e -> deleteSelectedStudent());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    selectedStudentId = Integer.parseInt(model.getValueAt(row, 0).toString());
                }
            }
        });

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        setVisible(true);
    }

    private void searchStudents() {
        model.setRowCount(0);
        selectedStudentId = -1;

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/studentms", "root", "")) {
            String keyword = searchField.getText();
            String selectedField = fieldSelector.getSelectedItem().toString();

            String sql;
            if (selectedField.equals("All")) {
                sql = "SELECT * FROM student WHERE CONCAT(student_id, name, gpa, subject, enrollment_date) LIKE ?";
            } else {
                sql = "SELECT * FROM student WHERE " + selectedField + " LIKE ?";
            }

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                found = true;
                model.addRow(new Object[]{
                    rs.getInt("student_id"),
                    rs.getString("name"),
                    rs.getDouble("gpa"),
                    rs.getString("subject"),
                    rs.getDate("enrollment_date")
                });
            }
            table.setVisible(found);
            deleteButton.setVisible(found);
            if (!found) JOptionPane.showMessageDialog(this, "No data found.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteSelectedStudent() {
        if (selectedStudentId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student from the table.");
            return;
        }

        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete student ID " + selectedStudentId + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/studentms", "root", "")) {
                String sql = "DELETE FROM student WHERE student_id = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, selectedStudentId);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Student deleted successfully.");
                searchStudents(); // refresh
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
}
