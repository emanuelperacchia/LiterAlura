# 📚 **Proyecto Literalura** 📚

## Descripción del Proyecto ✨

Este proyecto es una aplicación backend desarrollada en **Java** utilizando **Spring Boot**, que consulta una API externa para obtener información sobre libros. La aplicación utiliza **JPA** para la persistencia de datos en una base de datos **PostgreSQL** y ofrece una serie de funcionalidades que permiten interactuar con los datos de los libros y los autores de manera eficiente.

## Objetivos del Proyecto 🛠️

- Consultar información externa sobre libros desde una API pública.
- Almacenar esta información en una base de datos **PostgreSQL** para su posterior consulta.
- Proporcionar una capa de servicio para interactuar con los datos de los libros y los autores.
- Ofrecer funcionalidades para realizar filtrados y consultas complejas sobre los autores y los libros almacenados.

---

### Funcionalidades Principales 🚀
1. **Consulta de libros**: Permite obtener información sobre libros a partir de una API externa (Gutendex).
2. **Gestión de autores**: Los autores se gestionan con sus respectivos atributos (nombre, año de nacimiento, año de muerte) y se pueden consultar por rango de fechas.
3. **Base de datos**: La información obtenida de la API se persiste en una base de datos PostgreSQL.
4. **Consultas y filtrado**: Los libros pueden ser filtrados por idioma, y se pueden ordenar por el número de descargas.

## Tecnologías Utilizadas 🛠️
- **Java 17**: Lenguaje de programación utilizado para el desarrollo.
- **Spring Boot**: Framework principal para la creación de la aplicación backend.
- **JPA (Java Persistence API)**: Para la gestión de la persistencia de datos.
- **PostgreSQL**: Base de datos utilizada para almacenar los datos persistentes.
- **Jackson**: Librería utilizada para el procesamiento de datos JSON de la API.
- **Git**: Sistema de control de versiones utilizado para el manejo del código fuente.

---

## Estructura del Proyecto 🗂️

El proyecto está organizado en varios paquetes que contienen clases y servicios que cumplen con responsabilidades específicas. A continuación, te explico la estructura básica:

### Paquete `modelos`:
- **DatosLibro**: Contiene la representación de un libro, incluyendo título, autores, idiomas, y número de descargas.
- **DatosAutor**: Representa a un autor, con nombre, año de nacimiento y de muerte.
- **Respuesta**: Clase que contiene la lista de libros obtenida de la API.

### Paquete `repository`:
- **AutorRepository**: Interfaz de repositorio para gestionar las operaciones de CRUD para los autores.
- **LibroRepository**: Interfaz de repositorio para gestionar las operaciones de CRUD para los libros.

### Paquete `servicios`:
- **ConsumoAPI**: Servicio encargado de realizar solicitudes HTTP para obtener datos de la API externa.
- **ConvierteDatos**: Servicio que se encarga de convertir los datos JSON en objetos Java utilizando Jackson.
- **IConvierteDatos**: Interfaz para el servicio de conversión de datos.

---
## Descripción del Flujo de la Aplicación 🔄

### 1. **Consumo de la API Externa (Gutendex)**
El primer paso en la aplicación es la **consulta a una API externa** (Gutendex), la cual proporciona una lista de libros con diferentes atributos como **título**, **idioma**, **número de descargas** y **autores**. Esta API es consumida por el servicio `ConsumoAPI`.

**Explicación:**
- La clase `ConsumoAPI` utiliza la clase `HttpClient` de Java para realizar solicitudes HTTP a la API externa.
- Se construye una solicitud HTTP que obtiene los datos en formato **JSON**.
- El resultado es procesado y devuelto como un **String JSON**, que se puede usar en el siguiente paso para convertirlo en objetos Java.

### 1. Código de Consulta a la API
```
package com.aluracursos.literalura.servicios;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class ConsumoAPI {
    public String obtenerDatos(String url){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String json = response.body();
        return json;
    }
}
```
**Diagrama de funcionamiento**

```
Inicio
   |
Generar Solicitud HTTP
   |
Enviar Solicitud HTTP
   |
Esperar Respuesta
   |
¿Respuesta Exitosa?
   |--------------------|
  Sí                   No
   |                     |
Obtener Respuesta    Lanzar Excepción
   |                     |
Devolver JSON          |
   |                     |
   Fin                  Fin
```
---

### 2. **Conversión de los Datos JSON**
Una vez obtenidos los datos de la API, estos deben ser **convertidos a objetos Java** para que puedan ser gestionados y almacenados en la base de datos. Para esto, se utiliza el servicio `ConvierteDatos`.

**Código Java para Conversión**

El servicio ConvierteDatos se encarga de convertir los datos JSON en objetos Java utilizando la biblioteca Jackson. A continuación, se muestra el código que realiza esta conversión:

```
package com.aluracursos.literalura.servicios;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class ConvierteDatos implements IConvierteDatos {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> T obtenerDatos(String json, Class<T> clase) {
        try {
            return objectMapper.readValue(json,clase);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
```

**Explicación**

    `Jackson:` Utilizamos Jackson, una biblioteca popular para manejar JSON en Java, para deserializar (convertir) el JSON en objetos Java.

    `Método obtenerDatos():` Este método genérico acepta un JSON en formato String y una clase de tipo T como parámetro. La clase puede ser cualquier tipo de objeto Java, como Libro, Autor, etc. Jackson usa esta clase para determinar cómo estructurar los datos del JSON dentro del objeto Java correspondiente.

    `Generics:` El uso de generics en este método hace que sea reutilizable para cualquier tipo de conversión de JSON, lo que proporciona flexibilidad y reutilización de código.
---

### 3. **Persistencia en la Base de Datos (PostgreSQL)**
Los `datos convertidos` (libros y autores) se almacenan en una base de datos **PostgreSQL**. La persistencia se maneja a través de **JPA** (Java Persistence API), utilizando los repositorios `LibroRepository` y `AutorRepository`.

**Explicación:**
- La base de datos se gestiona a través de las interfaces de repositorio `LibroRepository` y `AutorRepository`, que extienden de `JpaRepository`.
- **JPA** se encarga de las operaciones de **CRUD** (Crear, Leer, Actualizar, Eliminar).
- La información de los libros y autores se mapea a tablas en la base de datos mediante las entidades `Libro` y `Autor`, que están anotadas con `@Entity` y `@Table`.

**Entidad `Libro`**
```
package com.aluracursos.literalura.modelos;

import jakarta.persistence.*;
@Entity
@Table(name = "libro", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"titulo"})
})
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
```

**Entidad `Autor`**

```
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
```
**Repositorio y JPA:**

    Los repositorios LibroRepository y AutorRepository extienden de JpaRepository. Esto permite realizar las operaciones de CRUD (Crear, Leer, Actualizar, Eliminar) en la base de datos de forma automática, sin necesidad de escribir consultas SQL manualmente.
    JPA es una especificación de Java para el acceso a bases de datos relacionales que facilita la persistencia de los objetos Java, eliminando la necesidad de manejar SQL explícitamente en el código.

**Operaciones CRUD:**

    Crear: Se pueden guardar nuevos libros y autores en la base de datos.
    Leer: Se puede consultar información de libros y autores ya almacenados, como buscar un libro por su título o listar todos los autores.
    Actualizar: Se pueden modificar los datos existentes, como actualizar la cantidad de descargas de un libro.
    Eliminar: Los libros y autores pueden ser eliminados de la base de datos si ya no son necesarios.

**Mapeo de Entidades:**

    Las entidades Libro y Autor están anotadas con @Entity y @Table para indicar que corresponden a tablas en la base de datos. Por ejemplo, la entidad Libro se mapea a la tabla libros, y la entidad Autor se mapea a la tabla autores.
    Las relaciones entre las entidades, como la relación One-to-Many entre Autor y Libro, son manejadas automáticamente por JPA.

***Repositorio LibroRepository:***

```
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

    Optional<Libro> findByTituloIgnoreCase(String titulo);
}
```
`findByIdioma(String idioma)`
<strong>Función:</strong> Este método consulta todos los libros cuyo campo idioma coincide con el valor proporcionado en el parámetro idioma.
<strong>Consulta SQL generada:</strong> Realiza una búsqueda de todos los libros que tienen el idioma especificado, ordenando los resultados por el campo idioma.

`findAllByOrderByDescargasDesc(Pageable pageable)`
<strong>Función:</strong> Este método obtiene una lista paginada de todos los libros, ordenados por el número de descargas en orden descendente.
<strong>Paginación:</strong> Utiliza el objeto Pageable para gestionar la paginación y retornar los resultados en fragmentos controlados, lo que mejora la eficiencia en la consulta de grandes volúmenes de datos.

`findAllLibros()`
<strong>Función:</strong> Devuelve todos los libros en la base de datos, sin ningún filtro específico. La consulta se realiza sin ningún orden específico.

`findByTitulo(String titulo)`
<strong>Función:</strong> Busca un libro por su título. Si se encuentra, retorna un objeto Optional que puede contener el libro con el título solicitado, o estar vacío si no se encuentra.

***Repositorio AutorRepository:***
```
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

    Optional<Autor> findByNombreIgnoreCase(String nombre);
}
```
`findAutoresByRangoNacimientoYMuerte(int min, int max)`
Función:</strong> Este método busca autores cuyo año de nacimiento esté dentro del rango especificado por los parámetros min y max. Además, solo se incluyen autores cuya fecha de muerte sea posterior al rango de años de nacimiento o que no tengan fecha de muerte (siendo null).
<strong>Consulta SQL generada:</strong> La consulta incluye una relación de tipo LEFT JOIN para cargar los libros de cada autor en una sola consulta (evitando la carga diferida), y ordena los resultados por el año de nacimiento.

`findAllAutores()`
<strong>>Función:</strong> Recupera todos los autores de la base de datos. La palabra clave DISTINCT asegura que los resultados sean únicos, sin duplicados.

`findByNombre(String nombre)`
<strong>Función:</strong> Busca un autor por su nombre. El resultado se devuelve envuelto en un objeto Optional para manejar posibles valores nulos.
---

### 4. **Consultas y Filtrado de Datos**
La aplicación permite realizar consultas complejas sobre los datos de los **autores** y **libros** almacenados en la base de datos.

#### a. **Consultas de Autores por Rango de Fechas**
En el `AutorRepository`, se define un método `findAutoresByRangoNacimientoYMuerte`, que permite consultar los autores cuyo año de nacimiento esté dentro de un rango determinado, y cuya muerte (si existe) ocurra después del inicio de este rango.

**Explicación:**
- Se utiliza la anotación `@Query` para realizar una consulta personalizada con **JPQL** (Java Persistence Query Language).
- La consulta usa la condición `BETWEEN` para filtrar autores por su año de nacimiento y, opcionalmente, por su año de muerte.

#### b. **Consultas de Libros por Idioma**
En el `LibroRepository`, el método `findByIdioma` permite consultar todos los libros en un idioma específico.

**Explicación:**
- Este método utiliza una consulta simple que filtra los libros por el atributo `idioma`.

#### c. **Paginación y Ordenación de Libros**
La aplicación permite consultar todos los libros ordenados por el número de descargas en orden descendente mediante el método `findAllByOrderByDescargasDesc`.

**Explicación:**
- Este método utiliza **Spring Data JPA** para realizar paginación y ordenación automáticamente a través de la interfaz `Pageable`.

```
+-------------------------+              +-----------------------+
|   LibroRepository        |              |   AutorRepository      |
+-------------------------+              +-----------------------+
| - findByIdioma(String)   |              | - findByNombre(String) |
| - findAllByOrderBy...    |              | - findAutoresByRango...|
| - findAllLibros()        |              | - findAllAutores()     |
| - findByTitulo(String)   |              +-----------------------+
+-------------------------+                       |
           |                                     |
           v                                     v
+-------------------------+              +-----------------------+
|     Base de Datos       |              |     Base de Datos      |
|-------------------------|              |-----------------------|
|   Tabla: Libro          |              |   Tabla: Autor         |
|   - id                  |              |   - id                 |
|   - titulo              |              |   - nombre             |
|   - idioma              |              |   - anoDeNacimiento    |
|   - descargas           |              |   - anoDeMuerte        |
|   - ...                  |              |   - ...                |
+-------------------------+              +-----------------------+
```
***Repositorios:***

    LibroRepository y AutorRepository son las interfaces que proporcionan métodos para interactuar con las entidades Libro y Autor.
    Estos repositorios extienden de JpaRepository y permiten realizar operaciones sobre las tablas Libro y Autor de la base de datos.

***Interacción con la base de datos:***

    Los repositorios proporcionan métodos que permiten buscar, filtrar y obtener información de las tablas relacionadas con los libros y los autores.
    Las consultas personalizadas (por ejemplo, mediante la anotación @Query) se traducen en comandos SQL o JPQL que se ejecutan sobre la base de datos.

***Tablas de la base de datos:***

    Tabla: Libro contiene las columnas relevantes para los libros (como id, titulo, idioma, descargas, etc.).
    Tabla: Autor contiene las columnas para los autores, como id, nombre, anoDeNacimiento, anoDeMuerte, etc.
---

### 5. **Interacción entre Componentes**
La interacción entre los componentes sigue un flujo claro:

1. El servicio `ConsumoAPI` obtiene los datos de la API.
   El servicio ConsumoAPI es el encargado de realizar una solicitud HTTP a la API externa. Utiliza el cliente HTTP de Java (HttpClient) para enviar una solicitud y recibir la respuesta en formato JSON.

2. Los datos son procesados y convertidos en objetos Java por `ConvierteDatos`.
   Una vez obtenidos los datos JSON de la API, estos son enviados al servicio ConvierteDatos. Este servicio utiliza Jackson, una librería popular en Java para la conversión de datos, para deserializar el JSON y convertirlo en objetos Java (por ejemplo, Libro y Autor).

3. Los objetos se persisten en la base de datos a través de los repositorios `LibroRepository` y `AutorRepository`.
   Los objetos Java convertidos (libros y autores) son almacenados en la base de datos utilizando los repositorios LibroRepository y AutorRepository. Estos repositorios interactúan con las tablas correspondientes de la base de datos a través de JPA (Java Persistence API), lo que permite realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar).

4. Se pueden realizar consultas personalizadas utilizando los métodos definidos en los repositorios.
   Una vez los datos están en la base de datos, se pueden realizar consultas personalizadas. Esto es posible gracias a los métodos definidos en los repositorios, como la búsqueda por idioma, el orden de los libros por descargas, o la búsqueda de autores en un rango de fechas.


```
+--------------------+         +--------------------+         +--------------------+         +--------------------+
|   ConsumoAPI       |  --->   |   ConvierteDatos   |  --->   |   Persistencia     |  --->   |   Consultas        |
| - Obtención de     |         | - Conversión de    |         | - Repositorios     |         | - Métodos          |
|   Datos de la API  |         |   JSON a objetos   |         | - Almacenamiento   |         |   personalizados   |
+--------------------+         +--------------------+         +--------------------+         +--------------------+
```
Explicación del Diagrama:

1. ConsumoAPI:
El proceso comienza con la clase ConsumoAPI, que obtiene los datos de la API externa. Realiza una solicitud HTTP a la URL proporcionada y recibe una respuesta en formato JSON.

3. ConvierteDatos:
Los datos JSON obtenidos por la API son enviados a ConvierteDatos, donde se deserializan y convierten en objetos Java utilizando Jackson. Por ejemplo, los datos de libros y autores se convierten en instancias de las clases Libro y Autor.

4. Persistencia:
Los objetos Java convertidos son pasados a los repositorios correspondientes (LibroRepository y AutorRepository), que se encargan de almacenar los datos en las tablas correspondientes de la base de datos PostgreSQL.

5. Consultas Personalizadas:
Una vez que los datos están en la base de datos, se pueden realizar consultas personalizadas utilizando los métodos de los repositorios. Esto incluye operaciones como buscar libros por idioma, ordenar por descargas, o filtrar autores por su fecha de nacimiento.

### 6. **Descripción de la Clase Principal**

La clase Principal es la clase de control principal de la aplicación, que permite interactuar con el sistema a través de un menú de opciones en consola. Esta clase proporciona una interfaz para realizar varias operaciones relacionadas con libros y autores, interactuando con una API externa para la búsqueda de libros y almacenando la información en una base de datos. Está diseñada para funcionar con las siguientes funcionalidades:
A continuación, se presenta el código de la clase `Principal`:

***Funcionalidades:***
1. ***Menú Interactivo***

El menú principal permite al usuario elegir entre varias opciones para realizar acciones dentro de la aplicación:

a. Buscar libro por título: Permite buscar libros por título o autor mediante la API de Gutendex y almacenar los resultados en la base de datos.
b. Listar libros registrados: Muestra todos los libros registrados en la base de datos.
c. Listar autores registrados: Muestra todos los autores registrados en la base de datos.
d. Listar autores vivos en un periodo determinado: Muestra autores cuya fecha de nacimiento y muerte caen dentro de un rango de años especificado.
e. Listar libros por idiomas: Muestra los libros registrados en la base de datos filtrados por un idioma específico.
f. Top 10 libros más descargados: Muestra los 10 libros más descargados, ordenados por la cantidad de descargas.

2. Interacción con la API de Gutendex

La aplicación utiliza la API pública de Gutendex para obtener información sobre libros, como títulos, autores, descargas, y otros metadatos. El método getLibro() permite realizar una búsqueda de libros por título o autor, y los resultados son procesados y almacenados en la base de datos si no existen previamente.
3. Almacenamiento en Base de Datos

La aplicación utiliza un repositorio de libros (LibroRepository) y un repositorio de autores (AutorRepository) para gestionar los datos. Los libros y autores se almacenan en la base de datos si no existen, asegurando que la base de datos siempre tenga la información más reciente.
4. Normalización de Datos

El texto introducido por el usuario (títulos y nombres) se normaliza para asegurar que las búsquedas no sean sensibles a mayúsculas, tildes y otros caracteres especiales, utilizando el método normalizarTexto().
5. Consultas y Filtros

El usuario puede realizar diversas consultas como:

    Buscar libros por título o autor.
    Listar todos los libros o autores registrados.
    Consultar libros por idioma (de una lista predefinida de idiomas).
    Consultar el top 10 de libros más descargados.

6. Manejo de Errores

Se implementa un manejo de errores robusto que incluye validación de entradas del usuario y control de excepciones durante las interacciones con la API y la base de datos. En caso de error, se proporciona retroalimentación clara al usuario y se registra el error para su revisión posterior.
Flujo de Ejecución

El flujo de ejecución se lleva a cabo en un ciclo de menú interactivo:

    El usuario selecciona una opción del menú.
    Dependiendo de la opción elegida, se ejecuta la función correspondiente:
        Si el usuario elige buscar libros, se le pide que ingrese un título o autor. Los resultados se obtienen de la API y, si no existen, se guardan en la base de datos.
        Si elige listar libros o autores, la aplicación muestra la lista de los registros almacenados.
        Si elige listar autores vivos en un periodo, se le solicita un rango de fechas y se muestra los autores que cumplen con este criterio.
        Si elige ver los libros más descargados, se muestra una lista de los 10 libros más populares según las descargas.

### Métodos Principales ###

    muestraElMenu(): Muestra el menú principal y permite al usuario seleccionar una opción.
    getLibro(): Obtiene los libros de la API de Gutendex basados en un título o autor, y devuelve la respuesta procesada.
    guardarPrimerLibroSiNoExiste(): Guarda el primer libro obtenido de la búsqueda en la base de datos si no existe previamente.
    librosRegistrados(): Muestra una lista de todos los libros registrados en la base de datos.
    autoresRegistrados(): Muestra una lista de todos los autores registrados en la base de datos.
    autoresVivosPeriodo(): Permite consultar los autores que estuvieron vivos en un rango de años especificado por el usuario.
    librosPorIdiomas(): Permite al usuario filtrar los libros por idioma, según una lista de idiomas válidos.
    top10LibrosMasDescargados(): Muestra los 10 libros más descargados en la base de datos.

***Consideraciones Adicionales:***

    Idioma de los Libros: Los libros pueden tener un idioma asociado, que se guarda en la base de datos. Si no se especifica un idioma, se asigna el valor "Desconocido".
    Formato de Entrada: El programa espera que las entradas del usuario sean correctas, y proporciona mensajes de error en caso contrario.
    Requisitos de Dependencias: Asegúrese de tener configuradas las dependencias necesarias para la base de datos y la interacción con la API externa.

**Códico de Clase Principal**
```
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
```

---

## Instrucciones para Ejecutar el Proyecto ⚡

1. **Clonar el Repositorio**:
   Para empezar, clona el repositorio en tu máquina local usando el siguiente comando:

   ```bash
   git clone https://github.com/tu-usuario/literalura.git
   ```
2. **Configurar la Base de Datos**: Asegúrate de tener una base de datos PostgreSQL configurada. Puedes crear una nueva base de datos llamada `literalura` y actualizar las credenciales en el archivo `application.properties`:
``` 
spring.application.name=literalura
spring.datasource.url=jdbc:postgresql://${DB_HOST}/literalura
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.format-sql=true
```
3. **Configurar la Base de Datos**: Asegúrate de tener Maven instalado para resolver las dependencias. Si usas Maven, simplemente ejecuta:
```
mvn clean install
```
4. **Ejecutar la Aplicación**: Puedes ejecutar la aplicación con:
```
mvn spring-boot:run
```
5. **Acceso a la Aplicación**: La aplicación estará disponible en http://localhost:8080.

---

## Video de funcionamiento 🎥
[![Texto alternativo del video](https://img.youtu.be/c6gQZzodQNg.jpg)](https://youtu.be/c6gQZzodQNg)

 **Contribuir al Proyecto** 🤖

Si deseas contribuir al proyecto, sigue estos pasos:

1. Haz un Fork del repositorio.
2. Crea una nueva rama para trabajar en tu característica o corrección.
3. Realiza tus cambios y confirma.
4. Envía un Pull Request para revisión.

---

## Licencia 📜

Este proyecto está bajo la licencia MIT. Para más detalles, consulta el archivo `LICENSE`.