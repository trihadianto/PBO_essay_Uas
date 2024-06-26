package Gudang_Barang;

import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FormBarang extends JFrame {
    private String[] judul = { "Kode Barang", "Nama Barang", "Harga Barang", "Stok Barang" };
    DefaultTableModel df;
    JTable tab = new JTable();
    JScrollPane scp = new JScrollPane();
    JPanel pnl = new JPanel();
    JLabel lblKodeBarang = new JLabel("Kode Barang");
    JTextField txKodeBarang = new JTextField(10);
    JLabel lblNamaBarang = new JLabel("Nama Barang");
    JTextField txNamaBarang = new JTextField(20);
    JLabel lblHargaBarang = new JLabel("Harga Barang");
    JTextField txHargaBarang = new JTextField(10);
    JLabel lblStokBarang = new JLabel("Stok Barang");
    JTextField txStokBarang = new JTextField(10);
    JButton btAdd = new JButton("Simpan");
    JButton btNew = new JButton("Baru");
    JButton btDel = new JButton("Hapus");
    JButton btEdit = new JButton("Ubah");
    Connection cn; // Added Connection object

    FormBarang() {
        super("Data Barang");
        setSize(460, 300);
        pnl.setLayout(null);

        pnl.add(lblKodeBarang);
        lblKodeBarang.setBounds(20, 10, 100, 20);
        pnl.add(txKodeBarang);
        txKodeBarang.setBounds(125, 10, 100, 20);

        pnl.add(lblNamaBarang);
        lblNamaBarang.setBounds(20, 33, 100, 20);
        pnl.add(txNamaBarang);
        txNamaBarang.setBounds(125, 33, 175, 20);

        pnl.add(lblHargaBarang);
        lblHargaBarang.setBounds(20, 56, 100, 20);
        pnl.add(txHargaBarang);
        txHargaBarang.setBounds(125, 56, 175, 20);

        pnl.add(lblStokBarang);
        lblStokBarang.setBounds(20, 79, 100, 20);
        pnl.add(txStokBarang);
        txStokBarang.setBounds(125, 79, 175, 20);

        pnl.add(btNew);
        btNew.setBounds(320, 10, 100, 20);
        btNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btNewAksi(e);
            }
        });

        pnl.add(btAdd);
        btAdd.setBounds(320, 33, 100, 20);
        btAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btAddAksi(e);
            }
        });

        pnl.add(btEdit);
        btEdit.setBounds(320, 56, 100, 20);
        btEdit.setEnabled(false);
        btEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btEditAksi(e);
            }
        });

        pnl.add(btDel);
        btDel.setBounds(320, 79, 100, 20);
        btDel.setEnabled(false);
        btDel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btDelAksi(e);
            }
        });

        df = new DefaultTableModel(null, judul);
        tab.setModel(df);
        scp.getViewport().add(tab);
        tab.setEnabled(true);
        tab.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                tabMouseClicked(evt);
            }
        });

        scp.setBounds(20, 110, 405, 130);
        pnl.add(scp);
        getContentPane().add(pnl);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Connect to the database
        cn = new Connect_DB().getConnect();
        if (cn == null) {
            System.err.println("Failed to connect to the database");
            System.exit(1);
        }

        // Load data from the database
        loadData();
    }

    void loadData() {
        try (Statement st = cn.createStatement()) {
            String sql = "SELECT * FROM table_barang";
            try (ResultSet rs = st.executeQuery(sql)) {
                clearTable();
                while (rs.next()) {
                    String kode = rs.getString("Kode_Barang");
                    String nama = rs.getString("Nama_Barang");
                    String harga = rs.getString("Harga_Barang");
                    String stok = rs.getString("Stok_Barang");
                    String[] data = { kode, nama, harga, stok };
                    df.addRow(data);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    void clearTable() {
        int numRow = df.getRowCount();
        for (int i = 0; i < numRow; i++) {
            df.removeRow(0);
        }
    }

    void clearTextField() {
        txKodeBarang.setText(null);
        txNamaBarang.setText(null);
        txHargaBarang.setText(null);
        txStokBarang.setText(null);
    }

    void simpanData(Tabel_Barang B) {
        try {
            String sql = "INSERT INTO tabel_barang (Kode_Barang, Nama_Barang, Harga_Barang, Stok_Barang) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, B.getKodeBarang());
            ps.setString(2, B.getNamaBarang());
            ps.setString(3, B.getHargaBarang());
            ps.setString(4, B.getStokBarang());
            int result = ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data Berhasil Disimpan",
                    "Info Proses", JOptionPane.INFORMATION_MESSAGE);
            String[] data = { B.getKodeBarang(), B.getNamaBarang(), B.getHargaBarang(), B.getStokBarang() };
            df.addRow(data);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    void hapusData(String kode) {
        try {
            String sql = "DELETE FROM tabel_barang WHERE Kode_Barang = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, kode);
            int result = ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data Berhasil Dihapus", "Info Proses",
                    JOptionPane.INFORMATION_MESSAGE);
            df.removeRow(tab.getSelectedRow());
            clearTextField();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    void ubahData(Tabel_Barang B, String kode) {
        try {
            String sql = "UPDATE table_barang SET Kode_Barang = ?, Nama_Barang = ?, Harga_Barang = ?, Stok_Barang = ? WHERE Kode_Barang = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, B.getKodeBarang());
            ps.setString(2, B.getNamaBarang());
            ps.setString(3, B.getHargaBarang());
            ps.setString(4, B.getStokBarang());
            ps.setString(5, kode);
            int result = ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data Berhasil Diubah", "Info Proses",
                    JOptionPane.INFORMATION_MESSAGE);
            clearTable();
            loadData();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btNewAksi(ActionEvent evt) {
        clearTextField();
        btEdit.setEnabled(false);
        btDel.setEnabled(false);
        btAdd.setEnabled(true);
    }

    private void btAddAksi(ActionEvent evt) {
        Tabel_Barang B = new Tabel_Barang();
        B.setKodeBarang(txKodeBarang.getText());
        B.setNamaBarang(txNamaBarang.getText());
        B.setHargaBarang(txHargaBarang.getText());
        B.setStokBarang(txStokBarang.getText());
        simpanData(B);
    }

    private void btDelAksi(ActionEvent evt) {
        int status;
        status = JOptionPane.showConfirmDialog(null, "Yakin data akan dihapus?",
                "Konfirmasi", JOptionPane.OK_CANCEL_OPTION);
        if (status == 0) {
            hapusData(txKodeBarang.getText());
        }
    }

    private void btEditAksi(ActionEvent evt) {
        Tabel_Barang B = new Tabel_Barang();
        B.setKodeBarang(txKodeBarang.getText());
        B.setNamaBarang(txNamaBarang.getText());
        B.setHargaBarang(txHargaBarang.getText());
        B.setStokBarang(txStokBarang.getText());
        ubahData(B, txKodeBarang.getText());
    }

    private void tabMouseClicked(MouseEvent evt) {
        int row = tab.getSelectedRow();
        txKodeBarang.setText(tab.getValueAt(row, 0).toString());
        txNamaBarang.setText(tab.getValueAt(row, 1).toString());
        txHargaBarang.setText(tab.getValueAt(row, 2).toString());
        txStokBarang.setText(tab.getValueAt(row, 3).toString());
        btEdit.setEnabled(true);
        btDel.setEnabled(true);
        btAdd.setEnabled(false);
    }

    public static void main(String[] args) {
        new FormBarang();
    }
}
