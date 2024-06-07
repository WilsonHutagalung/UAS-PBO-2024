package com.example.uas_pbo;

public class Cart {

    private int No_Seri;
    private String Nama_Sparepart;
    private String Merek;
    private String Kategori;
    private int Harga;
    private int Jumlah;
    private int totalHarga;


    public Cart(int no_seri,String nama_Sparepart, String merek, String kategori, int harga, int jumlah, int totalHarga) {
        this.No_Seri = no_seri;
        this.Nama_Sparepart = nama_Sparepart;
        this.Merek = merek;
        this.Kategori = kategori;
        this.Harga = harga;
        this.Jumlah = jumlah;
        this.totalHarga = totalHarga;
    }

    public int getNo_Seri() {
        return No_Seri;
    }

    public void setNo_Seri(int no_Seri) {
        No_Seri = no_Seri;
    }

    public String getNama_Sparepart() {
        return Nama_Sparepart;
    }

    public void setNama_Sparepart(String nama_Sparepart) {
        Nama_Sparepart = nama_Sparepart;
    }

    public String getMerek() {
        return Merek;
    }

    public void setMerek(String merek) {
        Merek = merek;
    }

    public String getKategori() {
        return Kategori;
    }

    public void setKategori(String kategori) {
        Kategori = kategori;
    }

    public int getHarga() {
        return Harga;
    }

    public void setHarga(int harga) {
        Harga = harga;
    }

    public int getJumlah() {
        return Jumlah;
    }

    public void setJumlah(int jumlah) {
        Jumlah = jumlah;
    }

    public int getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(int totalHarga) {
        this.totalHarga = totalHarga;
    }
}
