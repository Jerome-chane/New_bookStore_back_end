package com.example.my_book_store_back_end;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class MyBookStoreBackEndApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyBookStoreBackEndApplication.class, args);
	}
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public CommandLineRunner initData(BookRepository bookRepository, AuthorRepository authorRepository,CustomerRepository customerRepository,PurchaseResository purchaseResository) {
		return (args) -> {
			// save a couple of users
Book b1 = new Book("Tell me in words","es","The multifaceted catalan writer show us in his most romantic novel how, basically, our relationship is as easy as saying things clear. That's it.","https://preview.ibb.co/bC5ELQ/alex_min.png","https://preview.ibb.co/deD10Q/alex_min.png", null);
Book b2 = new Book("I see it black","es","Sometimes you think you understand something when someone explains it you, but when you have to code you come up with a doubt. Can you see it? Eduard Catala takes out his sleeve a science fiction master piece.","https://preview.ibb.co/dvM9AQ/eddie_min.png","https://preview.ibb.co/hnT0H5/eddie_min.png",null);
Book b3 = new Book("My algorithm is faster","es","Once again Warrior, once again the arrow. This is a book that will have you hooked from the moment you open its cover. It hooks you up fast, very fast, hyper fast.","https://preview.ibb.co/nF3Un5/flecha_min.png","https://preview.ibb.co/dUgbZk/flecha_min.png", null);
bookRepository.save(b1);bookRepository.save(b2);bookRepository.save(b3);
Author a1 = new Author("Jerome", "Chane", "j", "j.com", passwordEncoder().encode("123"));
authorRepository.save(a1);
Customer c1 = new Customer("Mr", "Potatoe", "Mr.P", "Mr.P.com", "123");
customerRepository.save(c1);

			Date date = new Date();
			Set<Book> set1 = new HashSet<>();set1.add(b1);set1.add(b3);
			Purchase purchase1 = new Purchase(c1,set1,date);
			purchaseResository.save(purchase1);



		};
	}

}


@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {
	@Autowired
	AuthorRepository authorRepository;
	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName-> {

			Author author = authorRepository.findByEmail(inputName);
			if (author != null) {
				return new User(author.getEmail(), author.getPassword(),
						AuthorityUtils.createAuthorityList("AUTHOR"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + inputName);
			}
		});
	}
}

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/api/**").permitAll()
				.antMatchers("/api/signup/**").permitAll()
//				.antMatchers("/api/login").permitAll()
				.antMatchers("/h2-console/**").permitAll()
				.antMatchers("/login.html").permitAll()
				.antMatchers("/login.js").permitAll()
				.antMatchers("/addBook").hasAnyAuthority("AUTHOR")
				.anyRequest()
				.fullyAuthenticated();
		http.formLogin()
				.usernameParameter("email")
				.passwordParameter("pwd")
				.loginPage("/api/login");
		http.logout().logoutUrl("/api/logout");
		// turn off checking for CSRF tokens
		http.csrf().disable();
//		System.out.println("login request received");
		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));
		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));
		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));
		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
		http.headers().frameOptions().disable();
	}


	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}

}