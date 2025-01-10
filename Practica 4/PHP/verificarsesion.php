<?php
session_start(); // Iniciar la sesión
//http://localhost/practica2/verificarsesion.php
// Verificar si la sesión está activa
if (isset($_SESSION['id_usuario'])) {
    // Si la sesión está activa, devolver el id_usuario
    echo json_encode(["id_usuario" => $_SESSION['id_usuario']]);
} else {
    // Si la sesión no está activa
    echo json_encode(["error" => "No se ha iniciado sesión"]);
}
?>