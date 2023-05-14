package es.kf.signapp.security.controller;

import es.kf.signapp.exceptions.RefreshTokenRequiredExecption;
import es.kf.signapp.model.User;
import es.kf.signapp.security.dtos.LoginDTO;
import es.kf.signapp.security.dtos.ResponseDTO;
import es.kf.signapp.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/token")
    public ResponseEntity<ResponseDTO> jwtToken(@RequestBody LoginDTO loginDTO) {
        ResponseDTO token;
        try {
            token = authService.getToken(loginDTO.getGrantType(), loginDTO.getUsername(), loginDTO.getPassword(), loginDTO.isWithRefreshToken(), loginDTO.getRefreshToken());
        } catch (RefreshTokenRequiredExecption | JwtException e) {
            return new ResponseEntity<>(new ResponseDTO(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(token, HttpStatus.OK);

    }

    @GetMapping("/auth/me")
    public ResponseEntity<User> me(HttpServletRequest request, HttpServletResponse response) {
        try {
            return new ResponseEntity<>(authService.me(request, response), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

    }

}
