package com.example.uas_pbo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    private TableColumn<Spareparts, String> ColumnHarga;

    @FXML
    private TableColumn<?, ?> ColumnKategori;

    @FXML
    private TableColumn<Spareparts, String> ColumnMerek;

    @FXML
    private TableColumn<Spareparts, String> ColumnNama;

    @FXML
    private TableColumn<Spareparts, String> ColumnNo;

    @FXML
    private TableColumn<Spareparts, String> ColumnStok;

    @FXML
    private TableColumn<Spareparts, String> ColumnTanggal;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnUpdate;

    @FXML
    private Button btnLogout;

    @FXML
    private ComboBox<String> cbMerek;

    @FXML
    private DatePicker date;

    @FXML
    private RadioButton rbGenuine;

    @FXML
    private RadioButton rbOem;

    @FXML
    private TableView<Spareparts> table;

    @FXML
    private TextField txtHarga;

    @FXML
    private TextField txtNama;

    @FXML
    private TextField txtNoSeri;

    @FXML
    private TextField txtSearch;

    @FXML
    private TextField txtStok;

    Connection conn = DBConnection.connect();
    private static PreparedStatement pstmt;
    private static ResultSet rs;
    private ObservableList<Spareparts> ListData;
    private String imagePath;
    private int selectedItemID;

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clear() {
        txtNama.clear();
        txtNoSeri.clear();
        cbMerek.setValue(null);
        date.setValue(null);
        txtStok.clear();
        txtHarga.clear();
    }

    @FXML
    void ClearField(ActionEvent event) {
        clear();
    }

    @FXML
    void CreateSparepart(ActionEvent event) {
        try {
            if (txtNoSeri.getText().isEmpty() || txtNama.getText().isEmpty() || cbMerek.getValue() == null ||
                    selectKategori() == null || date.getValue() == null || txtStok.getText().isEmpty() || txtHarga.getText().isEmpty()) {
                showAlert("Harap isi semua kolom.");
                return;
            }

            int noSeri = Integer.parseInt(txtNoSeri.getText());
            int stok = Integer.parseInt(txtStok.getText());
            int harga = Integer.parseInt(txtHarga.getText());

            // Validate integer fields
            if (noSeri <= 0 || stok <= 0 || harga <= 0) {
                showAlert("No Seri, Stok, dan Harga harus lebih besar dari nol.");
                return;
            }

            Spareparts sparepart = new Spareparts(0, null, null, null, null, 0, 0);
            sparepart.setNoSeri(noSeri);
            sparepart.setNamaSparepart(txtNama.getText());
            sparepart.setMerek(cbMerek.getValue());
            sparepart.setKategori(selectKategori());
            sparepart.setTanggal(date.getValue().toString());
            sparepart.setStok(stok);
            sparepart.setHarga(harga);

            PreparedStatement checkStatement = conn.prepareStatement("SELECT COUNT(*) FROM tbsparepart WHERE No_Seri = ?");
            checkStatement.setInt(1, noSeri);
            ResultSet rs = checkStatement.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                showAlert("Sparepart dengan No Seri yang sama sudah ada.");
                return;
            }

            PreparedStatement statement = conn.prepareStatement("INSERT INTO tbsparepart VALUES (?,?,?,?,?,?,?)");
            statement.setInt(1, sparepart.getNoSeri());
            statement.setString(2, sparepart.getNamaSparepart());
            statement.setString(3, sparepart.getMerek());
            statement.setString(4, sparepart.getKategori());
            statement.setString(5, sparepart.getTanggal());
            statement.setInt(6, sparepart.getStok());
            statement.setInt(7, sparepart.getHarga());
            statement.executeUpdate();

            clear();
            showAlert("Data berhasil ditambah");
            loadData();
        } catch (SQLException e) {
            showAlert("Gagal menambahkan sparepart.");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            showAlert("Harap masukkan angka yang valid untuk No Seri, Stok, dan Harga.");
            e.printStackTrace();
        }
    }


    @FXML
    void DeleteSparepart(ActionEvent event) {
        try {
            int NoSeri = Integer.parseInt(txtNoSeri.getText());

            String checkQuery = "SELECT COUNT(*) FROM tbsparepart WHERE No_Seri = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, NoSeri);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                showAlert("Data ID tidak ada");
                return;
            }

            PreparedStatement statement = conn.prepareStatement("DELETE FROM tbsparepart WHERE No_Seri = ?");
            statement.setInt(1, NoSeri);
            statement.executeUpdate();
            clear();
            showAlert("Data berhasil dihapus");
            loadData();
        } catch (SQLException e) {
            showAlert("Kesalahan database! Kesalahan memeriksa keberadaan ID.");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            showAlert("Harap masukkan angka yang valid untuk No Seri.");
            e.printStackTrace();
        }
    }

    @FXML
    void UpdateSparepart(ActionEvent event) {
        try {
            // Check if any field is empty
            if (txtNoSeri.getText().isEmpty() || txtNama.getText().isEmpty() || cbMerek.getValue() == null ||
                    selectKategori() == null || date.getValue() == null || txtStok.getText().isEmpty() || txtHarga.getText().isEmpty()) {
                showAlert("Harap isi semua kolom.");
                return;
            }

            int noSeri = Integer.parseInt(txtNoSeri.getText());
            int stok = Integer.parseInt(txtStok.getText());
            int harga = Integer.parseInt(txtHarga.getText());

            // Validate integer fields
            if (noSeri <= 0 || stok <= 0 || harga <= 0) {
                showAlert("No Seri, Stok, dan Harga harus lebih besar dari nol.");
                return;
            }

            String nama = txtNama.getText();
            String merek = cbMerek.getValue();
            String kategori = selectKategori();
            String tanggal = date.getValue().toString();

            PreparedStatement statement = conn.prepareStatement(
                    "UPDATE tbsparepart SET Nama_Sparepart = ?, Merek = ?, Kategori = ?, Tanggal = ?, Stok = ?, Harga = ? WHERE No_Seri = ?"
            );
            statement.setString(1, nama);
            statement.setString(2, merek);
            statement.setString(3, kategori);
            statement.setString(4, tanggal);
            statement.setInt(5, stok);
            statement.setInt(6, harga);
            statement.setInt(7, noSeri);
            statement.executeUpdate();
            clear();
            showAlert("Data berhasil diUpdate");
            loadData();
        } catch (SQLException e) {
            showAlert("Gagal memperbarui sparepart.");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            showAlert("Harap masukkan angka yang valid untuk No Seri, Stok, dan Harga.");
            e.printStackTrace();
        }
    }


    private void conn() {
        if (conn == null) {
            System.out.println("Koneksi Gagal");
            System.exit(1);
        }
    }

    String selectKategori() {
        if (rbGenuine.isSelected()) {
            return "Genuine";
        } else if (rbOem.isSelected()) {
            return "Oem";
        }
        return null;
    }

    private void filterTable(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            table.setItems(ListData);
            return;
        }

        ObservableList<Spareparts> filteredList = FXCollections.observableArrayList();

        for (Spareparts part : ListData) {
            if (part.getNamaSparepart().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(part);
            }
        }

        table.setItems(filteredList);
    }

    private void loadData() {
        conn();
        ListData = FXCollections.observableArrayList();
        try {
            String query = "SELECT * FROM tbsparepart";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Spareparts part = new Spareparts(0, null, null, null, null, 0, 0);
                part.setNoSeri(rs.getInt("No_Seri"));
                part.setNamaSparepart(rs.getString("Nama_Sparepart"));
                part.setMerek(rs.getString("Merek"));
                part.setKategori(rs.getString("Kategori"));
                part.setTanggal(rs.getString("Tanggal"));
                part.setStok(rs.getInt("Stok"));
                part.setHarga(rs.getInt("Harga"));
                ListData.add(part);
            }
            table.setItems(ListData);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @FXML
    void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Login.fxml"));
            Parent root = loader.load();
            Stage currentStage = (Stage) btnLogout.getScene().getWindow();
            currentStage.close();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Gagal memuat form login.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> mereks = FXCollections.observableArrayList("Toyota", "Honda", "Nissan", "Mitsubishi", "Suzuki");
        cbMerek.setItems(mereks);
        ColumnNo.setCellValueFactory(new PropertyValueFactory<>("noSeri"));
        ColumnNama.setCellValueFactory(new PropertyValueFactory<>("namaSparepart"));
        ColumnMerek.setCellValueFactory(new PropertyValueFactory<>("merek"));
        ColumnKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        ColumnTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        ColumnStok.setCellValueFactory(new PropertyValueFactory<>("stok"));
        ColumnHarga.setCellValueFactory(new PropertyValueFactory<>("harga"));

        loadData();

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTable(newValue);
        });

        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                txtNoSeri.setText(String.valueOf(newValue.getNoSeri()));
                txtNama.setText(newValue.getNamaSparepart());
                cbMerek.setValue(newValue.getMerek());
                rbGenuine.setSelected(false);
                rbOem.setSelected(false);

                switch (newValue.getKategori()) {
                    case "Genuine":
                        rbGenuine.setSelected(true);
                        break;
                    case "Oem":
                        rbOem.setSelected(true);
                        break;
                }
                String dateString = newValue.getTanggal();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate localDate = LocalDate.parse(dateString, formatter);
                date.setValue(localDate);
                txtStok.setText(String.valueOf(newValue.getStok()));
                txtHarga.setText(String.valueOf(newValue.getHarga()));
            }
        });

        btnSave.setOnAction(actionEvent -> {
            try {
                CreateSparepart(actionEvent);
            } catch (Exception throwables) {
                throwables.printStackTrace();
            }
        });

        btnUpdate.setOnAction(actionEvent -> {
            try {
                UpdateSparepart(actionEvent);
            } catch (Exception throwables) {
                throwables.printStackTrace();
            }
        });

        btnDelete.setOnAction(actionEvent -> {
            try {
                DeleteSparepart(actionEvent);
            } catch (Exception throwables) {
                throwables.printStackTrace();
            }
        });
    }
}
