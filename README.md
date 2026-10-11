<h1 align="center">Sistema de Gestión de Biblioteca Escolar</h1>
<p>
</p>

> Sistema de gestión de biblioteca escolar para el control de inventario, usuarios, préstamos y reportes.
> 
> Aplica los principios de Programación Orientada a Objetos mediante herencia, polimorfismo, interfaces, patrón DAO y Singleton para la persistencia de datos en MySQL.

## Estructura del Proyecto

    BibliotecaEscolar/
    ├── pom.xml
    ├── README.md
    ├── database/
    │   ├── PRY2203_EFT_S9_Script_crea_tablas_biblioteca.sql
    │   └── PRY2203_EFT_S9_Script_poblado_tablas_biblioteca.sql
    └── src/
        └── main/
            └── java/
                └── cl/
                    ├── app/
                    │   └── Main.java
                    ├── dao/
                    │   ├── CategoriaDAO.java
                    │   ├── DatabaseConnection.java
                    │   ├── EstudianteDAO.java
                    │   ├── LibroDAO.java
                    │   ├── PrestamoDAO.java
                    │   └── UsuarioDAO.java
                    ├── interfaces/
                    │   └── Prestable.java
                    ├── model/
                    │   ├── Categoria.java
                    │   ├── Estudiante.java
                    │   ├── Libro.java
                    │   ├── Persona.java
                    │   ├── Prestamo.java
                    │   └── Usuario.java
                    ├── services/
                    │   ├── BibliotecaService.java
                    │   └── ValidadorRut.java
                    ├── threads/
                    │   └── PrestamoHilo.java
                    ├── util/
                    │   └── RutInvalidoException.java
                    └── view/
                        ├── VentanaEstudiantes.java
                        ├── VentanaLibros.java
                        ├── VentanaLogin.java
                        ├── VentanaPrestamos.java
                        ├── VentanaPrincipal.java
                        └── VentanaReportes.java

### Modelos

| Clase | Descripción |
|---|---|
| `Persona` | Clase abstracta base que encapsula datos comunes y define método abstracto de rol. |
| `Usuario` | Extiende de Persona; representa usuarios del sistema con credenciales y rol. |
| `Estudiante` | Extiende de Persona; representa los alumnos registrados con su curso respectivo. |
| `Libro` | Representa los ejemplares del catálogo e implementa la interfaz Prestable. |
| `Categoria` | Representa las categorías temáticas de los libros. |
| `Prestamo` | Modela la transacción de préstamo, fechas asociadas y estado de entrega. |

### Interfaces

| Interfaz | Descripción |
|---|---|
| `Prestable` | Define el contrato para objetos que pueden prestarse y manipular stock disponible. |

### Acceso a Datos (DAO)

| Clase | Descripción |
|---|---|
| `DatabaseConnection` | Implementa patrón Singleton para administrar una única conexión con MySQL. |
| `UsuarioDAO` | Consulta y valida credenciales de acceso al sistema. |
| `EstudianteDAO` | Registra, consulta, actualiza y elimina estudiantes. |
| `LibroDAO` | Registra, consulta, actualiza y elimina libros del inventario. |
| `CategoriaDAO` | Consulta el listado de categorías disponibles. |
| `PrestamoDAO` | Registra préstamos, procesa devoluciones y genera consultas de reportes. |

### Servicios y Concurrencia

| Clase | Descripción |
|---|---|
| `BibliotecaService` | Coordina préstamos y devoluciones de manera sincronizada para evitar condiciones de carrera. |
| `ValidadorRut` | Servicio utilitario para validar y formatear el RUT. |
| `PrestamoHilo` | Hilo independiente que ejecuta el registro de préstamos. |
| `RutInvalidoException` | Excepción personalizada para control de RUTs con formato erróneo. |

### Interfaz Gráfica (Swing)

| Ventana | Descripción |
|---|---|
| `VentanaLogin` | Pantalla de autenticación y validación de credenciales según rol. |
| `VentanaPrincipal` | Menú de navegación principal con accesos restringidos por rol. |
| `VentanaLibros` | Gestión y catálogo de libros (CRUD para bibliotecario, consulta para estudiante). |
| `VentanaEstudiantes` | Gestión y registro de alumnos en el sistema. |
| `VentanaPrestamos` | Registro de préstamos en segundo plano y control de devoluciones. |
| `VentanaReportes` | Módulo de reportes: libros más prestados, historial por alumno y préstamos activos. |

## Instrucciones de Ejecución


> - Tener MySQL instalado y el servicio activo en el puerto 3306.
> - Ejecutar el script `database/PRY2203_EFT_S9_Script_crea_tablas_biblioteca.sql` para crear la base de datos y sus tablas.
> - Ejecutar el script `database/PRY2203_EFT_S9_Script_poblado_tablas_biblioteca.sql` para cargar los registros iniciales.
> - Abrir el proyecto en un IDE compatible con Java (recomendado: IntelliJ IDEA).
> - Ejecutar la clase `Main.java`.

## Author

Matías Rivas Gallardo

Github: [@Dageti](https://github.com/Dageti)