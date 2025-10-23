package com.kelompok6.bakmi.Models;

import java.time.LocalDateTime;

/**
 * Representasi {@link Customer} yang menggunakan layanan TakeAway (bawa pulang).
 * <p>
 * Menyimpan informasi waktu ambil (perkiraan kapan pesanan bisa diambil oleh customer).
 * Tipe layanan pada objek ini di-set ke {@link JenisLayanan#TakeAway}.
 */
public class TakeAwayCustomer extends Customer {
    /**
     * Waktu ambil yang diinginkan oleh customer (nullable).
     */
    private LocalDateTime waktuAmbil;

    /**
     * Konstruktor default yang menginisialisasi tipe layanan ke {@link JenisLayanan#TakeAway}.
     */
    public TakeAwayCustomer() {
        this.tipeLayanan = JenisLayanan.TakeAway;
    }

    /**
     * Konstruktor lengkap.
     *
     * @param idCustomer  id customer
     * @param nama        nama customer
     * @param waktuAmbil  waktu ambil yang diinginkan
     */
    public TakeAwayCustomer(int idCustomer, String nama, LocalDateTime waktuAmbil) {
        super(idCustomer, nama, JenisLayanan.TakeAway);
        this.waktuAmbil = waktuAmbil;
    }

    /**
     * Mengambil waktu ambil yang tersimpan.
     *
     * @return waktu ambil (nullable)
     */
    public LocalDateTime getWaktuAmbil() { return waktuAmbil; }

    /**
     * Menetapkan waktu ambil.
     *
     * @param waktuAmbil waktu ambil baru
     */
    public void setWaktuAmbil(LocalDateTime waktuAmbil) { this.waktuAmbil = waktuAmbil; }

    /**
     * Alias untuk {@link #setWaktuAmbil(LocalDateTime)} — mengatur waktu ambil pesanan.
     *
     * @param waktu waktu ambil yang diinginkan
     */
    public void aturWaktuAmbil(LocalDateTime waktu) { this.waktuAmbil = waktu; }
}