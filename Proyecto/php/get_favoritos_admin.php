<?php
header('Content-Type: application/json');

// Configuración de la base de datos
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "sistemausuarios";

// Crear conexión
$conn = new mysqli($servername, $username, $password, $dbname);

// Verificar conexión
if ($conn->connect_error) {
    die(json_encode(["error" => "Connection failed: " . $conn->connect_error]));
}

// Obtener datos del POST
$search_param = $_POST['search_param'];
$search = "%$search_param%";

// Consulta para obtener favoritos
$sqlFavoritos = "
    SELECT DISTINCT 
        favoritos.id_favorito, favoritos.api_url, favoritos.api_respuesta, favoritos.fecha_guardado
    FROM favoritos
    INNER JOIN usuario ON favoritos.id_usuario = usuario.id_usuario
    WHERE usuario.nombre LIKE ? OR usuario.apellidoP LIKE ? OR usuario.id_usuario = ? OR usuario.userU LIKE ?
";

$stmtFavoritos = $conn->prepare($sqlFavoritos);
$stmtFavoritos->bind_param("ssis", $search, $search, $search_param, $search);
$stmtFavoritos->execute();
$resultFavoritos = $stmtFavoritos->get_result();

// Procesar resultados de favoritos
$favoritos = [];
while ($row = $resultFavoritos->fetch_assoc()) {
    $favoritos[] = $row;
}

// Consulta para obtener search
$sqlSearch = "
    SELECT DISTINCT 
        search.id_search, search.search_query, search.search_date
    FROM search
    INNER JOIN usuario ON search.id_usuario = usuario.id_usuario
    WHERE usuario.nombre LIKE ? OR usuario.apellidoP LIKE ? OR usuario.id_usuario = ? OR usuario.userU LIKE ?
";

$stmtSearch = $conn->prepare($sqlSearch);
$stmtSearch->bind_param("ssis", $search, $search, $search_param, $search);
$stmtSearch->execute();
$resultSearch = $stmtSearch->get_result();

// Procesar resultados de search
$searchResults = [];
while ($row = $resultSearch->fetch_assoc()) {
    $searchResults[] = $row;
}

// Combinar resultados en un solo array
$response = [
    "favoritos" => $favoritos,
    "search" => $searchResults
];

// Retornar resultados en formato JSON
echo json_encode($response);

$stmtFavoritos->close();
$stmtSearch->close();
$conn->close();
?>
