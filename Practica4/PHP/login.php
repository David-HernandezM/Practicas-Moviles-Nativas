<?php

session_start();
header('Content-Type: application/json');
// Configuración de conexión a la base de datos
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "sistemausuarios";

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

// Obtener datos de la solicitud
$userU = isset($_POST['userU']) ? $_POST['userU'] : '';
$passwordU = isset($_POST['passwordU']) ? $_POST['passwordU'] : '';

// Consultar la base de datos para obtener la contraseña hasheada y el id_usuario
$stmt = $conn->prepare("SELECT id_usuario, passwordU FROM usuario WHERE userU = ?");
$stmt->bind_param("s", $userU);
$stmt->execute();
$result = $stmt->get_result();

// Verificar si se encontró el usuario
if ($result->num_rows > 0) {
    $row = $result->fetch_assoc();
    $passwordHashed = $row['passwordU'];
    $id_usuario = $row['id_usuario'];

    // Verificar la contraseña ingresada contra la contraseña hasheada
    if (password_verify($passwordU, $passwordHashed)) {
        // Consultar el rol del usuario desde la tabla usuario_rol
        $stmtRole = $conn->prepare("SELECT id_rol FROM usuario_rol WHERE id_usuario = ?");
        $stmtRole->bind_param("i", $id_usuario);
        $stmtRole->execute();
        $resultRole = $stmtRole->get_result();

        if ($resultRole->num_rows > 0) {
            $rowRole = $resultRole->fetch_assoc();
            $id_rol = $rowRole['id_rol'];

            // Iniciar la sesión y almacenar los datos
            $_SESSION['id_usuario'] = $id_usuario;
            $_SESSION['userU'] = $userU;
            $_SESSION['id_rol'] = $id_rol;  // Guardamos el rol en la sesión

            // Determinar el tipo de usuario y devolver respuesta JSON
            if ($id_rol == 1) {
                echo json_encode(array("status" => "success", "role" => "admin"));
            } elseif ($id_rol == 2) {
                echo json_encode(array("status" => "success", "role" => "usuario"));
            } else {
                echo json_encode(array("status" => "error", "message" => "Rol no reconocido"));
            }

        } else {
            echo json_encode(array("status" => "error", "message" => "No se encontró el rol del usuario."));
        }

        $stmtRole->close();
    } else {
        echo json_encode(array("status" => "error", "message" => "Contraseña incorrecta."));
    }
} else {
    echo json_encode(array("status" => "error", "message" => "Usuario no encontrado."));
}

// Cerrar conexiones
$stmt->close();
$conn->close();
?>
