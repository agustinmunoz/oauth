package com.in28minutes.microservices.oauth.service;

import com.in28minutes.microservices.oauth.entity.Role;
import com.in28minutes.microservices.oauth.entity.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService implements UserDetailsService {

    private Logger log = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioFeign feign;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //Interface propia de SpringSEcurity
        //Aquí vamos a obtener el usuario mediante el username
        // Pero lo vamos a hacer llamando a feign ususrios-service
        // Inyectamos Feign




        Usuario usuario = feign.findByUsername(username);

        if(usuario==null){
            log.error("Fallo Feign. No existe el usuario en la BBDD "+usuario);
            throw new UsernameNotFoundException("Fallo Feign. No existe el usuario en la BBDD "+ usuario);
        }

        // No podemos devolver sirectamente el Usuario User
        // Se devuelve un UserDetail que es una interface, User (propio de Spring Security es su implementacion)

        //Ver atributos de la clase User de Security (username,password,enable,accounrNotExpired, creditialNonExpired,acountNonLOcket y autorithies)

        //Los authorities son los roles, que nos vienen en usuario por la relacion manytomany
        //Pero los roles de SpringSecurity son del tipo GrantedAuthority, así que tenemos que mappear los que nos pvienen del Usuario
        //GrantedAuthority es la interface y SimpleGrantedAuthority es su implementacion

        List<GrantedAuthority>  roles_security = usuario.getRoles()
                .stream()
                .map(role-> new SimpleGrantedAuthority(role.getNombre()))
                .collect(Collectors.toList());



       /* List<GrantedAuthority>  roles_security = new ArrayList<>();


        for (Role role : usuario.getRoles()){
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role.getNombre());
            log.info("Rol del usuario "+ role.getNombre());
            roles_security.add(simpleGrantedAuthority);
        }*/




        User user_security = new User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getEnable(),
                true,
                true,
                true,
                roles_security
                );

        log.info("Usuario autenticado "+ user_security);

        return user_security;

        //y como es un @Service lo pdemos inyectar en otras clase
    }
}
