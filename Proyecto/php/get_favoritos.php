<?php
session_start();

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "sistemaUsuarios";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    echo json_encode(["success" => false, "message" => "Error de conexión a la base de datos"]);
    exit;
}

// Verificar que el usuario esté autenticado
if (!isset($_SESSION['id_usuario'])) {
    echo json_encode(["success" => false, "message" => "Usuario no autenticado"]);
    exit;
}

$id_usuario = $_SESSION['id_usuario'];

$sql = "SELECT id_favorito, api_url, api_respuesta, fecha_guardado FROM favoritos WHERE id_usuario = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $id_usuario);
$stmt->execute();
$result = $stmt->get_result();

$favorites = [];

// Función para obtener datos de una API con manejo de errores
function fetchApiData($url) {
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_TIMEOUT, 10);

    $response = curl_exec($ch);

    if (curl_errno($ch)) {
        $error_msg = curl_error($ch);
        error_log("cURL error for URL $url: $error_msg");
        curl_close($ch);
        return null;
    }

    curl_close($ch);
    return $response;
}

// Procesar cada favorito del usuario
while ($row = $result->fetch_assoc()) {
    $apiResponse = fetchApiData($row["api_url"]);
    $apiData = $apiResponse ? json_decode($apiResponse, true) : null;

    $castResponse = fetchApiData($row["api_url"] . "/cast");
    error_log("Cast response for URL {$row['api_url']}/cast: " . $castResponse); // Log para depuración
    $castData = $castResponse ? json_decode($castResponse, true) : null;

    $starring = "Actores no disponibles";
    if ($castData) {
        $castNames = [];
        foreach ($castData as $castMember) {
            if (isset($castMember['person']['name'])) {
                $castNames[] = $castMember['person']['name'];
            }
        }
        if (!empty($castNames)) {
            $starring = implode(", ", $castNames);
        }
    }

    if ($apiData === null) {
        $favorites[] = [
            "id" => $row["id_favorito"],
            "name" => "Datos no disponibles",
            "description" => "No se pudo obtener información de la API",
            "starring" => $starring,
            "posterUrl" => "https://via.placeholder.com/150"
        ];
        continue;
    }

    $favorites[] = [
        "id" => $row["id_favorito"],
        "name" => $apiData["name"] ?? "Nombre no disponible",
        "description" => strip_tags($apiData["summary"] ?? "Descripción no disponible"),
        "starring" => $starring,
        "posterUrl" => $apiData["image"]["medium"] ?? "https://via.placeholder.com/150"
    ];
}

// Configuración de respuesta JSON
header('Content-Type: application/json; charset=utf-8');

$jsonOutput = json_encode($favorites);
if ($jsonOutput === false) {
    echo json_encode(["success" => false, "message" => "Error al codificar datos a JSON"]);
} else {
    echo $jsonOutput;
}

$stmt->close();
$conn->close();
?>
