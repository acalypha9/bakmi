package com.kelompok6.bakmi.Models;

import java.util.List;

/**
 * Representasi sebuah kiosk self-ordering sederhana yang menyimpan informasi tentang
 * customer yang sedang menggunakan kiosk dan menyediakan operasi minimal untuk
 * menampilkan menu, menampilkan ringkasan pesanan, mencetak kode pesanan, dan
 * menampilkan status pesanan.
 */
public class SelfOrderingKiosk {
    private Customer currentCustomer;

    /**
     * Membuat instance {@code SelfOrderingKiosk} tanpa customer terpasang.
     */
    public SelfOrderingKiosk() {}

    /**
     * Membuat instance {@code SelfOrderingKiosk} dengan customer yang diberikan.
     *
     * @param customer customer awal yang menggunakan kiosk
     */
    public SelfOrderingKiosk(Customer customer) {
        this.currentCustomer = customer;
    }

    /**
     * Mengembalikan customer yang sedang aktif di kiosk.
     *
     * @return currentCustomer objek {@link Customer}, atau {@code null} bila belum ada
     */
    public Customer getCurrentCustomer() { return currentCustomer; }

    /**
     * Mengatur customer yang sedang aktif di kiosk.
     *
     * @param currentCustomer objek {@link Customer} untuk diset sebagai aktif
     */
    public void setCurrentCustomer(Customer currentCustomer) { this.currentCustomer = currentCustomer; }

    /**
     * Tempatkan logika tampilan menu di UI kiosk. Implementasi detail diserahkan
     * ke implementor (saat ini metode kosong).
     *
     * @param menuList daftar {@link Menu} yang akan ditampilkan
     */
    public void tampilkanMenu(List<Menu> menuList) {
    }

    /**
     * Tempatkan logika untuk menampilkan ringkasan pesanan pada kiosk.
     * Implementasi detail diserahkan ke implementor (saat ini metode kosong).
     *
     * @param p objek {@link Pesanan} yang akan ditampilkan ringkasannya
     */
    public void tampilkanRingkasanPesanan(Pesanan p) {
    }

    /**
     * Mengembalikan kode pesanan dari objek {@link Pesanan}.
     *
     * @param p objek Pesanan
     * @return kode pesanan atau {@code null} jika {@code p} bernilai {@code null}
     */
    public String cetakKodePesanan(Pesanan p) {
        if (p == null) return null;
        return p.getKodePesanan();
    }

    /**
     * Mengembalikan status pesanan dari objek {@link Pesanan}.
     *
     * @param p objek Pesanan
     * @return status pesanan atau {@code null} jika {@code p} bernilai {@code null}
     */
    public StatusPesanan tampilkanStatusPesanan(Pesanan p) {
        if (p == null) return null;
        return p.getStatusPesanan();
    }
}