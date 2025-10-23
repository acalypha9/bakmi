package com.kelompok6.bakmi.Models;

/**
 * Representasi pelanggan dengan layanan makan di tempat (dine-in).
 * <p>
 * {@code DineInCustomer} memperluas {@link Customer} dan menambahkan
 * properti {@code nomorMeja} untuk menyimpan nomor meja pelanggan.
 */
public class DineInCustomer extends Customer {
    private int nomorMeja;

    /**
     * Konstruktor default yang menginisialisasi tipe layanan ke {@link JenisLayanan#DineIn}.
     */
    public DineInCustomer() {
        this.tipeLayanan = JenisLayanan.DineIn;
    }

    /**
     * Konstruktor lengkap.
     *
     * @param idCustomer ID pelanggan
     * @param nama       nama pelanggan
     * @param nomorMeja  nomor meja yang sudah dipilih (atau 0 bila belum memilih)
     */
    public DineInCustomer(int idCustomer, String nama, int nomorMeja) {
        super(idCustomer, nama, JenisLayanan.DineIn);
        this.nomorMeja = nomorMeja;
    }

    /**
     * Dapatkan nomor meja pelanggan.
     *
     * @return nomor meja (integer)
     */
    public int getNomorMeja() { return nomorMeja; }

    /**
     * Tetapkan nomor meja pelanggan.
     *
     * @param nomorMeja nomor meja yang akan diset
     */
    public void setNomorMeja(int nomorMeja) { this.nomorMeja = nomorMeja; }

    /**
     * Pilih meja untuk pelanggan — alias dari {@link #setNomorMeja(int)}.
     *
     * @param no nomor meja yang dipilih
     */
    public void pilihMeja(int no) { setNomorMeja(no); }
}