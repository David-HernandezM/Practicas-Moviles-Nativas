<?php
if (isset($_GET['id_usuario'])) {
    $idUsuario = $_GET['id_usuario'];

    // Conexión a la base de datos
    $conn = new mysqli("localhost", "usuario", "contraseña", "sistemausuarios");

    if ($conn->connect_error) {
        die("Conexión fallida: " . $conn->connect_error);
    }

    $query = "SELECT imagen FROM imagenes WHERE id_usuario = '$idUsuario'";
    $result = $conn->query($query);

    if ($result && $row = $result->fetch_assoc()) {
        echo json_encode(['imagen' => $row['imagen']]); // Imagen en Base64
    } else {
        http_response_code(404);
        echo json_encode(['error' => 'Imagen no encontrada']);
    }

    $conn->close();
} else {
    http_response_code(400); // Bad Request
    echo json_encode(['error' => 'ID de usuario no proporcionado']);
}
?>
