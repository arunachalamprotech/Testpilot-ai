package com.testpilot.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** Simple CSRF protection: every POST form must send the token stored in the session. */
public class Csrf {

    public static String token(HttpSession session) {
        String t = (String) session.getAttribute("csrf");
        if (t == null) {
            byte[] bytes = new byte[24];
            new SecureRandom().nextBytes(bytes);
            t = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute("csrf", t);
        }
        return t;
    }

    public static boolean valid(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return false;
        }
        String expected = (String) session.getAttribute("csrf");
        String actual = req.getParameter("csrf");
        if (expected == null || actual == null) {
            return false;
        }
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8));
    }
}
