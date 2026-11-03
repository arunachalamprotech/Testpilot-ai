package com.testpilot.servlet;

import com.testpilot.dao.RequirementDAO;
import com.testpilot.dao.TestCaseDAO;
import com.testpilot.model.Requirement;
import com.testpilot.model.TestCase;
import com.testpilot.service.AiException;
import com.testpilot.service.TestCaseGenerator;
import com.testpilot.util.Csrf;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/testcases")
public class TestCaseServlet extends HttpServlet {
    private final RequirementDAO requirementDAO = new RequirementDAO();
    private final TestCaseDAO testCaseDAO = new TestCaseDAO();

    // ---------------------------------------------------------------- GET
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer reqId = parseId(req.getParameter("requirementId"));
        try {
            Requirement requirement = (reqId == null) ? null : requirementDAO.findById(reqId);
            if (requirement == null) {
                resp.sendRedirect(req.getContextPath() + "/requirements");
                return;
            }
            req.setAttribute("requirement", requirement);
            String action = req.getParameter("action");

            if ("new".equals(action)) {
                forward(req, resp, "testcase_form.jsp");
            } else if ("edit".equals(action)) {
                Integer id = parseId(req.getParameter("id"));
                TestCase tc = (id == null) ? null : testCaseDAO.findById(id);
                if (tc == null || tc.getRequirementId() != requirement.getId()) {
                    redirectToList(req, resp, requirement.getId(), null);
                    return;
                }
                req.setAttribute("testCase", tc);
                forward(req, resp, "testcase_form.jsp");
            } else if ("review".equals(action)) {
                List<TestCase> proposed = proposedFor(req.getSession(), requirement.getId());
                if (proposed == null) {
                    redirectToList(req, resp, requirement.getId(), null);
                    return;
                }
                req.setAttribute("proposed", proposed);
                forward(req, resp, "testcase_review.jsp");
            } else {
                showList(req, resp, requirement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Database error. Please try again.");
            req.setAttribute("testCases", new ArrayList<TestCase>());
            forward(req, resp, "testcases.jsp");
        }
    }

    // --------------------------------------------------------------- POST
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if (!Csrf.valid(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing security token");
            return;
        }
        Integer reqId = parseId(req.getParameter("requirementId"));
        try {
            Requirement requirement = (reqId == null) ? null : requirementDAO.findById(reqId);
            if (requirement == null) {
                resp.sendRedirect(req.getContextPath() + "/requirements");
                return;
            }
            req.setAttribute("requirement", requirement);
            String action = req.getParameter("action");

            if ("delete".equals(action)) {
                Integer id = parseId(req.getParameter("id"));
                if (id != null) {
                    testCaseDAO.delete(id, requirement.getId());
                }
                redirectToList(req, resp, requirement.getId(), "deleted");
            } else if ("generate".equals(action)) {
                generate(req, resp, requirement);
            } else if ("saveProposed".equals(action)) {
                saveProposed(req, resp, requirement);
            } else if ("discard".equals(action)) {
                clearProposed(req.getSession());
                redirectToList(req, resp, requirement.getId(), null);
            } else {
                saveManual(req, resp, requirement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Database error. Please try again.");
            req.setAttribute("testCases", new ArrayList<TestCase>());
            forward(req, resp, "testcases.jsp");
        }
    }

    private void generate(HttpServletRequest req, HttpServletResponse resp, Requirement requirement)
            throws ServletException, IOException, SQLException {
        try {
            List<TestCase> proposed = TestCaseGenerator.generate(requirement);
            HttpSession session = req.getSession();
            session.setAttribute("proposed", proposed);
            session.setAttribute("proposedReq", requirement.getId());
            resp.sendRedirect(req.getContextPath() + "/testcases?requirementId="
                    + requirement.getId() + "&action=review");
        } catch (AiException e) {
            req.setAttribute("error", e.getMessage());
            showList(req, resp, requirement);
        }
    }

    private void saveProposed(HttpServletRequest req, HttpServletResponse resp, Requirement requirement)
            throws IOException, SQLException {
        HttpSession session = req.getSession();
        List<TestCase> proposed = proposedFor(session, requirement.getId());
        String[] picks = req.getParameterValues("pick");
        if (proposed != null && picks != null) {
            List<TestCase> chosen = new ArrayList<>();
            for (String p : picks) {
                Integer idx = parseId(p);
                if (idx != null && idx >= 0 && idx < proposed.size()) {
                    TestCase tc = proposed.get(idx);
                    tc.setRequirementId(requirement.getId());
                    chosen.add(tc);
                }
            }
            if (!chosen.isEmpty()) {
                testCaseDAO.createAll(chosen);
            }
        }
        clearProposed(session);
        redirectToList(req, resp, requirement.getId(), "aisaved");
    }

    private void saveManual(HttpServletRequest req, HttpServletResponse resp, Requirement requirement)
            throws ServletException, IOException, SQLException {
        TestCase tc = new TestCase();
        Integer id = parseId(req.getParameter("id"));
        tc.setId(id == null ? 0 : id);
        tc.setRequirementId(requirement.getId());
        tc.setTitle(clean(req.getParameter("title")));
        tc.setSteps(clean(req.getParameter("steps")));
        tc.setExpectedResult(clean(req.getParameter("expectedResult")));
        tc.setSource("MANUAL");

        String error = null;
        if (tc.getTitle().isEmpty() || tc.getTitle().length() > 200) {
            error = "Title is required (maximum 200 characters).";
        } else if (tc.getSteps().length() > 5000 || tc.getExpectedResult().length() > 5000) {
            error = "Steps and expected result must be under 5000 characters.";
        }

        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("testCase", tc);
            forward(req, resp, "testcase_form.jsp");
            return;
        }

        if (tc.getId() > 0) {
            testCaseDAO.update(tc);
        } else {
            testCaseDAO.create(tc);
        }
        redirectToList(req, resp, requirement.getId(), "saved");
    }

    // ------------------------------------------------------------ helpers
    private void showList(HttpServletRequest req, HttpServletResponse resp, Requirement requirement)
            throws ServletException, IOException, SQLException {
        req.setAttribute("testCases", testCaseDAO.findByRequirement(requirement.getId()));
        forward(req, resp, "testcases.jsp");
    }

    @SuppressWarnings("unchecked")
    private List<TestCase> proposedFor(HttpSession session, int requirementId) {
        if (session == null) {
            return null;
        }
        Object list = session.getAttribute("proposed");
        Object reqId = session.getAttribute("proposedReq");
        if (list instanceof List && reqId instanceof Integer && ((Integer) reqId) == requirementId) {
            return (List<TestCase>) list;
        }
        return null;
    }

    private void clearProposed(HttpSession session) {
        if (session != null) {
            session.removeAttribute("proposed");
            session.removeAttribute("proposedReq");
        }
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/" + view).forward(req, resp);
    }

    private void redirectToList(HttpServletRequest req, HttpServletResponse resp, int reqId, String msg)
            throws IOException {
        String url = req.getContextPath() + "/testcases?requirementId=" + reqId;
        if (msg != null) {
            url += "&msg=" + msg;
        }
        resp.sendRedirect(url);
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
