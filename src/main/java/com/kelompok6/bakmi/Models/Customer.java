package com.kelompok6.bakmi.Models;

import java.util.Optional;

/**

 * Representasi pelanggan yang menggunakan aplikasi.
 * <p>
 * {@code Customer} memperluas {@link User} dan menyimpan informasi
 * tambahan seperti {@code idCustomer} dan {@code tipeLayanan}.
 * Kelas ini menyediakan utilitas untuk memilih tipe layanan,
 * membuat pesanan sederhana dari menu, serta helper untuk membaca/menetapkan
 * nama pelanggan menggunakan refleksi untuk kompatibilitas dengan
 * berbagai implementasi {@code User}.
 */
public class Customer extends User {
    protected int idCustomer;
    protected JenisLayanan tipeLayanan;

    /**

     * Konstruktor default.
     */
    public Customer() {}

    /**

     * Konstruktor lengkap.
     *
     * @param idCustomer   ID pelanggan
     * @param nama         nama pelanggan
     * @param tipeLayanan  jenis layanan yang dipilih pelanggan
     */
    public Customer(int idCustomer, String nama, JenisLayanan tipeLayanan) {
        super(idCustomer, nama);
        this.idCustomer = idCustomer;
        this.tipeLayanan = tipeLayanan;
    }

    /**

     * Dapatkan ID pelanggan.
     *
     * @return idCustomer
     */
    public int getIdCustomer() { return idCustomer; }

    /**

     * Tetapkan ID pelanggan.
     *
     * @param idCustomer idCustomer baru
     */
    public void setIdCustomer(int idCustomer) { this.idCustomer = idCustomer; }

    /**

     * Dapatkan tipe layanan pelanggan (DineIn / TakeAway, dsb).
     *
     * @return tipe layanan saat ini
     */
    public JenisLayanan getTipeLayanan() { return tipeLayanan; }

    /**

     * Tetapkan tipe layanan pelanggan.
     *
     * @param tipeLayanan tipe layanan yang akan diatur
     */
    public void setTipeLayanan(JenisLayanan tipeLayanan) { this.tipeLayanan = tipeLayanan; }

    /**

     * Pilih jenis layanan untuk pelanggan ini.
     *
     * @param jenis jenis layanan yang dipilih
     */
    public void pilihTipeLayanan(JenisLayanan jenis) {
        this.tipeLayanan = jenis;
    }

    /**

     * Buat {@link Pesanan} sederhana dengan satu item dari menu dan jumlah.
     * Pesanan akan berisi item tersebut dan total akan dihitung.
     *
     * @param m   menu yang dipilih
     * @param qty jumlah item
     * @return objek Pesanan pertama yang mengandung item dan total
     */
    public Pesanan pilihMenu(Menu m, int qty) {
        Pesanan p = new Pesanan();
        p.tambahItem(m, qty);
        p.hitungTotal();
        return p;
    }

    /**

     * Tinjau pesanan — placeholder (tidak melakukan apa-apa secara default).
     *
     * @param p pesanan yang akan ditinjau
     */
    public void tinjauPesanan(Pesanan p) {
    }

    /**

     * Konfirmasi pesanan dengan memanggil logika internal {@link Pesanan#konfirmasiPesanan()}.
     *
     * @param p pesanan yang dikonfirmasi
     */
    public void konfirmasiPesanan(Pesanan p) {
        p.konfirmasiPesanan();
    }

    /**

     * Bayar pesanan dengan memanggil {@link Pesanan#bayar()}.
     *
     * @param p pesanan yang akan dibayar
     */
    public void bayar(Pesanan p) {
        p.bayar();
    }

    /**

     * Lihat status pesanan dengan aman (mengembalikan null jika pesanan null).
     *
     * @param p pesanan yang ingin diperiksa
     * @return status pesanan atau {@code null} jika tidak ada pesanan
     */
    public StatusPesanan lihatStatusPesanan(Pesanan p) {
        return Optional.ofNullable(p).map(Pesanan::getStatusPesanan).orElse(null);
    }

    /**

     * Ambil nama pelanggan menggunakan refleksi.
     * <p>
     * Metode ini mencoba beberapa strategi (metode {@code getNama}, {@code getName},
     * field {@code nama}, field {@code name}) untuk kompatibilitas dengan varian
     * kelas {@code User} yang mungkin berbeda.
     *
     * @return nama pelanggan (string kosong jika tidak ditemukan)
     */
    public String getNamaCustomer() {
        try {
            java.lang.reflect.Method m = this.getClass().getMethod("getNama");
            Object o = m.invoke(this);
            return o == null ? "" : o.toString();
        } catch (Exception ignored) {}

        try {
            java.lang.reflect.Method m = this.getClass().getMethod("getName");
            Object o = m.invoke(this);
            return o == null ? "" : o.toString();
        } catch (Exception ignored) {}

        try {
            java.lang.reflect.Field f = this.getClass().getDeclaredField("nama");
            f.setAccessible(true);
            Object o = f.get(this);
            return o == null ? "" : o.toString();
        } catch (Exception ignored) {}

        try {
            java.lang.reflect.Field f = this.getClass().getDeclaredField("name");
            f.setAccessible(true);
            Object o = f.get(this);
            return o == null ? "" : o.toString();
        } catch (Exception ignored) {}

        return "";
    }

    /**

     * Tetapkan nama pelanggan menggunakan refleksi.
     * <p>
     * Metode ini mencoba beberapa strategi (metode {@code setNama}, {@code setName},
     * field {@code nama}, field {@code name}) untuk kompatibilitas dengan varian
     * kelas {@code User} yang mungkin berbeda. Jika {@code nama} bernilai {@code null},
     * operasi diabaikan.
     *
     * @param nama nama pelanggan yang akan diset
     */
    public void setNamaCustomer(String nama) {
        if (nama == null) return;
        try {
            java.lang.reflect.Method m = this.getClass().getMethod("setNama", String.class);
            m.invoke(this, nama);
            return;
        } catch (Exception ignored) {}

        try {
            java.lang.reflect.Method m = this.getClass().getMethod("setName", String.class);
            m.invoke(this, nama);
            return;
        } catch (Exception ignored) {}

        try {
            java.lang.reflect.Field f = this.getClass().getDeclaredField("nama");
            f.setAccessible(true);
            f.set(this, nama);
            return;
        } catch (Exception ignored) {}

        try {
            java.lang.reflect.Field f = this.getClass().getDeclaredField("name");
            f.setAccessible(true);
            f.set(this, nama);
        } catch (Exception ignored) {}
    }
}