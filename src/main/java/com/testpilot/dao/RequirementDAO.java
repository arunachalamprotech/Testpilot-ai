package com.testpilot.dao;

import com.testpilot.model.Requirement;
import com.testpilot.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RequirementDAO {

    private static final String SELECT_BASE =
            "SELECT r.id, r.title, r.description, r.priority, r.created_at, u.name AS created_by_name "
          + "FROM requirements r LEFT JOIN users u ON r.created_by = u.id ";

    public List<Requirement> findAll() throws SQLException {
        List<Requirement> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "ORDER BY r.id DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Requirement findById(int id) throws SQLException {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "WHERE r.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public void create(String title, String description, String priority, int userId) throws SQLException {
        String sql = "INSERT INTO requirements (title, description, priority, created_by) VALUES (?, ?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, priority);
            ps.setInt(4, userId);
            ps.executeUpdate();
        }
    }

    public boolean update(int id, String title, String description, String priority) throws SQLException {
        String sql = "UPDATE requirements SET title = ?, description = ?, priority = ? WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, priority);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM requirements WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int count() throws SQLException {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM requirements");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Requirement map(ResultSet rs) throws SQLException {
        Requirement r = new Requirement();
        r.setId(rs.getInt("id"));
        r.setTitle(rs.getString("title"));
        r.setDescription(rs.getString("description"));
        r.setPriority(rs.getString("priority"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        r.setCreatedByName(rs.getString("created_by_name"));
        return r;
    }
}
