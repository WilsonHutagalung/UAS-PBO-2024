-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 07 Jun 2024 pada 07.02
-- Versi server: 10.4.28-MariaDB
-- Versi PHP: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `spareparts`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `akun`
--

CREATE TABLE `akun` (
  `Id` int(11) NOT NULL,
  `username` text NOT NULL,
  `password` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `akun`
--

INSERT INTO `akun` (`Id`, `username`, `password`) VALUES
(1, 'Wilson', '123'),
(2, 'Agus', '024'),
(3, 'Joy', '014'),
(4, 'Ahmad', '2019'),
(7, 'Bowo', '12345'),
(8, 'Filip123', '111222'),
(9, 'qwe', 'qwe'),
(10, 'Disanto', 'Nupa');

-- --------------------------------------------------------

--
-- Struktur dari tabel `cart`
--

CREATE TABLE `cart` (
  `Id_Cart` int(11) NOT NULL,
  `User_Id` int(11) NOT NULL,
  `No_Seri` int(11) NOT NULL,
  `Nama_sparepart` text NOT NULL,
  `Merek` text NOT NULL,
  `Kategori` text NOT NULL,
  `Harga` int(11) NOT NULL,
  `Jumlah` int(11) NOT NULL,
  `Total_Harga` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `cart`
--

INSERT INTO `cart` (`Id_Cart`, `User_Id`, `No_Seri`, `Nama_sparepart`, `Merek`, `Kategori`, `Harga`, `Jumlah`, `Total_Harga`) VALUES
(6, 10, 1, 'Ban', 'Mitsubishi', 'Genuine', 550000, 6, 3300000);

-- --------------------------------------------------------

--
-- Struktur dari tabel `tbsparepart`
--

CREATE TABLE `tbsparepart` (
  `No_Seri` int(11) NOT NULL,
  `Nama_Sparepart` text NOT NULL,
  `Merek` text NOT NULL,
  `Kategori` text NOT NULL,
  `Tanggal` date NOT NULL,
  `Stok` int(11) NOT NULL,
  `Harga` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tbsparepart`
--

INSERT INTO `tbsparepart` (`No_Seri`, `Nama_Sparepart`, `Merek`, `Kategori`, `Tanggal`, `Stok`, `Harga`) VALUES
(1, 'Ban', 'Mitsubishi', 'Genuine', '2022-06-22', 88, 550000);

-- --------------------------------------------------------

--
-- Struktur dari tabel `transaksi`
--

CREATE TABLE `transaksi` (
  `ID_Transaksi` int(11) NOT NULL,
  `No_Seri` int(11) NOT NULL,
  `Id` int(11) NOT NULL,
  `Username` text NOT NULL,
  `Nama_Sparepart` text NOT NULL,
  `Tanggal_Transaksi` date NOT NULL,
  `Jumlah` int(11) NOT NULL,
  `Harga` int(11) NOT NULL,
  `Total_Harga` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `transaksi`
--

INSERT INTO `transaksi` (`ID_Transaksi`, `No_Seri`, `Id`, `Username`, `Nama_Sparepart`, `Tanggal_Transaksi`, `Jumlah`, `Harga`, `Total_Harga`) VALUES
(1, 1, 2, 'Agus', 'Ban', '2024-06-07', 8, 550000, 4400000),
(2, 1, 10, 'Disanto', 'Ban', '2024-06-07', 2, 550000, 1100000),
(3, 1, 10, 'Disanto', 'Ban', '2024-06-07', 2, 550000, 1100000);

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `akun`
--
ALTER TABLE `akun`
  ADD PRIMARY KEY (`Id`);

--
-- Indeks untuk tabel `cart`
--
ALTER TABLE `cart`
  ADD PRIMARY KEY (`Id_Cart`);

--
-- Indeks untuk tabel `tbsparepart`
--
ALTER TABLE `tbsparepart`
  ADD PRIMARY KEY (`No_Seri`);

--
-- Indeks untuk tabel `transaksi`
--
ALTER TABLE `transaksi`
  ADD PRIMARY KEY (`ID_Transaksi`),
  ADD KEY `No_Seri` (`No_Seri`),
  ADD KEY `Id` (`Id`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `akun`
--
ALTER TABLE `akun`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT untuk tabel `cart`
--
ALTER TABLE `cart`
  MODIFY `Id_Cart` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT untuk tabel `transaksi`
--
ALTER TABLE `transaksi`
  MODIFY `ID_Transaksi` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `transaksi`
--
ALTER TABLE `transaksi`
  ADD CONSTRAINT `transaksi_ibfk_1` FOREIGN KEY (`No_Seri`) REFERENCES `tbsparepart` (`No_Seri`),
  ADD CONSTRAINT `transaksi_ibfk_2` FOREIGN KEY (`Id`) REFERENCES `akun` (`Id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
