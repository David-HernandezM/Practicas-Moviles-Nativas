<?php
// Configuración de conexión a la base de datos
$servername = "localhost";
$username = "root"; // Usuario de la base de datos
$password = ""; // Contraseña de la base de datos
$dbname = "sistemausuarios"; // Nombre de la base de datos

// Crear conexión
$conn = new mysqli($servername, $username, $password, $dbname);

// Comprobar la conexión
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Habilitar el reporte de errores de PHP para depuración
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

// Obtener datos de la solicitud y verificar que existen
$nombre = isset($_POST['nombre']) ? $_POST['nombre'] : '';
$apellidoP = isset($_POST['apellidoP']) ? $_POST['apellidoP'] : '';
$apellidoM = isset($_POST['apellidoM']) ? $_POST['apellidoM'] : '';
$correo = isset($_POST['correo']) ? $_POST['correo'] : '';
$userU = isset($_POST['userU']) ? $_POST['userU'] : '';
$passwordU = isset($_POST['passwordU']) ? $_POST['passwordU'] : ''; // Usaremos este para hash
$edad = isset($_POST['edad']) ? $_POST['edad'] : 0;
$genero = isset($_POST['genero']) ? $_POST['genero'] : '';

// Encriptar la contraseña
$passwordU_hashed = password_hash($passwordU, PASSWORD_DEFAULT);

// Verificar si el usuario ya existe
$query = "SELECT COUNT(*) as count FROM usuario WHERE userU = ?";
$stmt_check = $conn->prepare($query);
$stmt_check->bind_param("s", $userU);
$stmt_check->execute();
$result = $stmt_check->get_result();
$row = $result->fetch_assoc();

if ($row['count'] > 0) {
    echo "El nombre de usuario ya existe. Por favor, elija otro.";
} else {
    // Insertar el nuevo usuario en la tabla usuarios
    $stmt = $conn->prepare("INSERT INTO usuario (nombre, apellidoP, apellidoM, correo, userU, passwordU, edad, genero) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
    $stmt->bind_param("ssssssis", $nombre, $apellidoP, $apellidoM, $correo, $userU, $passwordU_hashed, $edad, $genero);

    if ($stmt->execute()) {
        echo "Nuevo registro creado exitosamente";

        // Obtener el último ID insertado para el usuario recién creado
        $id_usuario = $conn->insert_id;

        // Insertar en la tabla usuario_rol con id_rol = 2 (ROLE_USER)
        $id_rol = 2;
        $stmt_rol = $conn->prepare("INSERT INTO usuario_rol (id_usuario, id_rol) VALUES (?, ?)");
        $stmt_rol->bind_param("ii", $id_usuario, $id_rol);

        if ($stmt_rol->execute()) {
            echo " y rol asignado exitosamente.";
        } else {
            echo " pero ocurrió un error al asignar el rol: " . $stmt_rol->error;
        }

        // Cerrar la consulta de rol
        $stmt_rol->close();
    } else {
        echo "Error: " . $stmt->error;
    }

    // Cerrar consulta de inserción
    $stmt->close();
}

// Cerrar conexión
$stmt_check->close();
$conn->close();
?>
