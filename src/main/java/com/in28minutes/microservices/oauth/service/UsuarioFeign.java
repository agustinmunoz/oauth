package com.in28minutes.microservices.oauth.service;

import com.in28minutes.microservices.oauth.entity.Usuario;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="servicio-usuarios")
public interface UsuarioFeign {

    @GetMapping("/usuarios/search/findByUsername")
    public Usuario findByUsername(@RequestParam String username);

}
