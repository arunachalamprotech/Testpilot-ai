package com.testpilot.servlet;

import com.testpilot.dao.UserDAO;
import com.testpilot.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String name = clean(req.getParameter("name"));
        String email = clean(req.getParameter("email")).toLowerCase();
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");
        String confirm = req.getParameter("confirm") == null ? "" : req.getParameter("confirm");

        String error = null;
        if (name.isEmpty() || name.length() > 100) {
            error = "Please enter your name (maximum 100 characters).";
        } else if (email.length() > 150 || !EMAIL.matcher(email).matches()) {
            error = "Please enter a valid email address.";
        } else if (password.length() < 8) {
            error = "Password must be at least 8 characters.";
        } else if (!password.equals(confirm)) {
            error = "Passwords do not match.";
        }

        try {
            if (error == null && userDAO.findByEmail(email) != null) {
                error = "This email is already registered.";
            }
            if (error == null) {
                userDAO.create(name, email, PasswordUtil.hash(password));
                resp.sendRedirect(req.getContextPath() + "/login?registered=1");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            error = "Database error. Please try again.";
        }

        req.setAttribute("error", error);
        req.setAttribute("name", name);
        req.setAttribute("email", email);
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    private static String clean(String s) {
        return s == null ? "" : s.trim();
    }
}
