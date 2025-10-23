package com.kelompok6.bakmi.Database;

import com.kelompok6.bakmi.Models.Menu;
import com.kelompok6.bakmi.Models.Pesanan;

import java.sql.*;
import java.time.LocalDateTime;

public class PesananDAO {

    public int save(Pesanan pesanan, Integer customerId, String layanan, Integer nomorMeja, String waktuAmbil) throws SQLException {
        String insertOrder = "INSERT INTO pesanan (kode, total, catatan, status, waktu, customer_id, layanan, nomor_meja, waktu_ambil) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String insertItem  = "INSERT INTO pesanan_item (pesanan_id, menu_id, nama, harga, qty, subtotal) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection c = DBUtil.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement psOrder = c.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setString(1, pesanan.getKodePesanan());
                psOrder.setLong(2, pesanan.getTotalHarga());
                psOrder.setString(3, pesanan.getCatatan());
                psOrder.setString(4, pesanan.getStatusPesanan() == null ? "" : pesanan.getStatusPesanan().name());
                psOrder.setString(5, pesanan.getWaktuPemesan() == null ? LocalDateTime.now().toString() : pesanan.getWaktuPemesan().toString());
                if (customerId != null) psOrder.setInt(6, customerId); else psOrder.setNull(6, Types.INTEGER);
                if (layanan != null) psOrder.setString(7, layanan); else psOrder.setNull(7, Types.VARCHAR);
                if (nomorMeja != null) psOrder.setInt(8, nomorMeja); else psOrder.setNull(8, Types.INTEGER);
                if (waktuAmbil != null) psOrder.setString(9, waktuAmbil); else psOrder.setNull(9, Types.VARCHAR);

                psOrder.executeUpdate();
                try (ResultSet keys = psOrder.getGeneratedKeys()) {
                    if (keys.next()) {
                        int orderId = keys.getInt(1);
                        try (PreparedStatement psItem = c.prepareStatement(insertItem)) {
                            for (Pesanan.Item it : pesanan.getItems()) {
                                Menu m = it.getMenu();
                                psItem.setInt(1, orderId);
                                psItem.setInt(2, m.getId());
                                psItem.setString(3, m.getName());
                                psItem.setLong(4, m.getPrice());
                                psItem.setInt(5, it.getQty());
                                psItem.setLong(6, it.getSubtotal());
                                psItem.addBatch();
                            }
                            psItem.executeBatch();
                        }
                        c.commit();
                        return orderId;
                    } else {
                        c.rollback();
                        throw new SQLException("Failed to retrieve generated order id");
                    }
                }
            } catch (Exception ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    public Pesanan findById(int id) throws SQLException {
        String qOrder = "SELECT id, kode, total, catatan, status, waktu, customer_id, layanan, nomor_meja, waktu_ambil FROM pesanan WHERE id = ?";
        String qItems = "SELECT menu_id, nama, harga, qty FROM pesanan_item WHERE pesanan_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement po = c.prepareStatement(qOrder);
             PreparedStatement pi = c.prepareStatement(qItems)) {
            po.setInt(1, id);
            try (ResultSet ro = po.executeQuery()) {
                if (!ro.next()) return null;
                Pesanan p = new Pesanan();
                p.setId(ro.getInt("id"));
                p.setKodePesanan(ro.getString("kode"));
                p.setCatatan(ro.getString("catatan"));
                String status = ro.getString("status");
                if (status != null && !status.isBlank()) {
                    try { p.setStatusPesanan(com.kelompok6.bakmi.Models.StatusPesanan.valueOf(status)); } catch (Exception ignored) {}
                }
                try { p.setWaktuPemesan(LocalDateTime.parse(ro.getString("waktu"))); } catch (Exception ignored) {}

                pi.setInt(1, id);
                try (ResultSet ri = pi.executeQuery()) {
                    while (ri.next()) {
                        Menu m = new Menu(
                                ri.getInt("menu_id"),
                                ri.getString("nama"),
                                "",
                                "",
                                null,
                                ri.getLong("harga"),
                                null
                        );
                        p.tambahItem(m, ri.getInt("qty"));
                    }
                }
                return p;
            }
        }
    }
}