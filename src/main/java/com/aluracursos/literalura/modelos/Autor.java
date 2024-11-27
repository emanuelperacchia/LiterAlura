package com.aluracursos.literalura.modelos;

import jakarta.persistence.*;
import java.util.List;
@Entity
@Table(name = "autor", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nombre"})
})
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   private long id;

    private String nombre;
    private Integer anoDeNacimiento;
    private Integer anoDeMuerte;

   @OneToMany(mappedBy = "autor", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Libro> libros;

    public Autor(String nombre, Integer anoDeNacimiento, Integer anoDeMuerte) {
        this.nombre = nombre;
        this.anoDeNacimiento = anoDeNacimiento;
        this.anoDeMuerte = anoDeMuerte;
    }

    public Autor() {

    }

    public void setAnoDeMuerte(Integer anoDeMuerte) {
        this.anoDeMuerte = anoDeMuerte;
    }

    public List<Libro> getLibro() {
        return libros;
    }

    public void setLibro(List<Libro> libro) {
        this.libros = libro;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getAnoDeNacimiento() {
        return this.anoDeNacimiento;
    }

    public void setAnoDeNacimiento(Integer anoDeNacimiento) {
        this.anoDeNacimiento = anoDeNacimiento;
    }

    public Integer getAnoDeMuerte() {
        return anoDeMuerte;
    }

    public void setAñoDeMuerte(Integer anoDeMuerte) {
        this.anoDeMuerte = anoDeMuerte;
    }

    @Override
    public String toString() {
        return "Persona{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", añoDeNacimiento=" + anoDeNacimiento +
                ", añoDeMuerte=" + anoDeMuerte +
                '}';
    }
}
