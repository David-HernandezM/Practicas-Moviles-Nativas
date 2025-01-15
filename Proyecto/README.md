# Proyecto de Móviles

## Descripción
Este proyecto consiste en una aplicación móvil desarrollada en Android Studio que permite a los usuarios consultar y visualizar películas desde una API externa. Además, incluye funcionalidades diferenciadas para usuarios y administradores.

## Requisitos Previos
Antes de ejecutar el proyecto, asegúrate de tener lo siguiente instalado y configurado:

1. **Android Studio** (versión recomendada: Koala o superior).
2. **XAMPP** para la gestión de la base de datos.
3. **Base de datos configurada** con la estructura adecuada (detallada en los pasos de instalación).


## Instrucciones de Instalación

### 1. Instalación de Herramientas

1. Descarga XAMPP desde [Apache Friends](https://www.apachefriends.org/es/download.html).
   - Completa el proceso de instalación haciendo clic en "Siguiente" hasta finalizar.
   - Inicia XAMPP y verifica que el panel principal funcione correctamente.

2. Instala Android Studio siguiendo la guía oficial de [Android Developers](https://developer.android.com/studio).
   - Asegúrate de sincronizar los archivos Gradle al abrir el proyecto.

### 2. Configuración de la Base de Datos

1. Dentro del repositorio se encuentra un archivo llamado `database.txt`, el cual contiene los comandos necesarios para crear y configurar la base de datos. Abre el archivo y ejecutalo en tu gestor de base de datos para configurar la estructura. El contenido del archivo `database.txt` es el siguiente:

```sql
CREATE DATABASE sistemausuarios CHARACTER SET utf8 COLLATE utf8_general_ci;

USE sistemausuarios ;

-- Create table usuario

CREATE TABLE usuario (
	id_usuario INT (11) PRIMARY KEY AUTO_INCREMENT,
	nombre VARCHAR(64) NOT NULL,
	apellidoP VARCHAR(64) NOT NULL,
	apellidoM VARCHAR(64) NOT NULL,
	correo VARCHAR(64) NOT NULL,
	userU VARCHAR(64) NOT NULL,
	passwordU VARCHAR(128) NOT NULL,
	fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	edad INT NOT NULL,
	genero VARCHAR(64) NOT NULL
    
);

-- Creacion de la tabla de roles

CREATE TABLE  rol (
	id_rol INT(11) PRIMARY KEY AUTO_INCREMENT,
	nombre VARCHAR(64) NOT NULL UNIQUE
    
);

-- Creacion de tabla intermedia  para la relación muchos a muchos entre usuarios y roles

CREATE TABLE usuario_rol (
	id_usuario INT(11),
	id_rol INT(11),
	PRIMARY KEY (id_usuario,id_rol),
	FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
	FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
);
    
-- Insercion de datos en la tabla rol
INSERT INTO rol (nombre) VALUES ('ROLE_ADMIN'), ('ROLE_USER');

-- Crea el usuario admin con la contrasenia admin
CREATE USER 'admin'@'localhost' IDENTIFIED BY 'admin';

-- OTORGA PERMISOS al admin sobre la base de datos practica2
GRANT ALL PRIVILEGES ON sistemaUsuarios.* TO 'admin'@'localhost';

-- Aplicar cambios
FLUSH PRIVILEGES;

CREATE TABLE imagenes ( id INT AUTO_INCREMENT PRIMARY KEY, nombre VARCHAR(255), imagen BLOB, id_usuario INT, FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) );

CREATE TABLE IF NOT EXISTS search (
    id_search INT AUTO_INCREMENT PRIMARY KEY, -- ID único para la búsqueda
    search_query TEXT NOT NULL, -- Término de búsqueda
    search_date TIMESTAMP NOT NULL, -- Fecha de la búsqueda (usaremos timestamp)
    id_usuario INT, -- ID del usuario (clave externa)
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) -- Relación con la tabla usuario
);

CREATE TABLE favoritos (
	id_favorito INT AUTO_INCREMENT PRIMARY KEY,
	id_usuario INT NOT NULL,
	api_url VARCHAR(255) NOT NULL,
	api_respuesta TEXT NOT NULL,
	fecha_guardado DATETIME DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);
```

2. Copia los archivos PHP proporcionados en una carpeta llamada `practica2` dentro de `C:\xampp\htdocs\`.

### 3. Preparación del Proyecto

1. Descarga y abre el proyecto en Android Studio.
2. Sincroniza los archivos Gradle desde el menú "File > Sync Project with Gradle Files".
3. Configura la API externa si es necesario, asegurándote de actualizar los endpoints en el código fuente.

## Pasos para Ejecutar el Proyecto

1. Inicia los servicios de Apache y MySQL en XAMPP.
2. Verifica que la base de datos `sistemausuarios` esté correctamente configurada.
3. Abre el proyecto en Android Studio.
4. Haz clic en el ícono verde de ejecución para compilar y ejecutar la aplicación.
5. Instala la aplicación en un emulador o dispositivo Android conectado.

Ahora la aplicación estará lista para usarse, permitiendo el registro, inicio de sesión y la visualización de películas según los roles de usuario y administrador.

