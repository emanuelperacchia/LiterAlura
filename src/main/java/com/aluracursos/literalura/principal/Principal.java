package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.modelos.*;
import com.aluracursos.literalura.repository.AutorRepository;
import com.aluracursos.literalura.repository.LibroRepository;
import com.aluracursos.literalura.servicios.ConsumoAPI;
import com.aluracursos.literalura.servicios.ConvierteDatos;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class Principal {
    private static final Logger logger = LoggerFactory.getLogger(Principal.class);
    private final ConsumoAPI consumoAPI;
    private final ConvierteDatos convierteDatos;
    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private Scanner scanner = new Scanner(System.in);
    private static final String URL = "https://gutendex.com/books/";

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository, ConsumoAPI consumoAPI, ConvierteDatos convierteDatos) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
        this.convierteDatos = convierteDatos;
        this.consumoAPI = consumoAPI;
    }

    public void muestraElMenu() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("------------------");
            System.out.println("1- Buscar libro por título.");
            System.out.println("2- Listar libros registrados.");
            System.out.println("3- Listar autores registrados.");
            System.out.println("4- Listar autores vivos en un determinado año.");
            System.out.println("5- Listar libros por idiomas.");
            System.out.println("6- Top 10 libros más descargados");
            System.out.println("0- Salir.");
            opcion = leerOpcionMenu();

            switch (opcion) {
                case 1 -> buscarLibros();
                case 2 -> librosRegistrados();
                case 3 -> autoresRegistrados();
                case 4 -> autoresVivosPeriodo();
                case 5 -> librosPorIdiomas();
                case 6 -> top10LibrosMasDescargados();
                case 0 -> {
                    System.out.println("Cerrando la aplicación...");
                    System.exit(0);
                }
                default -> System.out.println("Opción inválida");
            }
        }
    }
                       private int leerOpcionMenu() {
                          int opcion;
                          while (true) {
                              try {
                                  System.out.print("Elija el número de opción que desee: ");
                                  opcion = scanner.nextInt();
                                  scanner.nextLine();
                                  return opcion;
                              } catch (InputMismatchException e) {
                                  System.out.println("Entrada no válida. Por favor, ingrese un número entero.");
                                  scanner.nextLine();
                              }
                          }
                      }

                      public Respuesta getLibro() {
                          try {
                              System.out.println("Ingrese el nombre del libro o autor:");
                              String textoBuscado = scanner.nextLine();

                              if (textoBuscado == null || textoBuscado.trim().isEmpty()){
                                  System.out.println("El texto de búsqueda no puede estar vacío.");
                                  return null;
                              }
                              String urlBusqueda = URL + "?search=" + textoBuscado.replace(" ", "%20");
                              String json = consumoAPI.obtenerDatos(urlBusqueda);
                              if(json == null || json.isEmpty()){
                                  System.out.println("No se recibió respuesta de la API.");
                                  return null;
                              }
                              Respuesta datos = convierteDatos.obtenerDatos(json, Respuesta.class);

                              if (datos == null || datos.resultado().isEmpty()) {
                                  System.out.println("No se encontraron libros que coincidan con: " + textoBuscado);
                                  return null;
                              }
                              return datos;
                          }catch (Exception e){
                              logger.error("Error al obtener los datos del libro desde la API: " + e.getMessage());
                              System.out.println("Ocurrió un error al intentar obtener los datos del libro.");
                              return null;
                          }
                      }
                      public String normalizarTexto(String texto) {
                          if (texto == null) return null;

                          texto = texto.toLowerCase();
                          texto = texto.replaceAll("[áàäâ]", "a")
                                  .replaceAll("[éèëê]", "e")
                                  .replaceAll("[íìïî]", "i")
                                  .replaceAll("[óòöô]", "o")
                                  .replaceAll("[úùüû]", "u")
                                  .replaceAll("\\s+", " ") // Normaliza espacios múltiples
                                  .trim();

                          return texto;
                      }
                  private Optional<Libro> guardarPrimerLibroSiNoExiste(Respuesta respuesta) {
                      if (respuesta == null || respuesta.resultado() == null || respuesta.resultado().isEmpty()) {
                          return Optional.empty();
                      }

                      DatosLibros datosLibro = respuesta.resultado().get(0);

                      try {
                          String tituloNormalizado = normalizarTexto(datosLibro.titulo());
                          if(tituloNormalizado.length()>500){
                              tituloNormalizado = tituloNormalizado.substring(0,500);
                          }

                          Optional<Libro> libroExistente = libroRepository.findByTituloIgnoreCase(tituloNormalizado);
                          if (libroExistente.isPresent()) {
                              System.out.println("El libro '" + datosLibro.titulo() + "' ya está registrado.");
                              return libroExistente;
                          }

                          Autor autor = null;
                          if (datosLibro.autor() != null && !datosLibro.autor().isEmpty()) {
                              DatosAutor datosAutor = datosLibro.autor().get(0);
                              String nombreAutorNormalizado = normalizarTexto(datosAutor.nombre());

                              Optional<Autor> autorExistente = autorRepository.findByNombreIgnoreCase(nombreAutorNormalizado);

                              autor = autorExistente.orElseGet(() -> {
                                  Autor nuevoAutor = new Autor();
                                  nuevoAutor.setNombre(datosAutor.nombre());
                                  nuevoAutor.setAnoDeNacimiento(datosAutor.anoDeNacimiento());
                                  nuevoAutor.setAnoDeMuerte(datosAutor.anoDeMuerte());
                                  return autorRepository.save(nuevoAutor);
                              });
                          }

                          Libro libro = new Libro();
                          libro.setTitulo(datosLibro.titulo());
                          libro.setDescargas(datosLibro.descargas());
                          libro.setIdioma(obtenerIdioma(datosLibro));
                          libro.setAutor(autor);

                          Libro libroGuardado = libroRepository.save(libro);
                          System.out.println("Libro guardado: " + datosLibro.titulo());

                          return Optional.of(libroGuardado);

                      } catch (Exception e) {
                          logger.error("Error al procesar el libro: " + e.getMessage());
                          System.out.println("Error al procesar el libro: " + e.getMessage());
                          return Optional.empty();
                      }
                  }

                      private String obtenerIdioma(DatosLibros datosLibro) {
                          return datosLibro.idioma() != null && !datosLibro.idioma().isEmpty()
                                  ? datosLibro.idioma().get(0)
                                  : "Desconocido";
                      }
                      public void buscarLibros() {
                          var datos = getLibro();
                          if (datos == null || datos.resultado() == null || datos.resultado().isEmpty()) {
                              System.out.println("No se encontraron resultados");
                              return;
                          }
                          Optional<Libro> libroProcesado = guardarPrimerLibroSiNoExiste(datos);

                          libroProcesado.ifPresent(libro -> {
                              List<Libro> libroParaImprimir = Collections.singletonList(libro);
                              imprimirLibros(libroParaImprimir);
                          });
                      }

                      private void librosPorIdiomas() {

                          List<String> idiomasValidos = List.of("es", "it", "en", "ja", "fr", "pt", "ru", "zh", "de", "ar");

                          System.out.println("Ingresa el idioma que deseas buscar de la siguiente lista:");
                          System.out.println(
                                  """ 
                        **************************************************
                        *               LIBROS POR IDIOMA                *
                        **************************************************
                            es - Español                it - Italiano
                            en - Inglés                 ja - Japonés
                            fr - Francés                pt - Portugués
                            ru - Ruso                   zh - Chino Mandarín
                            de - Alemán                 ar - Árabe
                        """
        );

        String idioma;
        while (true) {
            System.out.print("Ingrese la clave de idioma: ");
            idioma = scanner.nextLine().toLowerCase();
            if (idiomasValidos.contains(idioma)) {
                List<Libro> libros = libroRepository.findByIdioma(idioma);
                imprimirLibros(libros);
                break;
            } else {
                System.out.println("Idioma no válido. Por favor ingrese uno de los idiomas válidos.");
            }
        }
    }
    private void top10LibrosMasDescargados() {
        System.out.println(
                """ 
                    **************************************************
                    *          TOP 10 LIBROS MÁS DESCARGADOS         *
                    **************************************************
                """
        );

        Pageable pageable = PageRequest.of(0, 10); // Página 0 con 10 elementos
        List<Libro> top10Libros = libroRepository.findAllByOrderByDescargasDesc(pageable).getContent();

        imprimirLibros(top10Libros);
    }


   private void autoresVivosPeriodo() {
       imprimirEncabezadoAutoresVivos();

       int minima = leerFecha("Ingrese la primera fecha: ");
       int maxima = leerFecha("Ingrese la segunda fecha: ");

       if (minima > maxima) {
           System.out.println("Error: La fecha mínima no puede ser mayor que la máxima.");
           return;
       }

       List<Autor> autores = autorRepository.findAutoresByRangoNacimientoYMuerte(minima, maxima);
       if (autores.isEmpty()) {
           System.out.println("No se encontraron autores vivos en el periodo especificado.");
           return;
       }

       autores.forEach(this::mostrarAutorConLibros);
   }

    private void imprimirEncabezadoAutoresVivos() {
        System.out.println(
                """
                **************************************************
                *          AUTORES VIVOS EN UN PERIODO           *
                **************************************************
                """
        );
    }

    private int leerFecha(String mensaje) {
        System.out.print(mensaje);
        while (!scanner.hasNextInt()) {
            System.out.println("Por favor, ingrese un número válido.");
            scanner.next();
            System.out.print(mensaje);
        }
        return scanner.nextInt();
    }

    private void mostrarAutorConLibros(Autor autor) {

        autor.getLibro().size();

        System.out.println(
                """
                --------------------------------------------------
                AUTOR: %s
                Año de nacimiento: %s
                Libros del autor:
                """.formatted(
                        autor.getNombre(),
                        autor.getAnoDeNacimiento() != null ? autor.getAnoDeNacimiento() : "Desconocido"
                )
        );

        if (autor.getLibro().isEmpty()) {
            System.out.println("  (Sin libros registrados)");
        } else {
            autor.getLibro().forEach(libro -> System.out.println("  - " + libro.getTitulo()));
        }

        System.out.println("--------------------------------------------------");
    }


    private void autoresRegistrados() {
        System.out.println(
                """ 
                    **************************************************
                    *              AUTORES REGISTRADOS               *
                    **************************************************
                """
        );
        List<Autor> autores = autorRepository.findAllAutores();

        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
            return;
        }

        for (int i = 0; i < autores.size(); i++) {
            System.out.println((i + 1) + ". " + autores.get(i).getNombre());
        }
    }

    private void librosRegistrados() {
            System.out.println(
                    """ 
                        **************************************************
                        *              LIBROS REGISTRADOS                *
                        **************************************************
                    """
            );
        List<Libro> libros = libroRepository.findAllLibros();

        if (libros.isEmpty()) {
                System.out.println("No hay libros registrados.");
                return;
            }
            imprimirLibros(libros);
        }
@Transactional
    private void imprimirLibros(List<Libro> libros) {
        if (libros == null || libros.isEmpty()) {
            System.out.println(
                    """ 
                        **************************************************
                        *              NO HAY LIBROS PARA MOSTRAR        *
                        **************************************************
                    """
            );
            return;
        }

        System.out.println(
                """ 
                    **************************************************
                    *                   LISTA DE LIBROS               *
                    **************************************************
                """
        );

        libros.forEach(libro -> {
            System.out.println(
                    """
                        --------------------------------------------------
                        TÍTULO: %s
                        --------------------------------------------------
                    """.formatted(libro.getTitulo())
            );

            if (libro.getAutor() != null) {
                Autor autor = libro.getAutor();
                System.out.printf(
                        """
                        Autor: %s
                        Año de nacimiento: %s
                        Año de muerte: %s
                        """,
                        autor.getNombre(),
                        autor.getAnoDeNacimiento() != null ? autor.getAnoDeNacimiento() : "Desconocido",
                        autor.getAnoDeMuerte() != null ? autor.getAnoDeMuerte() : "Aún vive"
                );
            } else {
                System.out.println("Autor: Desconocido");
            }

            System.out.println("Idiomas: " + (libro.getIdioma() != null ? String.join(", ", libro.getIdioma()) : "No especificado"));
            System.out.println("Descargas: " + libro.getDescargas());
            System.out.println(
                    """
                        --------------------------------------------------
                    """);});
    }

}