package com.aluracursos.literalura.modelos;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private Integer descargas;

    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Autor autor;

    @Column(name = "idioma")
    private String idioma;

public Libro(){}
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getDescargas() {
        return descargas;
    }

    public void setDescargas(int descargas) {
        this.descargas = descargas;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {

    this.idioma = idioma;
    }

    public Libro(String idioma,Autor autor, Integer descargas, String titulo, Long id) {
        this.idioma = idioma;
        this.autor = autor;
        this.descargas = descargas;
        this.titulo = titulo;
    }

    @Override
    public String toString() {
        return "Libro{" +
                "id = " + id +
                ", titulo = '" + titulo + '\'' +
                ", autor = " + (autor != null ? autor.getNombre() : "N/A") +
                ", lenguage = " + idioma +
                ", descargas = " + descargas +
                '}';
    }

}
