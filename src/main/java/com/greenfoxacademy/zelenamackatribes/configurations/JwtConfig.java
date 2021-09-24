package com.greenfoxacademy.zelenamackatribes.configurations;

import com.greenfoxacademy.zelenamackatribes.utils.filters.JwtAuthorizationFilter;
import com.greenfoxacademy.zelenamackatribes.utils.filters.JwtExceptionCatcherFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
class JwtConfig extends WebSecurityConfigurerAdapter {

  private JwtAuthorizationFilter jwtAuthorizationFilter;
  private JwtExceptionCatcherFilter jwtExceptionCatcherFilter;

  @Autowired
  public JwtConfig(
      JwtAuthorizationFilter jwtAuthorizationFilter,
      JwtExceptionCatcherFilter jwtExceptionCatcherFilter
  ) {
    this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    this.jwtExceptionCatcherFilter = jwtExceptionCatcherFilter;
  }

  @Bean
  public FilterRegistrationBean disableJwtAuthFilterAutoload(JwtAuthorizationFilter filter) {
    FilterRegistrationBean jwtHaltFilter = new FilterRegistrationBean(filter);
    jwtHaltFilter.setEnabled(false);
    return jwtHaltFilter;
  }

  @Bean
  public FilterRegistrationBean disableJwtExCatcherFilterAutoload(
      JwtExceptionCatcherFilter filter) {
    FilterRegistrationBean jwtExCatcherFilter = new FilterRegistrationBean(filter);
    jwtExCatcherFilter.setEnabled(false);
    return jwtExCatcherFilter;
  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring()
        .antMatchers("/login", "/register", "/kingdom/leaderboard", "/confirm");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .cors()
        .and()
        .exceptionHandling()
        .and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .antMatchers(HttpMethod.POST, "/login").permitAll()
        .antMatchers(HttpMethod.POST,"/register").permitAll()
        .antMatchers(HttpMethod.GET,"/kingdom/leaderboard").permitAll()
        .antMatchers(HttpMethod.GET, "/login", "/register", "/confirm").permitAll()
        .anyRequest().authenticated()
        .and()
        .addFilterBefore(
            jwtExceptionCatcherFilter,
            UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(
            jwtAuthorizationFilter,
            UsernamePasswordAuthenticationFilter.class);
  }
}
