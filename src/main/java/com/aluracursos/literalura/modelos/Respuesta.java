package com.aluracursos.literalura.modelos;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Respuesta(@JsonAlias("results") List<DatosLibros> resultado){
}
