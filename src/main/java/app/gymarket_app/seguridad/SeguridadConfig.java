package app.gymarket_app.seguridad;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class  SeguridadConfig  {
  @Autowired
  private final UserDetailsService userDetailsService;


  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }


  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            .authorizeHttpRequests()
            .requestMatchers("/", "/webjars/**", "/css/**","/img/**", "/public/**", "/auth/**","/files/**").permitAll()
            .requestMatchers("/", "/app/**").authenticated()
            .anyRequest().authenticated()

            .and()
            .formLogin()
                 .loginPage("/auth/login")
                 .defaultSuccessUrl("/public/index",true)
                 .loginProcessingUrl("/auth/login")
                 .permitAll()
            .and()
            .logout()
                  .logoutUrl("/auth/logout")
                  .logoutSuccessUrl("/public/index")
            .permitAll();
    // Para que funcione el h2-console estas dos líneas son necesarias
    http.csrf().disable();
    http.headers().frameOptions().disable();
    return http.build();
  }


}