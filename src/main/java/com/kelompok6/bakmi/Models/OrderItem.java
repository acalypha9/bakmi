package com.kelompok6.bakmi.Models;

import java.math.BigDecimal;

/**
 * Representasi sebuah item dalam pesanan.
 * <p>
 * {@code OrderItem} mengikat sebuah {@link Menu} dengan jumlah (quantity).
 * Objek ini bersifat sebagian immutable untuk field {@code menu} (tidak dapat diubah setelah dibuat),
 * namun jumlah dapat diubah melalui {@link #setQuantity(int)}.
 */
public class OrderItem {
    private final Menu menu;
    private int quantity;

    /**
     * Membuat {@code OrderItem} baru dengan menu dan jumlah tertentu.
     * Jika nilai {@code quantity} kurang dari 1, maka akan diset menjadi 1.
     *
     * @param menu     objek {@link Menu} yang dipesan (tidak boleh {@code null} idealnya)
     * @param quantity jumlah unit menu yang dipesan (jika &lt; 1 maka akan diset 1)
     */
    public OrderItem(Menu menu, int quantity) {
        this.menu = menu;
        this.quantity = Math.max(1, quantity);
    }

    /**
     * Mengembalikan referensi {@link Menu} untuk item ini.
     *
     * @return objek {@link Menu}
     */
    public Menu getMenu() { return menu; }

    /**
     * Mengembalikan jumlah unit yang dipesan untuk menu ini.
     *
     * @return jumlah unit (selalu >= 1)
     */
    public int getQuantity() { return quantity; }

    /**
     * Mengubah jumlah unit yang dipesan.
     * Jika nilai {@code quantity} kurang dari 1, maka akan diset menjadi 1.
     *
     * @param quantity jumlah unit baru (jika &lt; 1 maka diset 1)
     */
    public void setQuantity(int quantity) { this.quantity = Math.max(1, quantity); }
}