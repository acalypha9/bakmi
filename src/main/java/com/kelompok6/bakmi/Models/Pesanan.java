package com.kelompok6.bakmi.Models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representasi sebuah pesanan yang berisi daftar item, status, waktu pemesanan, dan total harga.
 * Objek {@code Pesanan} digunakan untuk membangun, menghitung total, dan mengubah status pesanan.
 */
public class Pesanan {
    private int id;
    private String kodePesanan;
    private long totalHarga;
    private String catatan;
    private StatusPesanan statusPesanan;
    private LocalDateTime waktuPemesan;
    private final List<Item> items = new ArrayList<>();

    /**
     * Membuat instance {@code Pesanan} baru dengan status awal {@link StatusPesanan#Draft},
     * waktu pemesanan saat ini, dan kode pesanan yang dibentuk dari timestamp.
     */
    public Pesanan() {
        this.statusPesanan = StatusPesanan.Draft;
        this.waktuPemesan = LocalDateTime.now();
        this.kodePesanan = "ORD-" + System.currentTimeMillis();
    }

    /**
     * Mengembalikan id internal pesanan.
     *
     * @return id pesanan
     */
    public int getId() { return id; }

    /**
     * Mengatur id pesanan.
     *
     * @param id id baru untuk pesanan
     */
    public void setId(int id) { this.id = id; }

    /**
     * Mengembalikan kode unik pesanan.
     *
     * @return kode pesanan
     */
    public String getKodePesanan() { return kodePesanan; }

    /**
     * Mengatur kode pesanan.
     *
     * @param kodePesanan kode pesanan baru
     */
    public void setKodePesanan(String kodePesanan) { this.kodePesanan = kodePesanan; }

    /**
     * Mengembalikan total harga pesanan (dalam satuan terkecil, sesuai implementasi harga Menu).
     *
     * @return total harga
     */
    public long getTotalHarga() { return totalHarga; }

    /**
     * Mengatur total harga pesanan.
     *
     * @param totalHarga nilai total harga baru
     */
    public void setTotalHarga(long totalHarga) { this.totalHarga = totalHarga; }

    /**
     * Mengembalikan catatan tambahan untuk pesanan.
     *
     * @return catatan pesanan
     */
    public String getCatatan() { return catatan; }

    /**
     * Mengatur catatan pesanan.
     *
     * @param catatan teks catatan
     */
    public void setCatatan(String catatan) { this.catatan = catatan; }

    /**
     * Mengembalikan status saat ini dari pesanan.
     *
     * @return status pesanan
     */
    public StatusPesanan getStatusPesanan() { return statusPesanan; }

    /**
     * Mengatur status pesanan.
     *
     * @param statusPesanan status baru
     */
    public void setStatusPesanan(StatusPesanan statusPesanan) { this.statusPesanan = statusPesanan; }

    /**
     * Mengembalikan waktu ketika pesanan dibuat.
     *
     * @return waktu pemesanan
     */
    public LocalDateTime getWaktuPemesan() { return waktuPemesan; }

    /**
     * Mengatur waktu pemesanan.
     *
     * @param waktuPemesan waktu baru untuk pesanan
     */
    public void setWaktuPemesan(LocalDateTime waktuPemesan) { this.waktuPemesan = waktuPemesan; }

    /**
     * Mengembalikan daftar item dalam pesanan.
     *
     * @return list item pesanan
     */
    public List<Item> getItems() { return items; }

    /**
     * Representasi sebuah item dalam {@link Pesanan}.
     * Item berisi referensi ke {@link com.kelompok6.bakmi.Models.Menu} dan kuantitasnya.
     */
    public static class Item {
        private final com.kelompok6.bakmi.Models.Menu menu;
        private int qty;

        /**
         * Membuat Item baru untuk menu tertentu dengan kuantitas.
         *
         * @param menu objek Menu yang dipesan
         * @param qty  jumlah yang dipesan
         */
        public Item(com.kelompok6.bakmi.Models.Menu menu, int qty) { this.menu = menu; this.qty = qty; }

        /**
         * Mengembalikan objek Menu untuk item ini.
         *
         * @return menu
         */
        public com.kelompok6.bakmi.Models.Menu getMenu() { return menu; }

        /**
         * Mengembalikan kuantitas item.
         *
         * @return kuantitas
         */
        public int getQty() { return qty; }

        /**
         * Mengatur kuantitas item.
         *
         * @param qty kuantitas baru
         */
        public void setQty(int qty) { this.qty = qty; }

        /**
         * Menghitung subtotal untuk item ini (harga menu dikali kuantitas).
         *
         * @return subtotal item
         */
        public long getSubtotal() { return menu.getPrice() * (long) qty; }
    }

    /**
     * Menambahkan sebuah menu ke daftar item pesanan. Jika menu sudah ada pada pesanan,
     * maka kuantitas akan ditambahkan.
     *
     * @param m   objek Menu yang ingin ditambahkan
     * @param qty kuantitas yang ingin ditambahkan
     */
    public void tambahItem(com.kelompok6.bakmi.Models.Menu m, int qty) {
        for (Item it : items) {
            if (it.getMenu().getId() == m.getId()) {
                it.setQty(it.getQty() + qty);
                return;
            }
        }
        items.add(new Item(m, qty));
    }

    /**
     * Menghapus sebuah menu dari daftar item pesanan (berdasarkan id Menu).
     *
     * @param m objek Menu yang ingin dihapus
     */
    public void hapusItem(com.kelompok6.bakmi.Models.Menu m) {
        items.removeIf(it -> it.getMenu().getId() == m.getId());
    }

    /**
     * Menghitung total harga keseluruhan pesanan dari semua item dan menyimpannya
     * ke field {@code totalHarga}.
     *
     * @return nilai total yang dihitung
     */
    public long hitungTotal() {
        long total = 0;
        for (Item it : items) total += it.getSubtotal();
        this.totalHarga = total;
        return total;
    }

    /**
     * Menandai pesanan sebagai terkonfirmasi apabila status saat ini adalah {@link StatusPesanan#Draft}.
     */
    public void konfirmasiPesanan() {
        if (statusPesanan == StatusPesanan.Draft) statusPesanan = StatusPesanan.Terkonfirmasi;
    }

    /**
     * Menandai pesanan sebagai dibayar apabila status saat ini adalah {@link StatusPesanan#Terkonfirmasi}
     * atau {@link StatusPesanan#MenungguPembayaran}.
     */
    public void bayar() {
        if (statusPesanan == StatusPesanan.Terkonfirmasi || statusPesanan == StatusPesanan.MenungguPembayaran) {
            statusPesanan = StatusPesanan.Dibayar;
        }
    }
}