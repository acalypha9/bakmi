package com.kelompok6.bakmi.Models;

/**
 * Daftar status yang mungkin dimiliki oleh sebuah {@link Pesanan}.
 * <p>
 * Nilai-nilai ini digunakan untuk melacak alur hidup pesanan mulai dari pembuatan
 * hingga selesai atau dibatalkan.
 */
public enum StatusPesanan {
    /**
     * Status awal ketika pesanan dibuat namun belum dikonfirmasi oleh customer.
     */
    Draft,

    /**
     * Status setelah customer mengonfirmasi pesanan tetapi sebelum pembayaran
     * (apabila alur sistem memisahkan konfirmasi dan pembayaran).
     */
    Terkonfirmasi,

    /**
     * Status menunggu pembayaran (dipakai bila ada proses pembayaran terpisah).
     */
    MenungguPembayaran,

    /**
     * Status setelah pesanan telah dibayar.
     */
    Dibayar,

    /**
     * Status ketika pesanan sedang diproses di dapur atau area persiapan.
     */
    Diproses,

    /**
     * Status ketika pesanan sudah selesai diproses dan siap untuk diambil oleh customer.
     */
    SiapAmbil,

    /**
     * Status ketika pesanan telah diserahkan/selesai untuk seluruh siklus.
     */
    Selesai,

    /**
     * Status ketika pesanan dibatalkan (oleh customer atau admin).
     */
    Dibatalkan
}