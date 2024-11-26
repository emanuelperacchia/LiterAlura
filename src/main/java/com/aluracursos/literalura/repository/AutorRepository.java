package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.modelos.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    @Query("""
    SELECT a 
    FROM Autor a 
    LEFT JOIN FETCH a.libros 
    WHERE a.anoDeNacimiento BETWEEN :min AND :max
    AND (a.anoDeMuerte IS NULL OR a.anoDeMuerte >= :min)
    ORDER BY a.anoDeNacimiento
""")
    List<Autor> findAutoresByRangoNacimientoYMuerte(@Param("min") int min, @Param("max") int max);
    @Query("SELECT DISTINCT a FROM Autor a")
    List<Autor> findAllAutores();
    Optional<Autor> findByNombre(String nombre);
}
