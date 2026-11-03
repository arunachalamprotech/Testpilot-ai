package com.testpilot.dao;

import com.testpilot.model.TestCase;
import com.testpilot.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestCaseDAO {

    private static final String COLUMNS =
            "id, requirement_id, title, steps, expected_result, source, created_at";

    public List<TestCase> findByRequirement(int requirementId) throws SQLException {
        List<TestCase> list = new ArrayList<>();
        String sql = "SELECT " + COLUMNS + " FROM test_cases WHERE requirement_id = ? ORDER BY id";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, requirementId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public TestCase findById(int id) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM test_cases WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public void create(TestCase tc) throws SQLException {
        List<TestCase> one = new ArrayList<>();
        one.add(tc);
        createAll(one);
    }

    public void createAll(List<TestCase> list) throws SQLException {
        String sql = "INSERT INTO test_cases (requirement_id, title, steps, expected_result, source) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (TestCase tc : list) {
                ps.setInt(1, tc.getRequirementId());
                ps.setString(2, tc.getTitle());
                ps.setString(3, tc.getSteps());
                ps.setString(4, tc.getExpectedResult());
                ps.setString(5, tc.getSource());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public boolean update(TestCase tc) throws SQLException {
        String sql = "UPDATE test_cases SET title = ?, steps = ?, expected_result = ? "
                   + "WHERE id = ? AND requirement_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tc.getTitle());
            ps.setString(2, tc.getSteps());
            ps.setString(3, tc.getExpectedResult());
            ps.setInt(4, tc.getId());
            ps.setInt(5, tc.getRequirementId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id, int requirementId) throws SQLException {
        String sql = "DELETE FROM test_cases WHERE id = ? AND requirement_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, requirementId);
            return ps.executeUpdate() > 0;
        }
    }

    public int countAll() throws SQLException {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM test_cases");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private TestCase map(ResultSet rs) throws SQLException {
        TestCase tc = new TestCase();
        tc.setId(rs.getInt("id"));
        tc.setRequirementId(rs.getInt("requirement_id"));
        tc.setTitle(rs.getString("title"));
        tc.setSteps(rs.getString("steps"));
        tc.setExpectedResult(rs.getString("expected_result"));
        tc.setSource(rs.getString("source"));
        tc.setCreatedAt(rs.getTimestamp("created_at"));
        return tc;
    }
}
