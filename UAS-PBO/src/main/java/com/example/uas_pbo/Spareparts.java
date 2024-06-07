package com.example.uas_pbo;

public class Spareparts {
    private int noSeri;
    private String namaSparepart;
    private String merek;
    private String kategori;
    private String tanggal;
    private int stok;
    private int harga;

    public Spareparts(int noSeri, String namaSparepart, String merek,String kategori, String tanggal, int stok, int harga) {
        this.noSeri = noSeri;
        this.namaSparepart = namaSparepart;
        this.merek = merek;
        this.kategori = kategori;
        this.tanggal = tanggal;
        this.stok = stok;
        this.harga = harga;
    }

    public int getNoSeri() {
        return noSeri;
    }

    public void setNoSeri(int noSeri) {
        this.noSeri = noSeri;
    }

    public String getNamaSparepart() {
        return namaSparepart;
    }

    public void setNamaSparepart(String namaSparepart) {
        this.namaSparepart = namaSparepart;
    }

    public String getMerek() {
        return merek;
    }

    public void setMerek(String merek) {
        this.merek = merek;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

}
