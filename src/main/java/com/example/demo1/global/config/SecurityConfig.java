package com.example.demo1.global.config;

import com.example.demo1.global.security.JwtAuthenticationFilter;
import com.example.demo1.global.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 💡 [추가] CORS 설정을 연결합니다.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 💡 [가장 중요] CSRF 보안 설정을 확실하게 꺼줍니다.
                .csrf(csrf -> csrf.disable())

                // H2 콘솔 사용을 위한 프레임 비활성화
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                // 세션을 사용하지 않고 JWT 토큰 방식을 사용하므로 STATELESS 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 💡 [경로 허용] 인증 없이 통과할 화이트리스트 지정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/oauth/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/oauth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )

                // 커스텀 JWT 필터를 시큐리티 필터 체인에 등록
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 💡 [추가] 쿠키 전송을 허용하는 CORS 설정 블록
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 쿠키를 주고받으려면 와일드카드(*) 대신 originPattern을 사용하거나 명시적 주소를 적어야 합니다.
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        // ⭐️ 핵심: 이 설정이 true여야 응답 세팅한 쿠키가 차단되지 않고 클라이언트에 들어갑니다.
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}