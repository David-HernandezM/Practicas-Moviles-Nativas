<?php
// Conexión a la base de datos
$conn = new mysqli("localhost", "root", "", "sistemausuarios");

// Verificar la conexión
if ($conn->connect_error) {
    die("Conexión fallida: " . $conn->connect_error);
}

// Obtener los parámetros (pueden ser nulos o vacíos)
$id_usuario = isset($_GET['id_usuario']) ? $_GET['id_usuario'] : null;
$userU = isset($_GET['userU']) ? $_GET['userU'] : null;
$nombre = isset($_GET['nombre']) ? $_GET['nombre'] : null;
$apellidoP = isset($_GET['apellidoP']) ? $_GET['apellidoP'] : null;

// Comenzar la consulta SQL base
$sql = "SELECT * FROM usuario WHERE 1=1";  // La condición "WHERE 1=1" siempre es verdadera, permite agregar condiciones dinámicas más fácilmente

// Agregar condiciones dinámicas según los parámetros recibidos
if ($id_usuario !== null) {
    $sql .= " AND id_usuario = " . $conn->real_escape_string($id_usuario);
}

if ($userU !== null && $userU !== '') {
    $sql .= " AND userU LIKE '%" . $conn->real_escape_string($userU) . "%'";
}

if ($nombre !== null && $nombre !== '') {
    $sql .= " AND nombre LIKE '%" . $conn->real_escape_string($nombre) . "%'";
}

if ($apellidoP !== null && $apellidoP !== '') {
    $sql .= " AND apellidoP LIKE '%" . $conn->real_escape_string($apellidoP) . "%'";
}

// Ejecutar la consulta
$result = $conn->query($sql);

// Verificar si se obtuvieron resultados
if ($result->num_rows > 0) {
    $usuarios = array();
    while ($row = $result->fetch_assoc()) {
        $usuarios[] = $row;
    }
    // Devolver los resultados en formato JSON
    echo json_encode($usuarios);
} else {
    echo json_encode([]); // Si no se encuentran resultados, devolver un array vacío
}

// Cerrar la conexión
$conn->close();
?>
