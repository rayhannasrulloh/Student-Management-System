import javax.swing.*; // Import library Swing untuk membuat GUI
import javax.swing.table.DefaultTableModel; // Import DefaultTableModel untuk mengelola data tabel
import java.awt.*; // Import library AWT untuk tata letak dan komponen GUI
import java.sql.*; // Import library SQL untuk koneksi dan operasi database

public class Explain extends JFrame {
    // Detail koneksi database
    static final String DB_URL = "jdbc:mysql://localhost:3306/studentms"; // URL database
    static final String DB_USER = "root"; // Username database
    static final String DB_PASS = ""; // Password database

    // Komponen GUI
    JLabel title; // Label untuk judul aplikasi
    JTextField txtId, txtName, txtGpa, txtSubject, txtEnrollmentDate, txtSearch; // Input teks untuk data mahasiswa
    JTable table; // Tabel untuk menampilkan data mahasiswa
    DefaultTableModel model; // Model data untuk tabel

    public Explain() {
        setTitle("Student Management System"); // Set judul jendela
        setSize(1000, 600); // Set ukuran jendela
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Tutup aplikasi saat jendela ditutup
        setLayout(new BorderLayout()); // Gunakan BorderLayout untuk tata letak

        // Panel atas untuk input data
        JPanel panelTop = new JPanel(new GridLayout(2, 6, 5, 5)); // GridLayout dengan 2 baris, 6 kolom, dan jarak 5px

        // Judul aplikasi
        title = new JLabel("Student Management System", SwingConstants.CENTER); // Label judul di tengah
        title.setFont(new Font("Poppins", Font.BOLD, 24)); // Set font judul
        title.setForeground(Color.black); // Set warna teks menjadi hitam
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Tambahkan margin atas dan bawah

        // Input teks untuk data mahasiswa
        txtId = new JTextField(); // Input ID mahasiswa
        txtName = new JTextField(); // Input nama mahasiswa
        txtGpa = new JTextField(); // Input GPA mahasiswa
        txtSubject = new JTextField(); // Input mata kuliah
        txtEnrollmentDate = new JTextField(); // Input tanggal pendaftaran
        txtSearch = new JTextField(); // Input untuk pencarian data

        // Tambahkan label dan input teks ke panel atas
        panelTop.add(new JLabel("ID:")); // Label ID
        panelTop.add(txtId); // Input ID
        panelTop.add(new JLabel("Name:")); // Label nama
        panelTop.add(txtName); // Input nama
        panelTop.add(new JLabel("GPA:")); // Label GPA
        panelTop.add(txtGpa); // Input GPA
        panelTop.add(new JLabel("Subject:")); // Label mata kuliah
        panelTop.add(txtSubject); // Input mata kuliah
        panelTop.add(new JLabel("Enrollment Date (yyyy-mm-dd):")); // Label tanggal pendaftaran
        panelTop.add(txtEnrollmentDate); // Input tanggal pendaftaran

        // Tombol untuk operasi CRUD
        JButton btnAdd = new JButton("Add"); // Tombol tambah data
        JButton btnUpdate = new JButton("Update"); // Tombol update data
        JButton btnDelete = new JButton("Delete"); // Tombol hapus data
        JButton btnSearch = new JButton("Search"); // Tombol cari data

        // Tambahkan tombol ke panel atas
        panelTop.add(btnAdd); // Tambahkan tombol tambah
        panelTop.add(btnUpdate); // Tambahkan tombol update
        panelTop.add(btnDelete); // Tambahkan tombol hapus
        panelTop.add(txtSearch); // Tambahkan input pencarian
        panelTop.add(btnSearch); // Tambahkan tombol cari

        // Tambahkan komponen ke jendela
        add(title, BorderLayout.NORTH); // Tambahkan judul di atas
        add(panelTop, BorderLayout.CENTER); // Tambahkan panel atas di tengah

        // Panel untuk tabel
        JPanel tablePanel = new JPanel(new BorderLayout()); // Panel dengan BorderLayout
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30)); // Tambahkan margin
        model = new DefaultTableModel(new String[]{"ID", "Name", "GPA", "Subject", "Enrollment Date"}, 0); // Kolom tabel
        table = new JTable(model); // Buat tabel dengan model
        table.setFillsViewportHeight(true); // Isi viewport tabel
        table.setBackground(Color.WHITE); // Warna latar tabel
        table.setFont(new Font("Arial", Font.PLAIN, 14)); // Font tabel
        table.setRowHeight(30); // Tinggi baris tabel
        table.setSelectionBackground(Color.LIGHT_GRAY); // Warna seleksi
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER); // Tambahkan tabel ke panel dengan scroll
        add(tablePanel, BorderLayout.SOUTH); // Tambahkan panel tabel di bawah

        loadStudents(); // Muat data mahasiswa dari database

        // Tambahkan aksi untuk tombol
        btnAdd.addActionListener(e -> addStudent()); // Tambahkan aksi untuk tombol tambah
        btnUpdate.addActionListener(e -> updateStudent()); // Tambahkan aksi untuk tombol update
        btnDelete.addActionListener(e -> deleteStudent()); // Tambahkan aksi untuk tombol hapus
        btnSearch.addActionListener(e -> searchStudent()); // Tambahkan aksi untuk tombol cari

        setVisible(true); // Tampilkan jendela
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS); // Koneksi ke database
    }

    private void loadStudents() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM student"); // Query untuk mengambil data mahasiswa
            model.setRowCount(0); // Hapus semua data di tabel
            while (rs.next()) { // Iterasi hasil query
                model.addRow(new Object[]{ // Tambahkan data ke tabel
                    rs.getInt("student_id"), // ID mahasiswa
                    rs.getString("name"), // Nama mahasiswa
                    rs.getBigDecimal("gpa"), // GPA mahasiswa
                    rs.getString("subject"), // Mata kuliah
                    rs.getDate("enrollment_date") // Tanggal pendaftaran
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage()); // Tampilkan pesan error
        }
    }

    private void addStudent() {
        String sql = "INSERT INTO student (student_id, name, gpa, subject, enrollment_date) VALUES (?, ?, ?, ?, ?)"; // Query tambah data
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(txtId.getText())); // Set ID mahasiswa
            pstmt.setString(2, txtName.getText()); // Set nama mahasiswa
            pstmt.setBigDecimal(3, new java.math.BigDecimal(txtGpa.getText())); // Set GPA mahasiswa
            pstmt.setString(4, txtSubject.getText()); // Set mata kuliah
            pstmt.setDate(5, java.sql.Date.valueOf(txtEnrollmentDate.getText())); // Set tanggal pendaftaran
            pstmt.executeUpdate(); // Eksekusi query
            JOptionPane.showMessageDialog(this, "Student added successfully."); // Tampilkan pesan sukses
            loadStudents(); // Muat ulang data mahasiswa
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding student: " + e.getMessage()); // Tampilkan pesan error
        }
    }

    private void updateStudent() {
        String sql = "UPDATE student SET name=?, gpa=?, subject=?, enrollment_date=? WHERE student_id=?"; // Query update data
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, txtName.getText()); // Set nama mahasiswa
            pstmt.setBigDecimal(2, new java.math.BigDecimal(txtGpa.getText())); // Set GPA mahasiswa
            pstmt.setString(3, txtSubject.getText()); // Set mata kuliah
            pstmt.setDate(4, java.sql.Date.valueOf(txtEnrollmentDate.getText())); // Set tanggal pendaftaran
            pstmt.setInt(5, Integer.parseInt(txtId.getText())); // Set ID mahasiswa
            pstmt.executeUpdate(); // Eksekusi query
            JOptionPane.showMessageDialog(this, "Student updated successfully."); // Tampilkan pesan sukses
            loadStudents(); // Muat ulang data mahasiswa
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating student: " + e.getMessage()); // Tampilkan pesan error
        }
    }

    private void deleteStudent() {
        String sql = "DELETE FROM student WHERE student_id=?"; // Query hapus data
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(txtId.getText())); // Set ID mahasiswa
            pstmt.executeUpdate(); // Eksekusi query
            JOptionPane.showMessageDialog(this, "Student deleted successfully."); // Tampilkan pesan sukses
            loadStudents(); // Muat ulang data mahasiswa
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting student: " + e.getMessage()); // Tampilkan pesan error
        }
    }

    private void searchStudent() {
        String keyword = txtSearch.getText(); // Ambil keyword pencarian
        String sql = "SELECT * FROM student WHERE name LIKE ? OR student_id LIKE ?"; // Query pencarian data
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%"); // Set parameter pencarian nama
            pstmt.setString(2, "%" + keyword + "%"); // Set parameter pencarian ID
            ResultSet rs = pstmt.executeQuery(); // Eksekusi query
            model.setRowCount(0); // Hapus semua data di tabel
            while (rs.next()) { // Iterasi hasil query
                model.addRow(new Object[]{ // Tambahkan data ke tabel
                    rs.getInt("student_id"), // ID mahasiswa
                    rs.getString("name"), // Nama mahasiswa
                    rs.getBigDecimal("gpa"), // GPA mahasiswa
                    rs.getString("subject"), // Mata kuliah
                    rs.getDate("enrollment_date") // Tanggal pendaftaran
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching student: " + e.getMessage()); // Tampilkan pesan error
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Explain::new); // Jalankan aplikasi GUI di thread event-dispatching
    }
}