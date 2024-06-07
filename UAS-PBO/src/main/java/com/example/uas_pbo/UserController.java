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
import java.util.Optional;
import java.util.ResourceBundle;


public class UserController implements Initializable {

    @FXML
    private TableColumn<Spareparts, String> ColumnHarga;

    @FXML
    private TableColumn<Spareparts, String> ColumnMerek;

    @FXML
    private TableColumn<Spareparts, String> ColumnKategori;

    @FXML
    private TableColumn<Spareparts, String> ColumnNama;

    @FXML
    private TableColumn<Spareparts, String> ColumnNo;

    @FXML
    private TableColumn<Spareparts,String> ColumnStok;

    @FXML
    private TableColumn<Spareparts, String> ColumnTanggal;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnBuy;

    @FXML
    private Button btnCart;

    @FXML
    private TextField txtSearch;

    @FXML
    private TableView<Spareparts> table;

    Connection conn = DBConnection.connect();
    private static PreparedStatement pstmt;
    private static ResultSet rs;
    public static int authenticatedUserId;
    private ObservableList<Spareparts> ListData ;

    private void conn() {
        if (conn == null) {
            System.out.println("Koneksi Gagal");
            System.exit(1);
        }
    }

    private int userId;

    public void setUserId(int userId) {
        this.userId = userId;
        UserController.authenticatedUserId = userId;
        loadData();
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    private void loadData() {
        conn();
        ListData = FXCollections.observableArrayList();
        try {
            String query = "SELECT * FROM tbsparepart WHERE Stok > 0";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Spareparts part = new Spareparts(0,null,null,null,null,0, 0);
                part.setNoSeri(rs.getInt("No_Seri"));
                part.setNamaSparepart(rs.getString("Nama_Sparepart"));
                part.setMerek(rs.getString("Merek"));
                part.setKategori(rs.getString("Kategori"));
                part.setTanggal(rs.getString("Tanggal"));
                part.setStok(rs.getInt("Stok"));
                part.setHarga(rs.getInt("Harga"));
                if (part.getStok() > 0) {
                    ListData.add(part);
                }
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
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load login form");
        }
    }




    @FXML
    void handleBuy(ActionEvent event) {
        Spareparts selectedPart = table.getSelectionModel().getSelectedItem();
        if (selectedPart == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Silakan pilih sparepart yang ingin dibeli.");
            return;
        }

        if (selectedPart.getStok() == 0) {
            showAlert(Alert.AlertType.ERROR, "Error", "Sparepart yang dipilih habis stoknya.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Masukkan Jumlah");
        dialog.setHeaderText("Masukkan jumlah yang ingin dibeli:");
        dialog.setContentText("Jumlah:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int quantity = Integer.parseInt(result.get());
                if (quantity <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Silakan masukkan jumlah yang valid.");
                    return;
                }

                if (selectedPart.getStok() < quantity) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Stok tidak cukup.");
                    return;
                }

                int totalPrice = selectedPart.getHarga() * quantity;

                String checkQuery = "SELECT * FROM Cart WHERE User_Id = ? AND No_Seri = ?";
                pstmt = conn.prepareStatement(checkQuery);
                pstmt.setInt(1, userId);  // Use userId instead of authenticatedUserId
                pstmt.setInt(2, selectedPart.getNoSeri());
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    int existingQuantity = rs.getInt("Jumlah");
                    int newQuantity = existingQuantity + quantity;
                    int newTotalPrice = selectedPart.getHarga() * newQuantity;

                    String updateQuery = "UPDATE Cart SET Jumlah = ?, Total_Harga = ? WHERE User_Id = ? AND No_Seri = ?";
                    pstmt = conn.prepareStatement(updateQuery);
                    pstmt.setInt(1, newQuantity);
                    pstmt.setInt(2, newTotalPrice);
                    pstmt.setInt(3, userId);
                    pstmt.setInt(4, selectedPart.getNoSeri());

                    pstmt.executeUpdate();

                    showAlert(Alert.AlertType.INFORMATION, "Success", "Jumlah item di keranjang berhasil diperbarui!");
                } else {
                    String insertQuery = "INSERT INTO Cart (User_Id, No_Seri, Nama_Sparepart, Merek, Kategori, Harga, Jumlah, Total_Harga) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    pstmt = conn.prepareStatement(insertQuery);
                    pstmt.setInt(1, userId);  // Use userId instead of authenticatedUserId
                    pstmt.setInt(2, selectedPart.getNoSeri());
                    pstmt.setString(3, selectedPart.getNamaSparepart());
                    pstmt.setString(4, selectedPart.getMerek());
                    pstmt.setString(5, selectedPart.getKategori());
                    pstmt.setInt(6, selectedPart.getHarga());
                    pstmt.setInt(7, quantity);
                    pstmt.setInt(8, totalPrice);

                    pstmt.executeUpdate();

                    showAlert(Alert.AlertType.INFORMATION, "Success", "Item berhasil ditambahkan ke keranjang!");
                }

                loadData();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Silakan masukkan jumlah yang valid.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal menambahkan item ke keranjang.");
                e.printStackTrace();
            }
        }
    }


    @FXML
    void handleCart(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Cart.fxml"));
            Parent root = loader.load();
            CartController cartController = loader.getController();
            cartController.setUserId(this.userId); // Pastikan userId diteruskan dengan benar

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) btnCart.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load cart form");
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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

        btnLogout.setOnAction(this::handleLogout);
        btnBuy.setOnAction(actionEvent -> {
            handleBuy(actionEvent);
        });
    }
}
