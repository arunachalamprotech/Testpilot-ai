package com.testpilot.servlet;

import com.testpilot.dao.UserDAO;
import com.testpilot.model.User;
import com.testpilot.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email") == null ? "" : req.getParameter("email").trim().toLowerCase();
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");

        String error = null;
        try {
            User user = userDAO.findByEmail(email);
            if (user != null && PasswordUtil.verify(password, user.getPasswordHash())) {
                // new session after login (prevents session fixation)
                HttpSession old = req.getSession(false);
                if (old != null) {
                    old.invalidate();
                }
                HttpSession session = req.getSession(true);
                session.setAttribute("user", user);
                resp.sendRedirect(req.getContextPath() + "/dashboard");
                return;
            }
            error = "Invalid email or password.";
        } catch (SQLException e) {
            e.printStackTrace();
            error = "Database error. Please try again.";
        }

        req.setAttribute("error", error);
        req.setAttribute("email", email);
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }
}
