<?php
// getuserdata.php
// Código para obtener los datos del usuario desde la base de datos

session_start(); // Iniciar sesión para acceder a la variable $_SESSION

// Verificar si la sesión está iniciada y obtener el id_usuario desde la sesión
if (!isset($_SESSION['id_usuario'])) {
    echo json_encode(["error" => "No se ha iniciado sesión"]);
    exit();
}

$id_usuario = $_SESSION['id_usuario']; // Obtener el id del usuario logueado

// Establecer la conexión a la base de datos
$conn = new mysqli('localhost', 'root', '', 'sistemausuarios');

// Verificar si hubo error de conexión
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Preparar la consulta para obtener los datos del usuario desde la tabla 'usuario' y la imagen desde la tabla 'imagenes'
$stmt = $conn->prepare(
    "SELECT u.id_usuario, i.imagen 
    FROM usuario u 
    LEFT JOIN imagenes i ON u.id_usuario = i.id_usuario 
    WHERE u.id_usuario = ?"
);
$stmt->bind_param("i", $id_usuario); // Usar el id_usuario de forma segura
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $row = $result->fetch_assoc();

    // Verificar si la imagen no está vacía y convertirla a base64
    if (!empty($row['imagen'])) {
        $imagenBase64 = base64_encode($row['imagen']); // Convertir la imagen a base64
        // Preparar los datos en formato JSON, incluyendo la imagen
        $response = [
            'id_usuario' => $row['id_usuario'],
            'imagen' => $imagenBase64  // Solo incluimos la imagen si existe
        ];
    } else {
        // Preparar los datos en formato JSON sin la imagen
        $response = [
            'id_usuario' => $row['id_usuario']
        ];
    }

    // Mostrar los datos en formato JSON
    echo json_encode($response);
} else {
    echo json_encode(["error" => "No se encontraron datos"]);
}

// Cerrar la conexión
$stmt->close();
$conn->close();
?>
