package com.pm.authservice.config;

import com.pm.authservice.dto.AuthResponseDTO;
import com.pm.authservice.dto.UserServiceRequestDTO;
import com.pm.authservice.dto.UserServiceResponseDTO;
import com.pm.authservice.model.AuthProvider;
import com.pm.authservice.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler
        implements AuthenticationSuccessHandler {

    private final RestTemplate restTemplate;

    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        try {

            System.out.println("OAuth success handler reached");

            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            System.out.println("OAuth user extracted");

            String email = oAuth2User.getAttribute("email");
            String firstName = oAuth2User.getAttribute("given_name");
            String lastName = oAuth2User.getAttribute("family_name");
            String providerId = oAuth2User.getAttribute("sub");

            System.out.println("Email: " + email);

            UserServiceRequestDTO dto = new UserServiceRequestDTO();
            dto.setEmail(email);
            dto.setFirstName(firstName);
            dto.setLastName(lastName);
            dto.setAuthProvider(AuthProvider.GOOGLE);
            dto.setProviderId(providerId);

            System.out.println("Calling user service");

            UserServiceResponseDTO createdUser = restTemplate.postForObject(
                    "http://localhost:4001/api/users/find-or-create",
                    dto,
                    UserServiceResponseDTO.class
            );

            System.out.println("User service response:");
            System.out.println(createdUser);

            if (createdUser == null) {
                throw new RuntimeException("createdUser is null");
            }

            System.out.println("Generating tokens");

            AuthResponseDTO tokens = authService.handleGoogleLogin(createdUser);

            System.out.println("Tokens generated");

            response.setContentType("application/json");

            response.getWriter().write("""
        {
          "accessToken":"%s",
          "refreshToken":"%s"
        }
        """.formatted(
                    tokens.getAccessToken(),
                    tokens.getRefreshToken()
            ));

        } catch (Exception e) {

            e.printStackTrace();

            response.setContentType("text/plain");

            response.getWriter().write(
                    "ERROR:\n\n" + e.getMessage()
            );
        }
    }
}