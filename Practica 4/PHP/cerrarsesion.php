<?php
// Iniciar sesión
session_start();

// Verificar si existe una sesión activa
if (isset($_SESSION['id_usuario'])) {
    // Destruir la sesión
    session_unset();
    session_destroy();

    // Respuesta de éxito
    echo json_encode(["success" => true, "message" => "Sesión cerrada correctamente."]);
} else {
    // Respuesta de error
    echo json_encode(["success" => false, "message" => "No hay sesión activa."]);
}
?>
