/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package timbangan3;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author yogi
 */
public class ConfigFrame extends javax.swing.JFrame {

    /**
     * Creates new form Config
     */
    public ConfigFrame() {
        initComponents();
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/3timbangan", "username", "password");
            String query = "SELECT * FROM config";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            
            int rowNum = 1; // Nomor urut awal
            while (resultSet.next()) {
                Object[] row = {
                    rowNum,
                    resultSet.getString("port1"),
                    resultSet.getString("port2"),
                    resultSet.getString("port3")
                };
                model.addRow(row);
                rowNum++;
            }
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        saveButton = new javax.swing.JButton();
        ipAdress1 = new javax.swing.JTextField();
        ipAdress2 = new javax.swing.JTextField();
        ipAdress3 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        timbangButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        saveButton.setText("Simpan");
        saveButton.setEnabled(false);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel1.setText("IP Address Timbangan A");

        jLabel2.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel2.setText("IP Address Timbangan B");

        jLabel3.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel3.setText("IP Address Timbangan C");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "IP Timbangan A", "IP Timbangan B", "IP Timbangan C"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jPanel1.setBackground(new java.awt.Color(25, 38, 85));

        timbangButton.setBackground(new java.awt.Color(243, 240, 202));
        timbangButton.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        timbangButton.setText("Timbang");
        timbangButton.setBorder(null);
        timbangButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timbangButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(1198, Short.MAX_VALUE)
                .addComponent(timbangButton, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(timbangButton, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                .addContainerGap())
        );

        editButton.setText("Edit");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ipAdress1, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(ipAdress3, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ipAdress2, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(editButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 664, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(152, 152, 152))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(101, 101, 101)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ipAdress1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addGap(7, 7, 7)
                        .addComponent(ipAdress2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ipAdress3, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(editButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(244, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        String ip1 = ipAdress1.getText();
        String ip2 = ipAdress2.getText();
        String ip3 = ipAdress3.getText();
        // Validasi kode barang dan nama barang tidak kosong
        if (ip1.isEmpty() || ip2.isEmpty() || ip3.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return; // Tidak melanjutkan simpan data
        }
        // Simpan data ke database
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/3timbangan", "root", "");
            String query = "INSERT INTO config (port1, port2, port3) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, ip1);
            preparedStatement.setString(2, ip2);
            preparedStatement.setString(3, ip3);
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        // Refresh tabel dengan memanggil fireTableDataChanged()
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.addRow(new Object[]{model.getRowCount() + 1, ip1, ip2, ip3});
        model.fireTableDataChanged();
        // Reset input fields
        ipAdress1.setText("");
        ipAdress2.setText("");
        ipAdress3.setText("");
    }//GEN-LAST:event_saveButtonActionPerformed

    private void timbangButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timbangButtonActionPerformed
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_timbangButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow >= 0) {
            String ip1 = ipAdress1.getText();
            String ip2 = ipAdress2.getText();
            String ip3 = ipAdress3.getText();
            if (ip1.isEmpty() || ip2.isEmpty() || ip3.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Kode barang dan nama barang harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return; // Tidak melanjutkan simpan data
            }
            // Mengambil id dari baris terpilih di jTable1
            String id = jTable1.getValueAt(selectedRow, 0).toString();
            try {
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/3timbangan", "root", "");
                String query = "UPDATE config SET port1=?, port2=?, port3=? WHERE id=?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, ip1);
                preparedStatement.setString(2, ip2);
                preparedStatement.setString(3, ip3);
                preparedStatement.setString(4, id); // Menggunakan kode_barang yang lama untuk identifikasi data yang ingin diubah
                preparedStatement.executeUpdate();
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setValueAt(ip1, selectedRow, 1);
            model.setValueAt(ip2, selectedRow, 2);
            model.setValueAt(ip3, selectedRow, 3);

            ipAdress1.setText("");
            ipAdress2.setText("");
            ipAdress3.setText("");
        } else {
            JOptionPane.showMessageDialog(null, "Pilih baris yang ingin diubah.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_editButtonActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if (evt.getClickCount() == 1) {
            int selectedRow = jTable1.getSelectedRow();
            if (selectedRow >= 0) {
                String ip1 = (String) jTable1.getValueAt(selectedRow, 1);
                String ip2 = (String) jTable1.getValueAt(selectedRow, 2);
                String ip3 = (String) jTable1.getValueAt(selectedRow, 3);
                
                ipAdress1.setText(ip1);
                ipAdress2.setText(ip2);
                ipAdress3.setText(ip3);
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

public List<String> getIpAddressesFromDatabase() {
    List<String> ipAddresses = new ArrayList<>();

    try {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/3timbangan", "root", "");
        String query = "SELECT port1, port2, port3 FROM config";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        if (resultSet.next()) {
            String ip1 = resultSet.getString("port1");
            String ip2 = resultSet.getString("port2");
            String ip3 = resultSet.getString("port3");

            ipAddresses.add(ip1);
            ipAddresses.add(ip2);
            ipAddresses.add(ip3);
        }

        connection.close();
    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    return ipAddresses;
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
            java.util.logging.Logger.getLogger(ConfigFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConfigFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConfigFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConfigFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ConfigFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton editButton;
    private javax.swing.JTextField ipAdress1;
    private javax.swing.JTextField ipAdress2;
    private javax.swing.JTextField ipAdress3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton timbangButton;
    // End of variables declaration//GEN-END:variables
}
