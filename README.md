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

**Imagen Sugerida:** Diagrama de flujo de cómo se realiza la consulta a la API externa.

---

### 2. **Conversión de los Datos JSON**
Una vez obtenidos los datos de la API, estos deben ser **convertidos a objetos Java** para que puedan ser gestionados y almacenados en la base de datos. Para esto, se utiliza el servicio `ConvierteDatos`.

**Explicación:**
- El servicio `ConvierteDatos` usa **Jackson** para deserializar el **JSON** recibido y convertirlo en objetos Java (por ejemplo, `Libro` y `Autor`).
- El método `obtenerDatos()` es genérico, lo que significa que puede convertir cualquier JSON en el tipo de objeto que se le pase como parámetro.

**Imagen Sugerida:** Ejemplo de cómo un JSON es convertido a un objeto Java utilizando Jackson.

---

### 3. **Persistencia en la Base de Datos (PostgreSQL)**
Los datos convertidos (libros y autores) se almacenan en una base de datos **PostgreSQL**. La persistencia se maneja a través de **JPA** (Java Persistence API), utilizando los repositorios `LibroRepository` y `AutorRepository`.

**Explicación:**
- La base de datos se gestiona a través de las interfaces de repositorio `LibroRepository` y `AutorRepository`, que extienden de `JpaRepository`.
- **JPA** se encarga de las operaciones de **CRUD** (Crear, Leer, Actualizar, Eliminar).
- La información de los libros y autores se mapea a tablas en la base de datos mediante las entidades `Libro` y `Autor`, que están anotadas con `@Entity` y `@Table`.

**Imagen Sugerida:** Diagrama de entidades y cómo se mapean a la base de datos.

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

**Imagen Sugerida:** Diagrama de cómo los repositorios interactúan con la base de datos.

---

### 5. **Interacción entre Componentes**
La interacción entre los componentes sigue un flujo claro:

1. El servicio `ConsumoAPI` obtiene los datos de la API.
2. Los datos son procesados y convertidos en objetos Java por `ConvierteDatos`.
3. Los objetos se persisten en la base de datos a través de los repositorios `LibroRepository` y `AutorRepository`.
4. Se pueden realizar consultas personalizadas utilizando los métodos definidos en los repositorios.

**Imagen Sugerida:** Diagrama de flujo del proceso completo, desde la consulta de la API hasta la persistencia de los datos en la base.

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

 **Contribuir al Proyecto** 🤖

Si deseas contribuir al proyecto, sigue estos pasos:

1. Haz un Fork del repositorio.
2. Crea una nueva rama para trabajar en tu característica o corrección.
3. Realiza tus cambios y confirma.
4. Envía un Pull Request para revisión.

---

## Licencia 📜

Este proyecto está bajo la licencia MIT. Para más detalles, consulta el archivo `LICENSE`.