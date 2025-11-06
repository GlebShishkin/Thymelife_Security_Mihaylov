package ru.codekitchen.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import ru.codekitchen.entity.User;
import ru.codekitchen.entity.UserRole;
import ru.codekitchen.repository.UserRepository;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Repository нужен, чтобы сходить за пользователем в бд
    private final UserRepository userRepository;
    @Autowired
    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        var entryPoint = new LoginUrlAuthenticationEntryPoint("/login");
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/", "/login", "/registration", "/error", "/css/**", "/static/**").permitAll()
                        .requestMatchers("/account/**").hasAnyRole(UserRole.USER.name(), UserRole.ADMIN.name())
                        .requestMatchers("/admin/**").hasRole(UserRole.ADMIN.name())
                        .anyRequest().authenticated()
                )
                // добавим вывод стека, если исключение (см. https://alexkosarev.name/2023/06/03/authentication-entry-point-spring-security/)
                .exceptionHandling(c ->
                        // основная точка входа
                        c.authenticationEntryPoint((req, res, e) -> {
                                    // вывод стека вызова
                                    e.printStackTrace();
                                    // выведем контекс аутентификации
                                    log.info("######securityFilterChain: " + SecurityContextHolder.getContext().getAuthentication().getName());
                                    log.info("######securityFilterChain: " + SecurityContextHolder.getContext().toString());
                                    // использование основной точки входа ("login") после исключения - можно убрать
                                    entryPoint.commence(req, res, e);
                                }))
                .formLogin(formLogin -> formLogin
                                .loginPage("/login")   // определяем кастомный url для логина
                                .permitAll()
                                .usernameParameter("email") // переопределим название для логина по умолчанию ("name")
                                .defaultSuccessUrl("/account")  // страница, куда перенаправляет успешный логин
//                                .failureUrl("/error") //!!! не делать так, т.к. нужно вернуть url по умолчанию: "localhost:8080/login?error"
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .permitAll()
                )
                .build();
    }

    // bean для хэширования пароля
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // bean для идентификации пользователя: поиск пользователя, в данном случае по его email
    @Bean
    public UserDetailsService userDetailsService() {
        // т.к. UserDetailsService - это не класс, а интерфейс, нужно переопределить его как анонимный класс
        return new UserDetailsService() {
            // получаем из бд UserDetails, кот содержит: логин, пароль, роли
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                User user = userRepository
                        .findByEmailIgnoreCase(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User with email = " + username + " not found"));
                Set<SimpleGrantedAuthority> roles = Collections.singleton(user.getRole().toAuthority());    // toAuthority преобразует "ROLE_" + UserRole.name()

                org.springframework.security.core.userdetails.User usrDet = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), roles);
log.info("############## userDetailsService: usrDet.getAuthorities() = " + usrDet.getAuthorities() + "; usrDet.toString() = " + usrDet.toString());
                return usrDet;
                //                return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), roles);
            }
        };
    }
}
