package com.in28minutes.microservices.oauth.security;

import com.in28minutes.microservices.oauth.service.UsuarioService;
import feign.Feign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    //Inyectamos el usuario Autenticado que hemos conseguido a través de Feign
    //En el ejemplo inyecta la interface UserDetailsService pero yo voy a inyectar la implementacion  UsuarioService

    @Autowired
    //UserDetailsService usuarioService;
    private UsuarioService usuarioService;

    //Registramos el usuario en ael autenticationManager

    //Para ello sobreescribimos el métoso configure y lo @Autowiredeamos para poderlo inyectar luego

    //Metodo para encriptar la contraseña. La contraseña se encripta y se compara con la de BBDD que también tiene
    //que estar enciptada en BBDD
    //Mirar App Principal para ver como se encripta una contraseña y luego la metemos en BBDD

    //Lo registramos como bean, para poder usarlo
    // la diferencia entre un @Bean y por ejemplo un @Service es que el @Bean no son Clases Propias sino del sistema
    //Entonces cuando haho un @Autowired de este Bean es igual que si hiciera un new BCryptPasswordEncoder()

    @Bean
    public BCryptPasswordEncoder enciptarPass(){
        return new BCryptPasswordEncoder();
    }


    @Autowired
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //Añadimos el usuario al Manager  y enciptamos la contraseña
        auth.userDetailsService(this.usuarioService)
                .passwordEncoder(enciptarPass());
    }


    //Bobreescribimsos el Autentication Manager


    @Override
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }


    // Esto es lo añadido

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
