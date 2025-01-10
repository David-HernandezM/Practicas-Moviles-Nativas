<?php
header('Content-Type: application/json');

function fetchApiData($url) {
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_TIMEOUT, 10);

    $response = curl_exec($ch);
    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);

    if (curl_errno($ch) || $httpCode !== 200) {
        error_log("Error en cURL para URL $url: " . curl_error($ch) . " Código HTTP: $httpCode");
        curl_close($ch);
        return null;
    }

    curl_close($ch);
    return $response;
}

$apiUrl = "https://api.tvmaze.com/shows";
$apiResponse = fetchApiData($apiUrl);
$apiDataArray = $apiResponse ? json_decode($apiResponse, true) : null;

if ($apiDataArray === null || empty($apiDataArray)) {
    echo json_encode(["success" => false, "message" => "No se pudo obtener información de la API"]);
    exit;
}

shuffle($apiDataArray);
$randomRecommendations = array_slice($apiDataArray, 0, 10);

$response = array_map(function ($item) {
    $showId = $item["id"] ?? 0;

    $posterUrl = $item["image"]["medium"] ?? "https://via.placeholder.com/150";

    $castUrl = "https://api.tvmaze.com/shows/$showId/cast";
    $castResponse = fetchApiData($castUrl);
    $castData = $castResponse ? json_decode($castResponse, true) : [];

    $actors = [];
    foreach ($castData as $castMember) {
        if (isset($castMember["person"]["name"])) {
            $actors[] = $castMember["person"]["name"];
        }
    }

    $starring = !empty($actors) ? implode(", ", $actors) : "Actores no disponibles";

    return [
        "id" => $showId,
        "name" => $item["name"] ?? "Nombre no disponible",
        "summary" => strip_tags($item["summary"] ?? "Descripción no disponible"),
        "posterUrl" => $posterUrl,
        "starring" => $starring
    ];
}, $randomRecommendations);

echo json_encode(["success" => true, "recommendations" => $response]);
?>
