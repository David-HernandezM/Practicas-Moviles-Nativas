<?php
header('Content-Type: application/json');
session_start();

// Configuración de conexión a la base de datos
$servername = "localhost";
$username = "admin";
$password = "admin";
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

// Verificar si el usuario está logueado
if (!isset($_SESSION['id_usuario'])) {
    echo json_encode(array("status" => "error", "message" => "Usuario no autenticado."));
    exit();
}

// Obtener los datos enviados desde la app
$id_usuario = $_SESSION['id_usuario'];
$search_query = isset($_POST['search_query']) ? $_POST['search_query'] : '';

// Obtener la fecha y hora actual en formato adecuado (Y-m-d H:i:s)
$search_date = date('Y-m-d H:i:s'); // Formato DATETIME

// Validar que el término de búsqueda no esté vacío
if (empty($search_query)) {
    echo json_encode(array("status" => "error", "message" => "Término de búsqueda vacío."));
    exit();
}

// Insertar el historial de búsqueda en la base de datos
$stmt = $conn->prepare("INSERT INTO search (search_query, search_date, id_usuario) VALUES (?, ?, ?)");
$stmt->bind_param("ssi", $search_query, $search_date, $id_usuario);

if ($stmt->execute()) {
    echo json_encode(array("status" => "success", "message" => "Búsqueda guardada exitosamente."));
} else {
    echo json_encode(array("status" => "error", "message" => "Error al guardar la búsqueda."));
}

// Cerrar conexiones
$stmt->close();
$conn->close();
?>
