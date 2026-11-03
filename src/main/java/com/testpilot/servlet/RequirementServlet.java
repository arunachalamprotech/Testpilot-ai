package com.testpilot.servlet;

import com.testpilot.dao.RequirementDAO;
import com.testpilot.model.Requirement;
import com.testpilot.model.User;
import com.testpilot.util.Csrf;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet("/requirements")
public class RequirementServlet extends HttpServlet {
    private static final List<String> PRIORITIES = Arrays.asList("LOW", "MEDIUM", "HIGH");
    private final RequirementDAO dao = new RequirementDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            if ("new".equals(action)) {
                showForm(req, resp, null);
            } else if ("edit".equals(action)) {
                Integer id = parseId(req.getParameter("id"));
                Requirement r = (id == null) ? null : dao.findById(id);
                if (r == null) {
                    resp.sendRedirect(req.getContextPath() + "/requirements");
                    return;
                }
                showForm(req, resp, r);
            } else {
                req.setAttribute("requirements", dao.findAll());
                forwardList(req, resp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("requirements", new ArrayList<Requirement>());
            req.setAttribute("error", "Database error. Please try again.");
            forwardList(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if (!Csrf.valid(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing security token");
            return;
        }
        String action = req.getParameter("action");
        try {
            if ("delete".equals(action)) {
                Integer id = parseId(req.getParameter("id"));
                if (id != null) {
                    dao.delete(id);
                }
                resp.sendRedirect(req.getContextPath() + "/requirements?msg=deleted");
                return;
            }
            saveRequirement(req, resp);
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Database error. Please try again.");
            showForm(req, resp, formValues(req));
        }
    }

    private void saveRequirement(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, SQLException {
        Requirement r = formValues(req);
        String error = null;
        if (r.getTitle().isEmpty() || r.getTitle().length() > 200) {
            error = "Title is required (maximum 200 characters).";
        } else if (r.getDescription().length() > 5000) {
            error = "Description is too long (maximum 5000 characters).";
        } else if (!PRIORITIES.contains(r.getPriority())) {
            error = "Please choose a valid priority.";
        }

        if (error != null) {
            req.setAttribute("error", error);
            showForm(req, resp, r);
            return;
        }

        if (r.getId() > 0) {
            dao.update(r.getId(), r.getTitle(), r.getDescription(), r.getPriority());
        } else {
            User user = (User) req.getSession().getAttribute("user");
            dao.create(r.getTitle(), r.getDescription(), r.getPriority(), user.getId());
        }
        resp.sendRedirect(req.getContextPath() + "/requirements?msg=saved");
    }

    private Requirement formValues(HttpServletRequest req) {
        Requirement r = new Requirement();
        Integer id = parseId(req.getParameter("id"));
        r.setId(id == null ? 0 : id);
        r.setTitle(clean(req.getParameter("title")));
        r.setDescription(clean(req.getParameter("description")));
        r.setPriority(clean(req.getParameter("priority")));
        return r;
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Requirement r)
            throws ServletException, IOException {
        req.setAttribute("requirement", r);
        req.getRequestDispatcher("/WEB-INF/views/requirement_form.jsp").forward(req, resp);
    }

    private void forwardList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/requirements.jsp").forward(req, resp);
    }

    private static Integer parseId(String s) {
        try {
            return (s == null) ? null : Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String clean(String s) {
        return s == null ? "" : s.trim();
    }
}
