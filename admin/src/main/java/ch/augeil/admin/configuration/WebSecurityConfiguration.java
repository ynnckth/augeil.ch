package ch.augeil.admin.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String REDEEM_DOWNLOAD_CODE_ENDPOINT = "/albums/*/redeem";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers(REDEEM_DOWNLOAD_CODE_ENDPOINT).permitAll()
                .anyRequest().authenticated().and().httpBasic();
    }
}
