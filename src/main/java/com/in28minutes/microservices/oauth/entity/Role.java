package com.in28minutes.microservices.oauth.entity;


import java.io.Serializable;


public class Role implements Serializable {


    private Long id;

    private String nombre;

    //Si queremos que sea bidireccional
    //@ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles")
    //private List<Usuario> usuarios;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

   /* public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }*/
}
