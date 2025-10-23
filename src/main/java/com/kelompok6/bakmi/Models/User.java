package com.kelompok6.bakmi.Models;

/**
 * Representasi entitas pengguna dasar dalam sistem.
 * <p>
 * Kelas ini menyimpan identifier pengguna dan nama yang dapat diwariskan
 * oleh kelas-kelas spesifik seperti {@link Customer}.
 */
public class User {
    /**
     * Identifier numerik untuk pengguna.
     */
    protected int idUser;

    /**
     * Nama pengguna.
     */
    protected String nama;

    /**
     * Konstruktor default.
     */
    public User() {}

    /**
     * Konstruktor lengkap.
     *
     * @param idUser identifier pengguna
     * @param nama   nama pengguna
     */
    public User(int idUser, String nama) {
        this.idUser = idUser;
        this.nama = nama;
    }

    /**
     * Mengambil identifier pengguna.
     *
     * @return id pengguna
     */
    public int getIdUser() { return idUser; }

    /**
     * Menetapkan identifier pengguna.
     *
     * @param idUser id pengguna baru
     */
    public void setIdUser(int idUser) { this.idUser = idUser; }

    /**
     * Mengambil nama pengguna.
     *
     * @return nama pengguna
     */
    public String getNama() { return nama; }

    /**
     * Menetapkan nama pengguna.
     *
     * @param nama nama pengguna baru
     */
    public void setNama(String nama) { this.nama = nama; }
}