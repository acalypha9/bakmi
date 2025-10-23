package com.kelompok6.bakmi.Database;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.sql.*;

public final class DBUtil {
    private static final String DB_PATH = "data/bakmi.db";
    private static final String JDBC_PREFIX = "jdbc:sqlite:";

    private DBUtil() {}

    public static Connection getConnection() throws SQLException {
        String absolute = Paths.get(DB_PATH).toAbsolutePath().toString();
        return DriverManager.getConnection(JDBC_PREFIX + absolute);
    }

    public static void initDatabaseIfNeeded() throws Exception {
        Path dbFile = Paths.get(DB_PATH);
        if (dbFile.getParent() != null) Files.createDirectories(dbFile.getParent());

        try (Connection conn = getConnection()) {
            try (Statement st = conn.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON;");
            }

            try (Statement st = conn.createStatement()) {
                st.execute("""
                    CREATE TABLE IF NOT EXISTS menu (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        description TEXT,
                        category TEXT,
                        filter TEXT,
                        price INTEGER NOT NULL,
                        image_path TEXT
                    );
                    """);

                st.execute("""
                    CREATE TABLE IF NOT EXISTS pesanan (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        kode TEXT NOT NULL,
                        total INTEGER NOT NULL,
                        catatan TEXT,
                        status TEXT,
                        waktu TEXT,
                        customer_id INTEGER,
                        layanan TEXT,
                        nomor_meja INTEGER,
                        waktu_ambil TEXT
                    );
                    """);

                st.execute("""
                    CREATE TABLE IF NOT EXISTS pesanan_item (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        pesanan_id INTEGER NOT NULL,
                        menu_id INTEGER NOT NULL,
                        nama TEXT,
                        harga INTEGER,
                        qty INTEGER,
                        subtotal INTEGER,
                        FOREIGN KEY(pesanan_id) REFERENCES pesanan(id) ON DELETE CASCADE,
                        FOREIGN KEY(menu_id) REFERENCES menu(id)
                    );
                    """);
            }

            try (Statement st = conn.createStatement()) {
                st.execute("CREATE UNIQUE INDEX IF NOT EXISTS ux_menu_name ON menu(name);");
            }

            boolean empty = true;
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM menu;")) {
                if (rs.next()) empty = rs.getInt(1) == 0;
            }

            if (empty) {
                InputStream in = DBUtil.class.getResourceAsStream("/db/init.sql");
                if (in != null) {
                    try (BufferedReader r = new BufferedReader(new InputStreamReader(in))) {
                        StringBuilder sb = new StringBuilder();
                        String line;
                        try (Statement st = conn.createStatement()) {
                            while ((line = r.readLine()) != null) {
                                line = line.trim();
                                if (line.isEmpty() || line.startsWith("--")) continue;
                                sb.append(line).append(' ');
                                if (line.endsWith(";")) {
                                    String stmt = sb.toString();
                                    try {
                                        st.execute(stmt);
                                    } catch (SQLException ex) {
                                        System.err.println("Failed to execute statement from init.sql: " + ex.getMessage());
                                    }
                                    sb.setLength(0);
                                }
                            }
                        }
                    }
                } else {
                    try (PreparedStatement p = conn.prepareStatement(
                            "INSERT OR IGNORE INTO menu(name, description, category, filter, price, image_path) VALUES (?, ?, ?, ?, ?, ?)")) {
                        seedMenu(p, "Paket 1", "Paket hemat berisi Bakmi Ayam, Pangsit Goreng, dan Es Teh Manis.", "Promo", "Paket", 35000, "/Images/paket1.png");
                        seedMenu(p, "Paket 2", "Paket lengkap dengan Bakmi Chili Oil, Dimsum, dan Es Jeruk.", "Promo", "Paket", 42000, "/Images/paket2.png");
                        seedMenu(p, "Paket 3", "Paket lengkap dengan Bakmi Pedas Mercon, Tahu Bakso, dan Es Lemon Soda.", "Promo", "Paket", 48000, "/Images/paket3.png");
                        seedMenu(p, "Nasi Goreng Spesial", "Nasi goreng spesial dengan topping ayam & sosis.", "Makanan", "Nasi", 25000, "/Images/nasi_goreng_spesial.png");
                        seedMenu(p, "Bakmi Ayam", "Bakmi lengkap dengan topping ayam.", "Makanan", "Bakmi Original", 18000, "/Images/bakmi_ayam.png");
                        seedMenu(p, "Bakmi Pedas Mercon", "Bakmi pedas khas.", "Makanan", "Bakmi Pedas", 23000, "/Images/bakmi_pedas.png");
                        seedMenu(p, "Bihun Kuah Udang", "Bihun kuah lengkap dengan udang.", "Makanan", "Lainnya", 28000, "/Images/bihun_kuah_udang.png");
                        seedMenu(p, "Pangsit Goreng", "Pangsit goreng renyah isi ayam.", "Camilan", "Pangsit", 15000, "/Images/pangsit_goreng.png");
                        seedMenu(p, "DimSum Ayam", "Dimsum ayam lembut.", "Camilan", "Dimsum", 18000, "/Images/dimsum_ayam.png");
                        seedMenu(p, "Es Teh Manis", "Es teh manis dingin.", "Minuman", "Dingin", 8000, "/Images/es_teh.png");
                        seedMenu(p, "Jus Naga", "Jus buah naga segar.", "Minuman", "Jus", 15000, "/Images/dragon_juice.png");
                        p.executeBatch();
                    }
                }
                System.out.println("Database initialized (seed executed).");
            } else {
                System.out.println("Database already contains menu data — skipping seed.");
            }
        }
    }

    private static void seedMenu(PreparedStatement p, String name, String desc, String cat, String filter, long price, String img) throws SQLException {
        p.setString(1, name);
        p.setString(2, desc);
        p.setString(3, cat);
        p.setString(4, filter);
        p.setLong(5, price);
        p.setString(6, img);
        p.addBatch();
    }
}