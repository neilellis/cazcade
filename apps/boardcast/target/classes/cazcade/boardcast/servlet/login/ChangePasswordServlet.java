package cazcade.boardcast.servlet.login;

import cazcade.boardcast.servlet.AbstractBoardcastServlet;
import cazcade.common.Logger;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.request.ChangePasswordRequest;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author neilellis@cazcade.com
 */
public class ChangePasswordServlet extends AbstractBoardcastServlet {
    @Nonnull
    private static final Logger log = Logger.getLogger(ChangePasswordServlet.class);


    @Override
    protected void doGet(@Nonnull final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/_pages/password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(@Nonnull final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        final String username = req.getParameter("username");
        final String password = req.getParameter("password");
        final String passwordConfirm = req.getParameter("password_confirm");
        final String hash = req.getParameter("hash");

        try {
            final HttpSession session = req.getSession(true);
            if (username == null) {
                req.setAttribute("error", "Username required.");
                if (hash == null) {
                    req.getRequestDispatcher("/_pages/forgot.jsp").forward(req, resp);
                    return;
                }
                else {
                    req.getRequestDispatcher("/_pages/password.jsp").forward(req, resp);
                    return;
                }
            }
            if (hash == null) {
                final ChangePasswordRequest response = dataStore.process(new ChangePasswordRequest(new LiquidSessionIdentifier(
                        username
                )
                )
                                                                        );
                if (response.getResponse().isError()) {
                    req.setAttribute("error", "Password reset request failed.");
                }
                else {
                    req.setAttribute("message", "Password reset sent, please check your email.");
                }
                req.getRequestDispatcher("/_pages/forgot.jsp").forward(req, resp);
            }
            else {
                if (!password.equals(passwordConfirm)) {
                    req.setAttribute("error", "Passwords didn't match.");
                }
                else if (!(password.matches(".*[^a-zA-Z].*") && password.matches(".*[a-zA-Z].*"))) {
                    req.setAttribute("error", "Invalid password, please include letters and non letters.");
                }
                else {
                    final ChangePasswordRequest response = dataStore.process(new ChangePasswordRequest(new LiquidSessionIdentifier(
                            username
                    ), password, hash
                    )
                                                                            );
                    if (response.getResponse().isError()) {
                        req.setAttribute("error", "Password change failed.");
                    }
                    else {
                        req.setAttribute("message", "Password changed.");
                    }
                }
                req.getRequestDispatcher("/_pages/password.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            log.error(e);
            throw new ServletException(e);
        }
    }


}
