<?php
// Iniciar la sesión
session_start();

// Configuración de la base de datos
$servername = "localhost";
$username = "root"; // Cambiar si es necesario
$password = "";     // Cambiar si es necesario
$dbname = "sistemaUsuarios";

// Crear conexión
$conn = new mysqli($servername, $username, $password, $dbname);

// Verificar conexión
if ($conn->connect_error) {
    die("Conexión fallida: " . $conn->connect_error);
}

// Verificar si hay un usuario en la sesión
if (!isset($_SESSION['id_usuario'])) {
    echo json_encode(["success" => false, "message" => "Usuario no autenticado"]);
    exit;
}

// Obtener el ID del usuario desde la sesión
$id_usuario = $_SESSION['id_usuario'];

// Verificar si se recibieron datos por POST
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $api_url = $_POST['api_url'];
    $api_respuesta = $_POST['api_respuesta'];

    // Validar campos obligatorios
    if (!empty($api_url) && !empty($api_respuesta)) {
        $stmt = $conn->prepare("INSERT INTO favoritos (id_usuario, api_url, api_respuesta, fecha_guardado) VALUES (?, ?, ?, NOW())");
        $stmt->bind_param("iss", $id_usuario, $api_url, $api_respuesta);

        if ($stmt->execute()) {
            echo json_encode(["success" => true, "message" => "Favorito agregado exitosamente"]);
        } else {
            echo json_encode(["success" => false, "message" => "Error al agregar favorito: " . $stmt->error]);
        }

        $stmt->close();
    } else {
        echo json_encode(["success" => false, "message" => "Todos los campos son obligatorios"]);
    }
} else {
    echo json_encode(["success" => false, "message" => "Método no permitido"]);
}

$conn->close();
?>
