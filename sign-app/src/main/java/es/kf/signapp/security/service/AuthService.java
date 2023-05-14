package es.kf.signapp.security.service;

import es.kf.signapp.exceptions.RefreshTokenRequiredExecption;
import es.kf.signapp.model.User;
import es.kf.signapp.repositories.UserRepository;
import es.kf.signapp.security.dtos.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    private final UserRepository userRepository;

    public ResponseDTO getToken(String grantType, String username, String password, boolean withRefreshToken, String refreshToken) throws RefreshTokenRequiredExecption {
        String subject=null;
        String scope=null;
        if(grantType.equals("password")){
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            subject=authentication.getName();
            scope=authentication.getAuthorities()
                    .stream().map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(" "));

        } else if(grantType.equals("refreshToken")){
            if(refreshToken ==null) {
                throw new RefreshTokenRequiredExecption("Refresh  Token is required");
            }
            Jwt decodeJWT = null;
            decodeJWT = jwtDecoder.decode(refreshToken);
            subject=decodeJWT.getSubject();
            UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            scope=authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));
        }
        ResponseDTO responseDTO=new ResponseDTO();
        Map<String, String> idToken=new HashMap<>();
        Instant instant=Instant.now();
        assert subject != null;
        JwtClaimsSet jwtClaimsSet=JwtClaimsSet.builder()
                .subject(subject)
                .issuedAt(instant)
                .expiresAt(instant.plus(10, ChronoUnit.MINUTES))
                .issuer("SIGN-APP-ES")
                .claim("scope",scope)
                .build();
        String jwtAccessToken=jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
        idToken.put("accessToken",jwtAccessToken);
        if(withRefreshToken){
            JwtClaimsSet jwtClaimsSetRefresh=JwtClaimsSet.builder()
                    .subject(subject)
                    .issuedAt(instant)
                    .expiresAt(instant.plus(60, ChronoUnit.MINUTES))
                    .issuer("SIGN-APP-ES")
                    .build();
            String jwtRefreshToken=jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSetRefresh)).getTokenValue();
            idToken.put("refreshToken",jwtRefreshToken);
        }
        responseDTO.setAccessToken(idToken.get("accessToken"));
        responseDTO.setRefreshToken(idToken.get("refreshToken"));
        responseDTO.setUser(getUser(subject));

        return responseDTO;
    }

    private User getUser(String subject) {
        // get user by username
        return userRepository.findByUsername(subject);
    }

    public User me(HttpServletRequest request, HttpServletResponse response) {
        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;
        User user = null;

        // JWT Token is in the form "Bearer token". Remove Bearer word and get
        // only the Token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            System.out.println("jwtToken: " + jwtToken);
            username = jwtDecoder.decode(jwtToken).getSubject();

        } else {
            throw new RuntimeException("JWT Token does not begin with Bearer String");
        }

        if (username != null) {
           // verify if token is valid

            if (Objects.requireNonNull(jwtDecoder.decode(jwtToken).getExpiresAt()).isBefore(Instant.now())) {
                System.out.println("JWT Token has expired");
                throw new RuntimeException("JWT Token has expired");
            }
            // if token is valid configure Spring Security to manually set
            // authentication
            user = getUser(username);
        }
        return user;
    }
}
