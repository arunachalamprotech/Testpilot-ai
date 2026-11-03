package com.testpilot.servlet;

import com.testpilot.dao.RequirementDAO;
import com.testpilot.dao.TestCaseDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private final RequirementDAO requirementDAO = new RequirementDAO();
    private final TestCaseDAO testCaseDAO = new TestCaseDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("reqCount", requirementDAO.count());
            req.setAttribute("tcCount", testCaseDAO.countAll());
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("reqCount", "-");
            req.setAttribute("tcCount", "-");
        }
        req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
    }
}
