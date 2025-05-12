import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Main extends JFrame {
    //database connection details
    static final String DB_URL = "jdbc:mysql://localhost:3306/studentms";
    // menginisialisasi variabel username database
    static final String DB_USER = "root";
    // menginisialisasi variabel password database
    static final String DB_PASS = "";

    // menginisialisasi variabel untuk menampilkan judul
    JLabel title;
    // menginisialisasi variabel untuk menampilkan inputan user
    JTextField txtId, txtName, txtGpa, txtSubject, txtEnrollmentDate, txtSearch;
    // menginisialisasi variabel untuk menampilkan tabel
    JTable table;
    // menginisialisasi variabel untuk menampilkan model tabel
    DefaultTableModel model;

    public Main() {
        // set title on top of the window
        setTitle("Student Management System");
        // set size of the window
        setSize(1000, 600);
        // set default close operation
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // set layout to border layout function
        setLayout(new BorderLayout());

        // set location of the window to center of the screen
        JPanel panelTop = new JPanel(new GridLayout(2, 6, 5, 5));

        // set title of the window
        title = new JLabel("Student Management System", SwingConstants.CENTER);
        // set font
        title.setFont(new Font("Poppins", Font.BOLD, 24));
        // set foreground jadi hitam
        title.setForeground(Color.black);
        // margin
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        
        txtId = new JTextField();
        txtName = new JTextField();
        txtGpa = new JTextField();
        txtSubject = new JTextField();
        txtEnrollmentDate = new JTextField();
        txtSearch = new JTextField();

        panelTop.add(new JLabel("ID:"));
        panelTop.add(txtId);
        panelTop.add(new JLabel("Name:"));
        panelTop.add(txtName);
        panelTop.add(new JLabel("GPA:"));
        panelTop.add(txtGpa);
        panelTop.add(new JLabel("Subject:"));
        panelTop.add(txtSubject);
        panelTop.add(new JLabel("Enrollment Date (yyyy-mm-dd):"));
        panelTop.add(txtEnrollmentDate);

        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnSearch = new JButton("Search");

        panelTop.add(btnAdd);
        panelTop.add(btnUpdate);
        panelTop.add(btnDelete);
        panelTop.add(txtSearch);
        panelTop.add(btnSearch);

        add(title, BorderLayout.NORTH);
        add(panelTop, BorderLayout.CENTER);
        add(panelTop, BorderLayout.NORTH);

        panelTop.setBackground(Color.LIGHT_GRAY);
        panelTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        model = new DefaultTableModel(new String[]{"ID", "Name", "GPA", "Subject", "Enrollment Date"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setSelectionBackground(Color.LIGHT_GRAY);

        loadStudents();

        btnAdd.addActionListener(e -> addStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnSearch.addActionListener(e -> searchStudent());

        setVisible(true);
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    private void loadStudents() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM student");
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("student_id"),
                    rs.getString("name"),
                    rs.getBigDecimal("gpa"),
                    rs.getString("subject"),
                    rs.getDate("enrollment_date")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage());
        }
    }

    private void addStudent() {
        String sql = "INSERT INTO student (student_id, name, gpa, subject, enrollment_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(txtId.getText()));
            pstmt.setString(2, txtName.getText());
            pstmt.setBigDecimal(3, new java.math.BigDecimal(txtGpa.getText()));
            pstmt.setString(4, txtSubject.getText());
            pstmt.setDate(5, java.sql.Date.valueOf(txtEnrollmentDate.getText()));
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student added successfully.");
            loadStudents();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding student: " + e.getMessage());
        }
    }

    private void updateStudent() {
        String sql = "UPDATE student SET name=?, gpa=?, subject=?, enrollment_date=? WHERE student_id=?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, txtName.getText());
            pstmt.setBigDecimal(2, new java.math.BigDecimal(txtGpa.getText()));
            pstmt.setString(3, txtSubject.getText());
            pstmt.setDate(4, java.sql.Date.valueOf(txtEnrollmentDate.getText()));
            pstmt.setInt(5, Integer.parseInt(txtId.getText()));
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student updated successfully.");
            loadStudents();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating student: " + e.getMessage());
        }
    }

    private void deleteStudent() {
        String sql = "DELETE FROM student WHERE student_id=?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(txtId.getText()));
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student deleted successfully.");
            loadStudents();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting student: " + e.getMessage());
        }
    }

    private void searchStudent() {
        String keyword = txtSearch.getText();
        String sql = "SELECT * FROM student WHERE name LIKE ? OR student_id LIKE ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("student_id"),
                    rs.getString("name"),
                    rs.getBigDecimal("gpa"),
                    rs.getString("subject"),
                    rs.getDate("enrollment_date")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching student: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
