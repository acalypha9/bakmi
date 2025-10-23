package com.kelompok6.bakmi.Models;

/**
 * Representasi sebuah item menu pada aplikasi.
 * <p>
 * Objek {@code Menu} immutable (hanya baca) yang menyimpan informasi
 * seperti id, nama, deskripsi, kategori, filter, harga, dan path gambar.
 */
public class Menu {
    private final int id;
    private final String name;
    private final String description;
    private final String category;
    private final String filter;
    private final long price;
    private final String imagePath;

    /**
     * Membuat instance {@code Menu}.
     *
     * @param id         identifier unik menu
     * @param name       nama menu
     * @param description deskripsi menu (boleh {@code null})
     * @param category   kategori menu (mis. "Makanan", "Minuman")
     * @param filter     tag/filter tambahan untuk pengelompokan
     * @param price      harga menu dalam satuan terkecil (mis. rupiah)
     * @param imagePath  path atau resource image untuk menu (boleh {@code null})
     */
    public Menu(int id, String name, String description, String category, String filter, long price, String imagePath) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.filter = filter;
        this.price = price;
        this.imagePath = imagePath;
    }

    /**
     * Mengembalikan id menu.
     *
     * @return id menu
     */
    public int getId() { return id; }

    /**
     * Mengembalikan nama menu.
     *
     * @return nama menu
     */
    public String getName() { return name; }

    /**
     * Mengembalikan deskripsi menu.
     *
     * @return deskripsi menu (mungkin {@code null})
     */
    public String getDescription() { return description; }

    /**
     * Mengembalikan kategori menu.
     *
     * @return kategori menu (mis. "Makanan", "Minuman")
     */
    public String getCategory() { return category; }

    /**
     * Mengembalikan nilai filter/tag menu.
     *
     * @return filter/tag untuk pengelompokan (mungkin {@code null})
     */
    public String getFilter() { return filter; }

    /**
     * Mengembalikan harga menu.
     *
     * @return harga menu (satuan terkecil, mis. rupiah)
     */
    public long getPrice() { return price; }

    /**
     * Mengembalikan path resource gambar untuk menu.
     *
     * @return path gambar (mungkin {@code null})
     */
    public String getImagePath() { return imagePath; }

    /**
     * Menyediakan representasi singkat menu yang mudah dibaca,
     * berisi nama, deskripsi singkat dan harga.
     *
     * @return string ringkasan menu
     */
    public String tampilkanMenu() {
        return String.format("%s - %s - %s", name, description == null ? "" : description, price);
    }
}