package example.com.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

import example.com.repository.UserRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	public UserRepository userRepository;

	@Autowired
	public AppConfig appConfig;

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(appConfig.passwordEncoder());
		return daoAuthenticationProvider;

	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().disable() // Optional: consider enabling for production
				.authorizeHttpRequests()
				//.requestMatchers("/user/**").hasRole("USER") // Ensure users have the ROLE_USER
				.requestMatchers("/**").permitAll() // Allow public access to other paths
				.and()
				.formLogin()
				.loginPage("/signin") // Custom login page
				.loginProcessingUrl("/userlogin") // Form login processing URL
				//.failureHandler(new CustomAuthenticationFailureHandler()) // Add the failure handler
				.defaultSuccessUrl("/user/homePage", true) // Redirect on successful login
				.permitAll() // Allow everyone to access the login page
				.and()
				.logout() // Optional: configure logout
				.logoutUrl("/logout") // Logout URL
				.logoutSuccessUrl("/signin?logout") // Redirect after logout
				.permitAll();

		return http.build();
	}

	//Disable Security Temporarily (for Development)
//	@Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .authorizeHttpRequests(auth -> auth
//                .anyRequest().permitAll()  // allow all URLs without login
//            )
//            .csrf().disable()  // disable CSRF for development
//            .formLogin().disable(); // disable login form
//
//        return http.build();
//    }
}
