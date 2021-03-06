package dev.mateuszkowalczyk.puncher.controller;

import dev.mateuszkowalczyk.puncher.model.LoginData;
import dev.mateuszkowalczyk.puncher.model.AuthorizationToken;
import dev.mateuszkowalczyk.puncher.model.RegisterDTO;
import dev.mateuszkowalczyk.puncher.response.InvalidDataResponse;
import dev.mateuszkowalczyk.puncher.response.Response;
import dev.mateuszkowalczyk.puncher.response.SuccessfulCreateResponse;
import dev.mateuszkowalczyk.puncher.security.JwtTokenProvider;
import dev.mateuszkowalczyk.puncher.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

@RestController
@RequestMapping(value = "/api")
public class AuthController {
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;
    private AuthService authService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authService = authService;
    }

    @ResponseBody
    @PostMapping(value = "/login")
    public Response login(@RequestBody LoginData data, HttpServletResponse response) {
        try {
            String username = data.getUsername();
            this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
            String token = jwtTokenProvider.create(username, new ArrayList<String>() {
                {
                    add("ROLE_USER");
                }
            });
            AuthorizationToken authorizationToken = new AuthorizationToken();
            authorizationToken.setUsername(username);
            authorizationToken.setToken(token);
            authorizationToken.setMessage("Successful");

            return authorizationToken;
        } catch (AuthenticationException e) {
            return new InvalidDataResponse("Invalid login data");
        }
    }

    @PostMapping(value = "/register")
    public Response register(@RequestBody RegisterDTO data) {
        this.authService.register(data);

        return new SuccessfulCreateResponse("Created succesful now can you login");
    }
}
