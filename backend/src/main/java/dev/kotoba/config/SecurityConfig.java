package dev.kotoba.config;
import java.time.Clock;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;
@Configuration public class SecurityConfig {
 @Bean Clock clock(){ return Clock.systemUTC(); }
 @Bean JwtDecoder jwtDecoder(@Value("${app.issuer}") String issuer,@Value("${app.jwks}") String jwks){
  var decoder=NimbusJwtDecoder.withJwkSetUri(jwks).jwsAlgorithms(a->{a.add(org.springframework.security.oauth2.jose.jws.SignatureAlgorithm.ES256);a.add(org.springframework.security.oauth2.jose.jws.SignatureAlgorithm.RS256);}).build();
  OAuth2TokenValidator<Jwt> audience=token->{
   try { UUID.fromString(token.getSubject()); } catch(Exception e){return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token"));}
   return token.getAudience().contains("authenticated") && "authenticated".equals(token.getClaimAsString("role"))?OAuth2TokenValidatorResult.success():OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token"));
  };
  decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefaultWithIssuer(issuer),audience));return decoder;
 }
 @Bean SecurityFilterChain security(HttpSecurity http) throws Exception {
  return http.csrf(c->c.disable()).cors(Customizer.withDefaults()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(a->a.requestMatchers("/health").permitAll().anyRequest().authenticated())
   .oauth2ResourceServer(o->o.jwt(Customizer.withDefaults()).authenticationEntryPoint((req,res,e)->{res.setStatus(401);res.setContentType("application/problem+json");res.getWriter().write("{\"status\":401,\"detail\":\"Vui lòng đăng nhập lại.\"}");}))
   .build();
 }
 @Bean CorsConfigurationSource cors(@Value("${app.cors-origins}") String origins){
  var c=new CorsConfiguration();c.setAllowedOrigins(Arrays.stream(origins.split(",")).map(String::trim).toList());c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));c.setAllowedHeaders(List.of("Authorization","Content-Type"));c.setMaxAge(3600L);
  var source=new UrlBasedCorsConfigurationSource();source.registerCorsConfiguration("/**",c);return source;
 }
}
