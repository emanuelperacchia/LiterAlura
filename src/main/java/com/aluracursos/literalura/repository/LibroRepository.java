package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.modelos.Libro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    @Query("SELECT l FROM Libro l WHERE l.idioma = :idioma ORDER BY l.idioma")
    List<Libro> findByIdioma(@Param("idioma") String idioma);

    Page<Libro> findAllByOrderByDescargasDesc(Pageable pageable);

    @Query("SELECT l FROM Libro l")
    List<Libro> findAllLibros();

    Optional<Libro> findByTitulo(String titulo);
}
