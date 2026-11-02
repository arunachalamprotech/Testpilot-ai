package com.testpilot.servlet;

import com.testpilot.util.DBUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/dbtest")
public class DbTestServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        try (Connection con = DBUtil.getConnection()) {
            String product = con.getMetaData().getDatabaseProductName();
            String version = con.getMetaData().getDatabaseProductVersion();
            resp.getWriter().println("SUCCESS: connected to " + product + " " + version);
            resp.getWriter().println("Database: " + con.getCatalog());
        } catch (Exception e) {
            resp.getWriter().println("FAILED: " + e.getMessage());
        }
    }
}
