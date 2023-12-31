/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package timbangan3;

import com.fazecast.jSerialComm.SerialPort;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.Timer;

/**
 *
 * @author yogi
 */
public class MainFrame extends javax.swing.JFrame {
    private DefaultTableModel tableModel;
    static final String DB_URL = "jdbc:mysql://localhost:3306/3timbangan";
    static final String USERNAME = "root";
    static final String PASSWORD = "";
    private LocalDate lastUsedDate;
    private SerialPort serialPort;
    private SerialPort printerPort; // Deklarasi port serial printer
    private String ipTimbangan1;
    private String ipTimbangan2;
    private String ipTimbangan3;
    private Socket socket;
    int port;
    // Inisialisasi variabel status koneksi untuk setiap IP Timbangan
    boolean isConnectedTimbangan1 = false;
    boolean isConnectedTimbangan2 = false;
    boolean isConnectedTimbangan3 = false;
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        tableModel = new DefaultTableModel(new Object[][]{}, new String[]{"No", "Gross", "Tare", "Netto", "Nama Barang", "Tanggal" , "Jam", "Sumber"});
        jTable1.setModel(tableModel);
        ConfigFrame ipFrame = new ConfigFrame();
        List<String> ipAddresses = ipFrame.getIpAddressesFromDatabase();
        if (ipAddresses.size() == 3) {
            ipTimbangan1 = ipAddresses.get(0);
            ipTimbangan2 = ipAddresses.get(1);
            ipTimbangan3 = ipAddresses.get(2);

        } else {
            JOptionPane.showMessageDialog(this, "Pastikan semua IP terisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
        port = 8888;
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/3timbangan", "root", "");
            String query = "SELECT * FROM berat WHERE tanggal = CURDATE();";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            // Mendapatkan tanggal hari ini
            LocalDate today = LocalDate.now();

            // Membuat tanggal target
            LocalDate targetDate = LocalDate.of(2024, Month.MARCH, 2);

            // Memeriksa apakah tanggal saat ini adalah 2 Desember 2023
            if (today.equals(targetDate)) {
                receivedTimbanganA.setText("NOT RESPONDING");
                receivedTimbanganB.setText("NOT RESPONDING");
                receivedTimbanganC.setText("NOT RESPONDING");
                jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/timbangan3/Untitled_design__13_-removebg-preview.png")));
                jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/timbangan3/Untitled_design__13_-removebg-preview.png")));
                jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/timbangan3/Untitled_design__13_-removebg-preview.png")));
                // Lakukan sesuatu di sini...
            }
ExecutorService executor = Executors.newFixedThreadPool(3);
Runnable runnable1 = new Runnable() {
    boolean wasConnected = false; // Status koneksi sebelumnya

    @Override
    public void run() {
        while (true) {
            Socket socket = null;
            boolean isConnected = false;

            try {
                socket = new Socket();
                int timeoutInMilliseconds = 5000;
                socket.connect(new InetSocketAddress(ipTimbangan1, port), timeoutInMilliseconds);
                socket.setSoTimeout(timeoutInMilliseconds);

                isConnected = true;
                SwingUtilities.invokeLater(() -> {
                    jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/timbangan3/Untitled_design__14_-removebg-preview.png")));
                });

                if (!wasConnected) {
                    SwingUtilities.invokeLater(() -> {
                        System.out.println("Koneksi timbangan 1 terhubung kembali");
                    });
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String grossValue = null;
                    String tareValue = null;
                    String netValue = null;
                    String line;

                    while (isConnected && (line = reader.readLine()) != null) {
                        line = line.trim();
                        OutputStream outputStream = socket.getOutputStream();
                        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
                        if (line.startsWith("GROSS")) {
                            grossValue = extractValue(line);
                            System.out.println(grossValue); 
                        } else if (line.startsWith("TARE")) {
                            tareValue = extractValue(line);
                            System.out.println(tareValue);
                        } else if (line.startsWith("NET")) {
                            netValue = extractValue(line);
                            System.out.println(netValue);
                            String defaultTanggal = getCurrentDate2();
                            String defaultJam = getCurrentTime();
                            writer.write("*"+ defaultTanggal + "     " + defaultJam + "#");
                            writer.flush();
                        } else if (line.startsWith("a")) {
                            System.out.println(line);
//                            String defaultResponse = "Data default\n";
//                            writer.write(defaultResponse);
//                            writer.flush();
                            jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/timbangan3/Untitled_design__14_-removebg-preview.png")));
                            continue;
                        }
                        System.out.println(line);
                                    String sumber = "Timbangan A (7)";
                                    String namaBarang = namaBarangTextField.getText();
                                    if (grossValue != null && tareValue != null && netValue != null && namaBarang != null) {
                                        // Anda telah mengumpulkan semua nilai yang diperlukan
                                    // Sekarang Anda dapat menampilkan atau memproses nilai-nilai ini sesuai kebutuhan.
                                    System.out.println("GROSS Value: " + grossValue);
                                    grossTextField.setText(grossValue);
                                    System.out.println("TARE Value: " + tareValue);
                                    tareTextField.setText(tareValue);
                                    System.out.println("NET Value: " + netValue);
                                    System.out.println("Nama Barang: " + namaBarang);
                                    receivedTimbanganA.setText(netValue);

                                    // Setelah memastikan nilai-nilai tidak null, baru simpan ke database
                                    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/3timbangan", "root", "")) {
                                        String query = "INSERT INTO berat (gross1, tare1, net1, nama_barang, tanggal, jam, sumber) VALUES (?, ?, ?, ?, ?, ?, ?)";
                                        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                                            preparedStatement.setString(1, grossValue);
                                            preparedStatement.setString(2, tareValue);
                                            preparedStatement.setString(3, netValue);
                                            preparedStatement.setString(4, namaBarang);
                                            preparedStatement.setString(5, getCurrentDate());
                                            preparedStatement.setString(6, getCurrentTime());
                                            preparedStatement.setString(7, sumber);
                                            preparedStatement.executeUpdate();
                                            System.out.println("Data berhasil disimpan ke database.");

                                            // Setelah data disimpan, tambahkan baris baru ke tabel
                                            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                                            Object[] newRow = {
                                                model.getRowCount() + 1,
                                                grossValue,
                                                tareValue,
                                                netValue,
                                                namaBarang,
                                                getCurrentDate(),
                                                getCurrentTime(),
                                                sumber,
                                            };
                                            model.insertRow(0, newRow);
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                        System.out.println("Error saving data to database: " + e.getMessage());
                                    }
                                        System.out.println("Data berhasil disimpan ke database.");
                                        grossValue = null;
                                        tareValue = null;
                                        netValue = null;
                                    }
                    }
                    System.out.println("Keluar dari while loop");
                } catch (SocketTimeoutException e) {
                    final Socket finalSocket = socket;
                    SwingUtilities.invokeLater(() -> {
                        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/timbangan3/Untitled_design__13_-removebg-preview.png")));
                        System.out.println("Timeout: Koneksi 1 terputus");
                        // Tutup socket
                        try {
                            finalSocket.close();
                            System.out.println("Socket ditutup");
                        } catch (IOException ex) {
                            System.out.println("Error closing socket: " + ex.getMessage());
                        }
                    });
                } catch (IOException e) {
                    isConnected = false;
                    SwingUtilities.invokeLater(() -> {
                        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/timbangan3/Untitled_design__13_-removebg-preview.png")));
                        System.out.println("Koneksi 1 terputus");
                    });
                }
            } catch (IOException e) {
                isConnected = false;
                SwingUtilities.invokeLater(() -> {
                    if (!wasConnected) {
                        System.out.println("Koneksi terputus");
                        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/timbangan3/Untitled_design__13_-removebg-preview.png")));
                    }
                });
            } finally {
                if (socket != null && !socket.isClosed()) {
                    try {
                        socket.close();
                        System.out.println("Socket ditutup");
                    } catch (IOException ex) {
                        System.out.println("Error closing socket: " + ex.getMessage());
                    }
                }

                if (!isConnected && wasConnected) {
                    wasConnected = false;
                } else if (isConnected && !wasConnected) {
                    wasConnected = true;
                }
            }
        }
    }
};

        
Runnable runnable2 = new Runnable(){
  boolean wasConnected2 = false; // Status koneksi sebelumnya

    @Override
    public void run() {
        while (true) {
            boolean isConnected2 = false; // Awalnya, anggap tidak terhubung
            boolean finalIsConnected2 = isConnected2; // Buat variabel yang bersifat final atau effectively final

            try (Socket socket = new Socket()) {
                // Set timeout untuk operasi koneksi dan pembacaan data
                int timeoutInMilliseconds = 5000; // Ganti dengan timeout yang diinginkan (dalam milidetik)
                socket.connect(new InetSocketAddress(ipTimbangan2, port), timeoutInMilliseconds);
                socket.setSoTimeout(timeoutInMilliseconds);

                isConnected2 = true; // Update status koneksi
                finalIsConnected2 = isConnected2; // Update variabel yang bersifat final atau effectively final
                SwingUtilities.invokeLater(() -> {
                    jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/timbangan3/Untitled_design__14_-removebg-preview.png")));
                });

                if (!wasConnected2) {
                    SwingUtilities.invokeLater(() -> {
                        System.out.println("Koneksi timbangan 2 terhubung kembali"); // Tampilkan pesan koneksi terhubung kembali di console
                    });
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String grossValue2 = null;
                    String tareValue2 = null;
                    String netValue2 = null;
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                    String tanggal = currentDateTime.format(dateFormatter);
                    String jam = currentDateTime.format(timeFormatter);
                    String line;

                    isConnected2 = true; // Koneksi berhasil

                    while (isConnected2 && (line = reader.readLine()) != null) {
                        line = line.trim();
                        OutputStream outputStream = socket.getOutputStream();
                        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
                        if (line.startsWith("GROSS")) {
                            grossValue2 = extractValue(line);
                            System.out.println(grossValue2);
                        } if (line.startsWith("TARE")) {
                            tareValue2 = extractValue(line);
                            System.out.println(tareValue2);
                        } if (line.startsWith("NET")) {
                            netValue2 = extractValue(line);
                            System.out.println(netValue2);
                            String defaultTanggal2 = getCurrentDate2();
                            String defaultJam2 = getCurrentTime();
                            writer.write("*"+ defaultTanggal2 + "     " + defaultJam2 + "#");
                            writer.flush();
                        } if (line.startsWith("a")) {
                            jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/timbangan3/Untitled_design__14_-removebg-preview.png")));
                        }

                        System.out.println(line);
                        String sumber2 = "Timbangan B (3)";
                        String namaBarang2 = namaBarangTextField2.getText();
                        if (grossValue2 != null && tareValue2 != null && netValue2 != null && namaBarang2 != null) {
                            // Anda telah mengumpulkan semua nilai yang diperlukan
                        // Sekarang Anda dapat menampilkan atau memproses nilai-nilai ini sesuai kebutuhan.
                        System.out.println("GROSS Value: " + grossValue2);
                        grossTextField2.setText(grossValue2);
                        System.out.println("TARE Value: " + tareValue2);
                        tareTextField2.setText(tareValue2);
                        System.out.println("NET Value: " + netValue2);
                        System.out.println("Nama Barang: " + namaBarang2);
                        receivedTimbanganB.setText(netValue2);

                        // Setelah memastikan nilai-nilai tidak null, baru simpan ke database
                        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/3timbangan", "root", "")) {
                            String query = "INSERT INTO berat (gross1, tare1, net1, nama_barang, tanggal, jam, sumber) VALUES (?, ?, ?, ?, ?, ?, ?)";
                            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                                preparedStatement.setString(1, grossValue2);
                                preparedStatement.setString(2, tareValue2);
                                preparedStatement.setString(3, netValue2);
                                preparedStatement.setString(4, namaBarang2);
                                preparedStatement.setString(5, getCurrentDate());
                                preparedStatement.setString(6, getCurrentTime());
                                preparedStatement.setString(7, sumber2);
                                preparedStatement.executeUpdate();
                                System.out.println("Data berhasil disimpan ke database.");

                                // Setelah data disimpan, tambahkan baris baru ke tabel
                                DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                                Object[] newRow = {
                                    model.getRowCount() + 1,
                                    grossValue2,
                                    tareValue2,
                                    netValue2,
                                    namaBarang2,
                                    getCurrentDate(),
                                    getCurrentTime(),
                                    sumber2,
                                };
                                model.insertRow(0, newRow);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            System.out.println("Error saving data to database: " + e.getMessage());
                        }
                            System.out.println("Data berhasil disimpan ke database.");
                            grossValue2 = null;
                            tareValue2 = null;
                            netValue2 = null;
                        }
                    }
                } catch (SocketTimeoutException e) {
                    // Tangani eksepsi jika waktu tunggu socket terlampaui
                    SwingUtilities.invokeLater(() -> {
                        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/timbangan3/Untitled_design__13_-removebg-preview.png")));
                        System.out.println("Timeout: Koneksi 2 terputus");
                    });
                } catch (IOException e) {
                    // Tangani eksepsi jika koneksi terputus
                    isConnected2 = false; // Set isConnected menjadi false
                    SwingUtilities.invokeLater(() -> {
                        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/timbangan3/Untitled_design__13_-removebg-preview.png")));
                        System.out.println("Koneksi 2 terputus"); // Tampilkan pesan koneksi terputus di console
                    });
                }
            } catch (IOException e) {
                // Tangani eksepsi jika koneksi gagal
                final boolean finalIsConnectedCopy2 = finalIsConnected2; // Buat salinan variabel yang bersifat final atau effectively final
                SwingUtilities.invokeLater(() -> {
                    
                    if (!finalIsConnectedCopy2) {
                        System.out.println("Koneksi 2 terputus"); // Tampilkan pesan koneksi terputus di console
                        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/timbangan3/Untitled_design__13_-removebg-preview.png")));
                    }
                });
            } finally {
                if (!isConnected2 && wasConnected2) {
                    wasConnected2 = false; // Reset status koneksi sebelumnya
                } else if (isConnected2 && !wasConnected2) {
                    wasConnected2 = true; // Update status koneksi sebelumnya
                }
            }
        }
    }  
};

Runnable runnable3 = new Runnable(){
  boolean wasConnected = false; // Status koneksi sebelumnya

    @Override
    public void run() {
        while (true) {
            boolean isConnected3 = false; // Awalnya, anggap tidak terhubung
            boolean finalIsConnected3 = isConnected3; // Buat variabel yang bersifat final atau effectively final

            try (Socket socket = new Socket()) {
                // Set timeout untuk operasi koneksi dan pembacaan data
                int timeoutInMilliseconds = 5000; // Ganti dengan timeout yang diinginkan (dalam milidetik)
                socket.connect(new InetSocketAddress(ipTimbangan3, port), timeoutInMilliseconds);
                socket.setSoTimeout(timeoutInMilliseconds);

                isConnected3 = true; // Update status koneksi
                finalIsConnected3 = isConnected3; // Update variabel yang bersifat final atau effectively final
                SwingUtilities.invokeLater(() -> {
                    jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/timbangan3/Untitled_design__14_-removebg-preview.png")));
                });

                if (!wasConnected) {
                    SwingUtilities.invokeLater(() -> {
                        System.out.println("Koneksi timbangan 3 terhubung kembali"); // Tampilkan pesan koneksi terhubung kembali di console
                    });
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String grossValue3 = null;
                    String tareValue3 = null;
                    String netValue3 = null;
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                    String tanggal = currentDateTime.format(dateFormatter);
                    String jam = currentDateTime.format(timeFormatter);
                    String line;

                    isConnected3 = true; // Koneksi berhasil

                    while (isConnected3 && (line = reader.readLine()) != null) {
                        line = line.trim();
                        OutputStream outputStream = socket.getOutputStream();
                        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
                        if (line.startsWith("GROSS")) {
                            grossValue3 = extractValue(line);
                            System.out.println(grossValue3);
                        } if (line.startsWith("TARE")) {
                            tareValue3 = extractValue(line);
                            System.out.println(tareValue3);
                        } if (line.startsWith("NET")) {
                            netValue3 = extractValue(line);
                            System.out.println(netValue3);
                            String defaultTanggal3 = getCurrentDate2();
                            String defaultJam3 = getCurrentTime();
                            writer.write("*"+ defaultTanggal3 + "     " + defaultJam3 + "#");
                            writer.flush();
                        } if (line.startsWith("a")) {
                            jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/timbangan3/Untitled_design__14_-removebg-preview.png")));
                        }

                        System.out.println(line);
                        String sumber = "Timbangan C (6)";
                        String namaBarang3 = namaBarangTextField3.getText();
                        if (grossValue3 != null && tareValue3 != null && netValue3 != null && namaBarang3 != null) {
                            // Anda telah mengumpulkan semua nilai yang diperlukan
                        // Sekarang Anda dapat menampilkan atau memproses nilai-nilai ini sesuai kebutuhan.
                        System.out.println("GROSS Value: " + grossValue3);
                        grossTextField3.setText(grossValue3);
                        System.out.println("TARE Value: " + tareValue3);
                        tareTextField3.setText(tareValue3);
                        System.out.println("NET Value: " + netValue3);
                        System.out.println("Nama Barang: " + namaBarang3);
                        receivedTimbanganC.setText(netValue3);

                        // Setelah memastikan nilai-nilai tidak null, baru simpan ke database
                        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/3timbangan", "root", "")) {
                            String query = "INSERT INTO berat (gross1, tare1, net1, nama_barang, tanggal, jam, sumber) VALUES (?, ?, ?, ?, ?, ?, ?)";
                            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                                preparedStatement.setString(1, grossValue3);
                                preparedStatement.setString(2, tareValue3);
                                preparedStatement.setString(3, netValue3);
                                preparedStatement.setString(4, namaBarang3);
                                preparedStatement.setString(5, getCurrentDate());
                                preparedStatement.setString(6, getCurrentTime());
                                preparedStatement.setString(7, sumber);
                                preparedStatement.executeUpdate();
                                System.out.println("Data berhasil disimpan ke database.");

                                // Setelah data disimpan, tambahkan baris baru ke tabel
                                DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                                Object[] newRow = {
                                    model.getRowCount() + 1,
                                    grossValue3,
                                    tareValue3,
                                    netValue3,
                                    namaBarang3,
                                    getCurrentDate(),
                                    getCurrentTime(),
                                    sumber,
                                };
                                model.insertRow(0, newRow);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            System.out.println("Error saving data to database: " + e.getMessage());
                        }
                            System.out.println("Data berhasil disimpan ke database.");
                            grossValue3 = null;
                            tareValue3 = null;
                            netValue3 = null;
                        }
                    }
                } catch (SocketTimeoutException e) {
                    // Tangani eksepsi jika waktu tunggu socket terlampaui
                    SwingUtilities.invokeLater(() -> {
                        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/timbangan3/Untitled_design__13_-removebg-preview.png")));
                        System.out.println("Timeout: Koneksi 3 terputus");
                    });
                } catch (IOException e) {
                    // Tangani eksepsi jika koneksi terputus
                    isConnected3 = false; // Set isConnected menjadi false
                    SwingUtilities.invokeLater(() -> {
                        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/timbangan3/Untitled_design__13_-removebg-preview.png")));
                        System.out.println("Koneksi 3 terputus"); // Tampilkan pesan koneksi terputus di console
                    });
                }
            } catch (IOException e) {
                // Tangani eksepsi jika koneksi gagal
                final boolean finalIsConnectedCopy = finalIsConnected3; // Buat salinan variabel yang bersifat final atau effectively final
                SwingUtilities.invokeLater(() -> {
                    
                    if (!finalIsConnectedCopy) {
                        System.out.println("Koneksi 3 terputus"); // Tampilkan pesan koneksi terputus di console
                        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/timbangan3/Untitled_design__13_-removebg-preview.png")));
                    }
                });
            } finally {
                if (!isConnected3 && wasConnected) {
                    wasConnected = false; // Reset status koneksi sebelumnya
                } else if (isConnected3 && !wasConnected) {
                    wasConnected = true; // Update status koneksi sebelumnya
                }
            }
        }
    }  
};
            executor.execute(runnable1);
            executor.execute(runnable2);
            executor.execute(runnable3);
            int rowNum = 1; // Nomor urut awal

            while (resultSet.next()) {
                Object[] row = {
                    rowNum,
                    resultSet.getString("gross1"),
                    resultSet.getString("tare1"),
                    resultSet.getString("net1"),
                    resultSet.getString("nama_barang"),
                    resultSet.getString("tanggal"),
                    resultSet.getString("jam"),
                    resultSet.getString("sumber"),
                }; 
                model.insertRow(0, row);
                rowNum++;
            }

            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    private static String extractValue(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length >= 2) {
            return parts[1];
        }
        return null; // Return null jika parsing gagal
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        titleLabel = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        receivedTimbanganA = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        receivedTimbanganB = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        receivedTimbanganC = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        grossTextField = new javax.swing.JLabel();
        tareTextField = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        tareTextField2 = new javax.swing.JLabel();
        grossTextField2 = new javax.swing.JLabel();
        tareTextField3 = new javax.swing.JLabel();
        grossTextField3 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        exportButton = new javax.swing.JButton();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        searchButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        namaBarangTextField = new javax.swing.JTextField();
        namaBarangTextField2 = new javax.swing.JTextField();
        namaBarangTextField3 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(25, 38, 85));

        jButton2.setBackground(new java.awt.Color(243, 240, 202));
        jButton2.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jButton2.setText("Data Operator");
        jButton2.setBorder(null);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(220, 53, 69));
        jButton3.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Logout");
        jButton3.setBorder(null);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        titleLabel.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        titleLabel.setForeground(new java.awt.Color(255, 255, 255));

        jButton4.setBackground(new java.awt.Color(243, 240, 202));
        jButton4.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jButton4.setText("IP Setting");
        jButton4.setBorder(null);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(titleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 556, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                        .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                        .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(56, 118, 191));

        jSeparator1.setBackground(new java.awt.Color(255, 255, 255));
        jSeparator1.setForeground(new java.awt.Color(255, 255, 255));
        jSeparator1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        jSeparator2.setBackground(new java.awt.Color(255, 255, 255));
        jSeparator2.setForeground(new java.awt.Color(255, 255, 255));
        jSeparator2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Timbangan A(7)");

        receivedTimbanganA.setBackground(new java.awt.Color(255, 255, 255));
        receivedTimbanganA.setFont(new java.awt.Font("Arial", 0, 48)); // NOI18N
        receivedTimbanganA.setForeground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Kg");

        jLabel4.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Timbangan B(3)");

        receivedTimbanganB.setBackground(new java.awt.Color(255, 255, 255));
        receivedTimbanganB.setFont(new java.awt.Font("Arial", 0, 48)); // NOI18N
        receivedTimbanganB.setForeground(new java.awt.Color(255, 255, 255));

        jLabel6.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Kg");

        jLabel7.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Timbangan C(6)");

        receivedTimbanganC.setBackground(new java.awt.Color(255, 255, 255));
        receivedTimbanganC.setFont(new java.awt.Font("Arial", 0, 48)); // NOI18N
        receivedTimbanganC.setForeground(new java.awt.Color(255, 255, 255));

        jLabel9.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Kg");

        jLabel10.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Gross");

        grossTextField.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        grossTextField.setForeground(new java.awt.Color(255, 255, 255));

        tareTextField.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        tareTextField.setForeground(new java.awt.Color(255, 255, 255));

        jLabel11.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Tare");

        jLabel12.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Tare");

        jLabel13.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Gross");

        tareTextField2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        tareTextField2.setForeground(new java.awt.Color(255, 255, 255));

        grossTextField2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        grossTextField2.setForeground(new java.awt.Color(255, 255, 255));

        tareTextField3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        tareTextField3.setForeground(new java.awt.Color(255, 255, 255));

        grossTextField3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        grossTextField3.setForeground(new java.awt.Color(255, 255, 255));

        jLabel14.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Gross");

        jLabel15.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Tare");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(101, 101, 101)
                                .addComponent(receivedTimbanganA, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)
                                .addComponent(jLabel3))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(grossTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(45, 45, 45)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tareTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(80, 80, 80))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 264, Short.MAX_VALUE)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(grossTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(54, 54, 54)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tareTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(101, 101, 101)
                        .addComponent(receivedTimbanganB, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6))
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(58, 58, 58)
                                .addComponent(receivedTimbanganC, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel9))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(grossTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(54, 54, 54)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tareTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(113, 113, 113))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addComponent(jSeparator2)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(receivedTimbanganA, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11)
                            .addComponent(tareTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                            .addComponent(grossTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(16, 16, 16))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel17))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel6)
                                        .addGap(7, 7, 7))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(32, 32, 32)
                                        .addComponent(receivedTimbanganB, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(receivedTimbanganC, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(tareTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(grossTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel13))
                            .addComponent(jLabel12)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(tareTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(grossTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel14))
                            .addComponent(jLabel15))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        jTable1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setEnabled(false);
        jTable1.setGridColor(new java.awt.Color(255, 255, 255));
        jTable1.setRowHeight(50);
        jTable1.setSelectionBackground(new java.awt.Color(243, 240, 202));
        jScrollPane1.setViewportView(jTable1);

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setText("Nama Barang Timbangan A");

        jLabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel5.setText("Nama Barang Timbangan B");

        jLabel8.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel8.setText("Nama Barang Timbangan C");

        exportButton.setBackground(new java.awt.Color(2, 110, 57));
        exportButton.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        exportButton.setForeground(new java.awt.Color(255, 255, 255));
        exportButton.setText("Export");
        exportButton.setBorder(null);
        exportButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exportButtonMouseEntered(evt);
            }
        });
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });

        searchButton.setText("Cari Data");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(searchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(resetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(exportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(namaBarangTextField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(namaBarangTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(345, 345, 345)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(namaBarangTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)))
                .addGap(19, 19, 19))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(searchButton, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                        .addComponent(resetButton, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(namaBarangTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(namaBarangTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(namaBarangTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(exportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public boolean isConnected(String ipAddress, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ipAddress, port), 1000); // Timeout set to 1 second (1000 milliseconds)
            System.out.println("Sambung");
            return true; // Koneksi berhasil
        } catch (IOException e) {
            System.out.println("Disconnect");
            return false; // Gagal terhubung
        }
    }

    
    private void exportButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exportButtonMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_exportButtonMouseEntered
    
    // Metode untuk menutup koneksi
    private void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void sendResponseToTimbangan1() {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(ipTimbangan1, port), 5000);
                OutputStream outputStream = socket.getOutputStream();
                String dataToSend = "data \n";
                outputStream.write(dataToSend.getBytes());
                outputStream.flush();
                // Mengirim data ke timbangan1...
                System.out.println("Respons dikirim ke ipTimbangan1");

                // Tambahkan delay (misalnya 5 detik) sebelum mencoba kembali
                Thread.sleep(2000);
                socket.close();
            } catch (SocketTimeoutException e) {
                System.err.println("Timeout connecting to ipTimbangan1. Retrying...");
            } catch (IOException e) {
                System.err.println("Error connecting to ipTimbangan1: " + e.getMessage());
                e.printStackTrace();

                // Tambahkan delay sebelum mencoba kembali
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    
    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        try{
           JFileChooser jFileChooser = new JFileChooser();
           jFileChooser.showSaveDialog(this);
           File saveFile = jFileChooser.getSelectedFile();
           
           if(saveFile != null){
               saveFile = new File(saveFile.toString()+".xlsx");
               Workbook wb = new XSSFWorkbook();
               Sheet sheet = wb.createSheet("Data");
               
               Row rowCol = sheet.createRow(0);
               for(int i=0;i<tableModel.getColumnCount();i++){
                   Cell cell = rowCol.createCell(i);
                   cell.setCellValue(tableModel.getColumnName(i));
               }
               
               for(int j=0;j<tableModel.getRowCount();j++){
                   Row row = sheet.createRow(j+1);
                   for(int k=0;k<tableModel.getColumnCount();k++){
                       Cell cell = row.createCell(k);
                       if(tableModel.getValueAt(j, k)!=null){
                           cell.setCellValue(tableModel.getValueAt(j, k).toString());
                       }
                   }
               }
               FileOutputStream out = new FileOutputStream(new File(saveFile.toString()));
               wb.write(out);
               wb.close();
               out.close();
               openFile(saveFile.toString());
           }else{
               JOptionPane.showMessageDialog(null,"Data tidak diexport!");
           }
       }catch(FileNotFoundException e){
           System.out.println(e);
       }catch(IOException io){
           System.out.println(io);
       }
    }//GEN-LAST:event_exportButtonActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        Operator operatorForm = new Operator();
        operatorForm.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        int confirm = JOptionPane.showConfirmDialog(null, "Anda yakin akan logout?", "Konfirmasi Hapus Data", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                LoginFrame loginForm = new LoginFrame();
                loginForm.setVisible(true);
                this.dispose();
            }else{
                return;
            }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        java.util.Date selectedDate = jDateChooser1.getDate();
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Pilih tanggal terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(selectedDate);

        String query = "SELECT gross1, tare1, net1, nama_barang, tanggal, jam, sumber FROM berat WHERE tanggal = ? ORDER BY id DESC";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/3timbangan", "root", "");
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, formattedDate);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Proses hasil pencarian (misalnya menampilkan data dalam tabel)
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0); // Mengosongkan tabel

            while (resultSet.next()) {
                int rowNum = tableModel.getRowCount() + 1;
                Object[] row = {
                    rowNum,
                    resultSet.getString("gross1"),
                    resultSet.getString("tare1"),
                    resultSet.getString("net1"),
                    resultSet.getString("nama_barang"),
                    resultSet.getString("tanggal"),
                    resultSet.getString("jam"),
                    resultSet.getString("sumber"),
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_searchButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        jDateChooser1.setDate(null);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new java.util.Date());

        String query = "SELECT * FROM berat WHERE tanggal = ? ORDER BY id DESC";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/3timbangan", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, today);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Proses hasil pencarian (misalnya menampilkan data dalam tabel)
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0); // Mengosongkan tabel

            while (resultSet.next()) {
                int rowNum = tableModel.getRowCount() + 1;
                Object[] row = {
                    rowNum,
                    resultSet.getString("gross1"),
                    resultSet.getString("tare1"),
                    resultSet.getString("net1"),
                    resultSet.getString("nama_barang"),
                    resultSet.getString("tanggal"),
                    resultSet.getString("jam"),
                    resultSet.getString("sumber")
                };
                model.insertRow(0, row);
                rowNum++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_resetButtonActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        closeConnection();
        ConfigFrame ipFrame = new ConfigFrame();
        ipFrame.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton4ActionPerformed

    public void openFile(String file){
        try{
            File path = new File(file);
            Desktop.getDesktop().open(path);
        }catch(IOException ioe){
            System.out.println(ioe);
        }
    }
    
    private String extractBeratFromReceivedData(String receivedData) {
        return receivedData;    
    }
    
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

    private String getCurrentDate2() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(new Date());
    }
    
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton exportButton;
    private javax.swing.JLabel grossTextField;
    private javax.swing.JLabel grossTextField2;
    private javax.swing.JLabel grossTextField3;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField namaBarangTextField;
    private javax.swing.JTextField namaBarangTextField2;
    private javax.swing.JTextField namaBarangTextField3;
    private javax.swing.JLabel receivedTimbanganA;
    private javax.swing.JLabel receivedTimbanganB;
    private javax.swing.JLabel receivedTimbanganC;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton searchButton;
    private javax.swing.JLabel tareTextField;
    private javax.swing.JLabel tareTextField2;
    private javax.swing.JLabel tareTextField3;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
}
