package com.kelompok6.bakmi.Database;

import com.kelompok6.bakmi.Models.Menu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDAO {

    public List<Menu> getAll() throws SQLException {
        List<Menu> list = new ArrayList<>();
        String sql = "SELECT id, name, description, category, filter, price, image_path FROM menu ORDER BY id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {
            while (rs.next()) {
                Menu m = new Menu(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getString("filter"),
                        rs.getLong("price"),
                        rs.getString("image_path")
                );
                list.add(m);
            }
        }
        return list;
    }

    public Menu findById(int id) throws SQLException {
        String sql = "SELECT id, name, description, category, filter, price, image_path FROM menu WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return new Menu(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getString("category"),
                            rs.getString("filter"),
                            rs.getLong("price"),
                            rs.getString("image_path")
                    );
                }
            }
        }
        return null;
    }

    public int insert(Menu m) throws SQLException {
        String sql = "INSERT INTO menu(name, description, category, filter, price, image_path) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, m.getName());
            p.setString(2, m.getDescription());
            p.setString(3, m.getCategory());
            p.setString(4, m.getFilter());
            p.setLong(5, m.getPrice());
            p.setString(6, m.getImagePath());
            int affected = p.executeUpdate();
            if (affected == 0) throw new SQLException("Insert failed, no rows.");
            try (ResultSet gen = p.getGeneratedKeys()) {
                if (gen.next()) return gen.getInt(1);
            }
            return -1;
        }
    }
}