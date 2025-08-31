package example.com.configuration;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String errorMessage = "Invalid username or password";

        if (exception.getMessage().contains("disabled")) {
            errorMessage = "Your account is not enabled. Please verify your email.";
        }

        //request.getRequestDispatcher("/errorMsg.html").forward(request, response);
        response.sendRedirect("/signin?error=" + errorMessage);
    }
}

