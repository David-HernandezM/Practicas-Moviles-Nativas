<?php
//http://localhost/practica2/crud.php
//http://10.0.2.2/practica2/crud.php
// Configuración para manejar errores correctamente
error_reporting(E_ALL); // Reportar todos los errores

// Conexión a la base de datos
$servername = "localhost";
$username = "root";  // Ajusta tu usuario de base de datos
$password = "";      // Ajusta tu contraseña
$dbname = "sistemausuarios";

// Configuración para que las respuestas sean siempre JSON
header('Content-Type: application/json');  // Establecer el tipo de contenido como JSON

$conn = new mysqli($servername, $username, $password, $dbname);

// Verificar si la conexión a la base de datos falla
if ($conn->connect_error) {
    $response = ["message" => "Error de conexión: " . $conn->connect_error];
    echo json_encode($response);
    exit();  // Asegurarse de que el script termine en caso de error de conexión
}

// Verificar si el usuario tiene el rol de ADMIN
function isAdmin($userId) {
    global $conn;
    $sql = "SELECT r.nombre_rol FROM usuario_rol ur
            JOIN rol r ON ur.id_rol = r.id_rol
            WHERE ur.id_usuario = ? AND r.nombre_rol = 'ROLE_ADMIN'";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $userId);
    $stmt->execute();
    $stmt->store_result();
    return $stmt->num_rows > 0;
}

// Operaciones CRUD
$requestMethod = $_SERVER['REQUEST_METHOD'];

switch ($requestMethod) {
    case 'GET':
        handleGetRequest();
        break;
    case 'POST':
        handlePostRequest();
        break;
    case 'PUT':
        handlePutRequest();
        break;
    case 'DELETE':
        handleDeleteRequest();
        break;
    default:
        echo json_encode(["message" => "Método no soportado"]);
}

// Manejo de la solicitud GET
function handleGetRequest() {
    global $conn;
    $query = "SELECT * FROM usuario";

    // Agregar condiciones de búsqueda según los parámetros GET
    if (isset($_GET['id_usuario'])) {
        $id_usuario = intval($_GET['id_usuario']);
        $query .= " WHERE id_usuario = $id_usuario";
    }
    if (isset($_GET['userU'])) {
        $userU = $conn->real_escape_string($_GET['userU']);
        $query .= " WHERE userU LIKE '%$userU%'";
    }
    if (isset($_GET['nombre'])) {
        $nombre = $conn->real_escape_string($_GET['nombre']);
        $query .= " WHERE nombre LIKE '%$nombre%'";
    }
    if (isset($_GET['apellidoP'])) {
        $apellidoP = $conn->real_escape_string($_GET['apellidoP']);
        $query .= " WHERE apellidoP LIKE '%$apellidoP%'";
    }

    $result = $conn->query($query);
    $users = [];
    while ($row = $result->fetch_assoc()) {
        $users[] = $row;
    }

    // Enviar la respuesta como JSON
    echo json_encode($users);
}

// Manejo de la solicitud POST
function handlePostRequest() {
    global $conn;
    
    // Recuperar datos JSON enviados desde la app
    $input = json_decode(file_get_contents('php://input'), true);
    if (!$input) {
        echo json_encode(["message" => "No se recibieron datos válidos"]);
        return;
    }

    $nombre = $input['nombre'];
    $apellidoP = $input['apellidoP'];
    $apellidoM = $input['apellidoM'];
    $userU = $input['userU'];
    $passwordU = password_hash($input['passwordU'], PASSWORD_BCRYPT);  // Cifra la contraseña
    $edad = $input['edad'];
    $correo = $input['correo'];
    $rol = $input['rol']; // 1: Admin, 2: User

    // Validar si el rol es admin o user
    if ($rol != 1 && $rol != 2) {
        echo json_encode(["message" => "Rol inválido. Usa 1 para ADMIN y 2 para USER."]);
        return;
    }

    // Insertar usuario
    $sql = "INSERT INTO usuario (nombre, apellidoP, apellidoM, userU, passwordU, edad, correo) 
            VALUES (?, ?, ?, ?, ?, ?, ?)";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("sssssis", $nombre, $apellidoP, $apellidoM, $userU, $passwordU, $edad, $correo);

    if ($stmt->execute()) {
        $id_usuario = $stmt->insert_id;
        
        // Asignar el rol al usuario
        $sqlRol = "INSERT INTO usuario_rol (id_usuario, id_rol) VALUES (?, ?)";
        $stmtRol = $conn->prepare($sqlRol);
        $stmtRol->bind_param("ii", $id_usuario, $rol);
        $stmtRol->execute();

        echo json_encode(["message" => "Usuario creado con éxito", "id_usuario" => $id_usuario]);
    } else {
        echo json_encode(["message" => "Error al crear usuario"]);
    }
}

// Manejo de la solicitud PUT
function handlePutRequest() {
    global $conn;

    // Recuperar datos JSON enviados desde la app
    $input = json_decode(file_get_contents('php://input'), true);
    if (!$input) {
        echo json_encode(["message" => "No se recibieron datos válidos"]);
        return;
    }

    $id_usuario = intval($input['id_usuario']);
    $nombre = $input['nombre'];
    $apellidoP = $input['apellidoP'];
    $apellidoM = $input['apellidoM'];
    $userU = $input['userU'];
    $passwordU = isset($input['passwordU']) ? password_hash($input['passwordU'], PASSWORD_BCRYPT) : null;
    $edad = $input['edad'];
    $correo = $input['correo'];
    $rol = isset($input['rol']) ? $input['rol'] : null;

    // Verificar si es admin
    if (!isAdmin($id_usuario)) {
        echo json_encode(["message" => "No tienes permisos para realizar esta acción"]);
        return;
    }

    // Actualizar los datos del usuario
    $sql = "UPDATE usuario SET nombre=?, apellidoP=?, apellidoM=?, userU=?, passwordU=?, edad=?, correo=? WHERE id_usuario=?";
    $stmt = $conn->prepare($sql);
    if ($passwordU) {
        $stmt->bind_param("sssssssi", $nombre, $apellidoP, $apellidoM, $userU, $passwordU, $edad, $correo, $id_usuario);
    } else {
        $stmt->bind_param("ssssssi", $nombre, $apellidoP, $apellidoM, $userU, $edad, $correo, $id_usuario);
    }

    if ($stmt->execute()) {
        // Si se pasó un nuevo rol, actualizarlo
        if ($rol) {
            $sqlRol = "UPDATE usuario_rol SET id_rol=? WHERE id_usuario=?";
            $stmtRol = $conn->prepare($sqlRol);
            $stmtRol->bind_param("ii", $rol, $id_usuario);
            $stmtRol->execute();
        }
        echo json_encode(["message" => "Usuario actualizado con éxito"]);
    } else {
        echo json_encode(["message" => "Error al actualizar usuario"]);
    }
}
$conn->close();
?>

