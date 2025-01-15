<?php
// http://10.0.2.2/practica2/delete.php
session_start(); // Iniciar sesión

// Configuración de la base de datos
$servername = "localhost";
$username = "root"; // Cambia esto si es necesario
$password = ""; // Cambia esto si es necesario
$dbname = "sistemausuarios"; // Nombre de la base de datos

// Crear conexión
$conn = new mysqli($servername, $username, $password, $dbname);

// Verificar la conexión
if ($conn->connect_error) {
    die(json_encode(["message" => "Conexión fallida: " . $conn->connect_error]));
}

// Verificar si el usuario está logeado
if (!isset($_SESSION['id_usuario']) || !isset($_SESSION['userU']) || !isset($_SESSION['id_rol'])) {
    echo json_encode(["message" => "Acceso denegado. Debe estar logeado para realizar esta acción."]);
    exit;
}

// Verificar que el usuario logeado sea administrador
if ($_SESSION['id_rol'] != 1) { // 1 es el ID del rol administrador
    echo json_encode(["message" => "Solo los administradores pueden eliminar usuarios."]);
    exit;
}

// Obtener los datos enviados por POST
$data = json_decode(file_get_contents("php://input"));

// Verificar que se hayan enviado los parámetros necesarios
if (!isset($data->id_usuario)) {
    echo json_encode(["message" => "Debe proporcionar el parámetro 'id_usuario'."]);
    exit;
}

$idUsuario = $data->id_usuario;

// Verificar si el usuario a eliminar también es administrador
$checkTargetAdminQuery = "
    SELECT r.id_rol
    FROM usuario u
    JOIN usuario_rol ur ON u.id_usuario = ur.id_usuario
    JOIN rol r ON ur.id_rol = r.id_rol
    WHERE u.id_usuario = ? AND r.id_rol = 1";

$stmt = $conn->prepare($checkTargetAdminQuery);
$stmt->bind_param("i", $idUsuario);
$stmt->execute();
$targetResult = $stmt->get_result();

if ($targetResult->num_rows > 0) {
    // Si el usuario a eliminar es administrador, impedir la acción
    echo json_encode(["message" => "No se puede eliminar a otro administrador."]);
} else {
    // Proceder con la eliminación
    $conn->begin_transaction(); // Iniciar una transacción

    try {
        // Eliminar el registro en la tabla usuario_rol
        $deleteUsuarioRolQuery = "DELETE FROM usuario_rol WHERE id_usuario = ?";
        $stmt = $conn->prepare($deleteUsuarioRolQuery);
        $stmt->bind_param("i", $idUsuario);
        $stmt->execute();

        // Eliminar el registro en la tabla usuario
        $deleteUsuarioQuery = "DELETE FROM usuario WHERE id_usuario = ?";
        $stmt = $conn->prepare($deleteUsuarioQuery);
        $stmt->bind_param("i", $idUsuario);
        $stmt->execute();

        // Confirmar la transacción
        $conn->commit();
        echo json_encode(["message" => "Usuario con ID $idUsuario eliminado correctamente."]);
    } catch (Exception $e) {
        $conn->rollback(); // Revertir la transacción en caso de error
        echo json_encode(["message" => "Error al eliminar el usuario: " . $e->getMessage()]);
    }
}

// Cerrar la conexión
$conn->close();
?>

