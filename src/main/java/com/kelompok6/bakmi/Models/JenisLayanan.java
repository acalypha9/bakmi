package com.kelompok6.bakmi.Models;

/**
 * Tipe layanan yang dipilih oleh pelanggan.
 * <p>
 * {@code JenisLayanan} digunakan untuk membedakan apakah pelanggan
 * melakukan pemesanan untuk makan di tempat (DineIn) atau dibawa pulang (TakeAway).
 */
public enum JenisLayanan {
    /**
     * Pelanggan makan di tempat (dine-in).
     */
    DineIn,

    /**
     * Pelanggan membawa pulang pesanan (take-away).
     */
    TakeAway
}