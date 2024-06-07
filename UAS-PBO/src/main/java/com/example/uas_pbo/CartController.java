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
import java.util.ResourceBundle;


public class CartController implements Initializable {

    @FXML
    private TableColumn<Cart, String> ColumnHarga;

    @FXML
    private TableColumn<Cart, String> ColumnJumlah;

    @FXML
    private TableColumn<Cart, String> ColumnKategori;

    @FXML
    private TableColumn<Cart, String> ColumnMerek;

    @FXML
    private TableColumn<Cart, String> ColumnNama;

    @FXML
    private TableColumn<Cart, String> ColumnNo;

    @FXML
    private TableColumn<Cart, String> ColumnTotal;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnCheckout;

    @FXML
    private TableView<Cart> table;

    Connection conn = DBConnection.connect();
    private static PreparedStatement pstmt;
    private static ResultSet rs;
    private ObservableList<Cart> ListData;
    private int userId;
    private String username;

    private void conn() {
        if (conn == null) {
            System.out.println("Koneksi Gagal");
            System.exit(1);
        }
    }

    public void setUserId(int userId) {
        this.userId = userId;
        this.username = getUsernameByUserId(userId);
        loadData();
    }

    private String getUsernameByUserId(int userId) {
        String username = null;
        try {
            String query = "SELECT username FROM Akun WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                username = rs.getString("username");
            } else {
                System.out.println("User ID not found: " + userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
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
            String query = "SELECT * FROM Cart WHERE User_Id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Cart cart = new Cart(
                        rs.getInt("No_Seri"),
                        rs.getString("Nama_Sparepart"),
                        rs.getString("Merek"),
                        rs.getString("Kategori"),
                        rs.getInt("Harga"),
                        rs.getInt("Jumlah"),
                        rs.getInt("Total_Harga")
                );
                ListData.add(cart);
            }
            table.setItems(ListData);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @FXML
    void Checkout(ActionEvent event) {
        ObservableList<Cart> selectedParts = table.getSelectionModel().getSelectedItems();
        if (selectedParts.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select at least one sparepart to buy.");
            return;
        }

        try {
            for (Cart selectedPart : selectedParts) {
                int quantity = selectedPart.getJumlah();

                int currentStock = getStockFromSpareparts(selectedPart.getNo_Seri());
                if (currentStock < quantity) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Not enough stock available for " + selectedPart.getNama_Sparepart());
                    return;
                }

                int totalPrice = selectedPart.getHarga() * quantity;

                String insertQuery = "INSERT INTO transaksi (No_Seri, Id, Username, Nama_Sparepart, Tanggal_Transaksi, Jumlah, Harga, Total_Harga) VALUES (?, ?, ?, ?, CURDATE(), ?, ?, ?)";
                pstmt = conn.prepareStatement(insertQuery);
                pstmt.setInt(1, selectedPart.getNo_Seri());
                pstmt.setInt(2, userId); // Use userId instead of authenticatedUserId
                pstmt.setString(3, username); // Use username from getUsernameByUserId method
                pstmt.setString(4, selectedPart.getNama_Sparepart());
                pstmt.setInt(5, quantity);
                pstmt.setInt(6, selectedPart.getHarga());
                pstmt.setInt(7, totalPrice);

                pstmt.executeUpdate();

                int remainingStock = currentStock - quantity;
                String updateQuery = "UPDATE tbsparepart SET Stok = ? WHERE No_Seri = ?";
                pstmt = conn.prepareStatement(updateQuery);
                pstmt.setInt(1, remainingStock);
                pstmt.setInt(2, selectedPart.getNo_Seri());
                pstmt.executeUpdate();


                String deleteQuery = "DELETE FROM Cart WHERE No_Seri = ? AND User_Id = ?";
                pstmt = conn.prepareStatement(deleteQuery);
                pstmt.setInt(1, selectedPart.getNo_Seri());
                pstmt.setInt(2, userId);
                pstmt.executeUpdate();
            }

            showAlert(Alert.AlertType.INFORMATION, "Success", "Transaction completed successfully!");

            loadData(); // Refresh data di tabel cart
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to complete transaction.");
            e.printStackTrace();
        }
    }

    private int getStockFromSpareparts(int noSeri) throws SQLException {
        String query = "SELECT Stok FROM tbsparepart WHERE No_Seri = ?";
        pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, noSeri);
        rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("Stok");
        }
        return 0;
    }

    @FXML
    void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/User.fxml"));
            Parent root = loader.load();
            UserController userController = loader.getController();
            userController.setUserId(this.userId); // Kembalikan userId ke UserController

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            
            Stage currentStage = (Stage) btnBack.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load user form");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ColumnNo.setCellValueFactory(new PropertyValueFactory<>("no_Seri"));
        ColumnNama.setCellValueFactory(new PropertyValueFactory<>("nama_Sparepart"));
        ColumnMerek.setCellValueFactory(new PropertyValueFactory<>("merek"));
        ColumnKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        ColumnHarga.setCellValueFactory(new PropertyValueFactory<>("harga"));
        ColumnJumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
        ColumnTotal.setCellValueFactory(new PropertyValueFactory<>("totalHarga"));

        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        loadData();
    }
}


