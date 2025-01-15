<?php
//http://10.0.2.2/practica2/updateonlyuser.php
//http://localhost/practica2/updateonlyuser.php

error_reporting(E_ALL);
ini_set('display_errors', '1');

// Conexión a la base de datos
$servername = "localhost";   
$username = "root";          
$password = "";              
$dbname = "sistemausuarios";       

header('Content-Type: application/json'); 

// Crear conexión con la base de datos
$conn = new mysqli($servername, $username, $password, $dbname);  

// Verificar la conexión
if ($conn->connect_error) {
    die("Conexión fallida: " . $conn->connect_error);
}

session_start(); // Iniciar la sesión

// Verificar si el usuario está autenticado
if (!isset($_SESSION['id_usuario'])) {
    echo json_encode(["message" => "No estás autenticado"]);
    exit;
}

$usuarioSesionId = $_SESSION['id_usuario']; // Obtener el id del usuario desde la sesión

// Manejo de la solicitud PUT
function handlePutRequest() {
    global $conn, $usuarioSesionId;

    // Obtener los datos JSON enviados desde la aplicación
    $raw_data = file_get_contents('php://input');
    
    // Mostrar los datos crudos para depuración
    error_log("Datos crudos recibidos: " . $raw_data);
    
    // Decodificar los datos JSON
    $input = json_decode($raw_data, true);

    // Si los datos no se recibieron o no se decodificaron correctamente, mostrar mensaje
    if (!$input) {
        echo json_encode(["message" => "No se recibieron datos válidos"]);
        return;
    }

    // Validar y extraer los campos
    $fields = [];
    $values = [];

    // Añadir dinámicamente los campos enviados
    if (isset($input['nombre'])) {
        $fields[] = "nombre=?";
        $values[] = $input['nombre'];
    }
    if (isset($input['apellidoP'])) {
        $fields[] = "apellidoP=?";
        $values[] = $input['apellidoP'];
    }
    if (isset($input['apellidoM'])) {
        $fields[] = "apellidoM=?";
        $values[] = $input['apellidoM'];
    }
    if (isset($input['userU'])) {
        $fields[] = "userU=?";
        $values[] = $input['userU'];
    }
    if (isset($input['passwordU'])) {
        $fields[] = "passwordU=?";
        $values[] = password_hash($input['passwordU'], PASSWORD_BCRYPT);
    }
    if (isset($input['edad'])) {
        $fields[] = "edad=?";
        $values[] = $input['edad'];
    }
    if (isset($input['correo'])) {
        $fields[] = "correo=?";
        $values[] = $input['correo'];
    }
    if (isset($input['genero'])) {
        $fields[] = "genero=?";
        $values[] = $input['genero'];
    }

    // Si no hay campos para actualizar, terminar
    if (empty($fields)) {
        echo json_encode(["message" => "No se enviaron campos para actualizar"]);
        return;
    }

    // Agregar el ID del usuario al final de los valores
    $values[] = $usuarioSesionId;

    // Construir la consulta dinámica
    $sql = "UPDATE usuario SET " . implode(", ", $fields) . " WHERE id_usuario=?";
    $stmt = $conn->prepare($sql);

    // Vincular los parámetros dinámicamente
    $types = str_repeat("s", count($fields)) . "i"; // Todos los campos son cadenas excepto el ID (entero)
    $stmt->bind_param($types, ...$values);

    // Ejecutar la consulta para actualizar los datos
    if ($stmt->execute()) {
        // Verificar si la imagen está presente
        if (isset($input['imagen'])) {
            $imagenBase64 = $input['imagen'];  // Imagen en formato Base64

            // Eliminar el prefijo 'data:image/jpeg;base64,' si está presente
            $imagenBase64 = preg_replace('/^data:image\/\w+;base64,/', '', $imagenBase64);

            // Decodificar la imagen de Base64
            $imagenData = base64_decode($imagenBase64);

            if ($imagenData === false) {
                echo json_encode(["message" => "Error al decodificar la imagen"]);
                return;
            }

            // Verificar si el usuario ya tiene una imagen asociada
            $sqlImagenExistente = "SELECT id FROM imagenes WHERE id_usuario = ?";
            $stmtImagenExistente = $conn->prepare($sqlImagenExistente);
            $stmtImagenExistente->bind_param("i", $usuarioSesionId);
            $stmtImagenExistente->execute();
            $stmtImagenExistente->store_result();

            if ($stmtImagenExistente->num_rows > 0) {
                // Si el usuario ya tiene una imagen, actualizarla
                $sqlActualizarImagen = "UPDATE imagenes SET imagen = ? WHERE id_usuario = ?";
                $stmtActualizarImagen = $conn->prepare($sqlActualizarImagen);
                $null = null; // Para manejar el parámetro binario correctamente
                $stmtActualizarImagen->bind_param("bi", $null, $usuarioSesionId);
                $stmtActualizarImagen->send_long_data(0, $imagenData);

                if ($stmtActualizarImagen->execute()) {
                    echo json_encode(["message" => "Usuario e imagen actualizados con éxito"]);
                } else {
                    echo json_encode(["message" => "Error al actualizar la imagen", "error" => $stmtActualizarImagen->error]);
                }
            } else {
                // Si no tiene imagen, insertar una nueva
                $sqlInsertarImagen = "INSERT INTO imagenes (imagen, id_usuario) VALUES (?, ?)";
                $stmtInsertarImagen = $conn->prepare($sqlInsertarImagen);
                $stmtInsertarImagen->bind_param("bi", $null, $usuarioSesionId);
                $stmtInsertarImagen->send_long_data(0, $imagenData);

                if ($stmtInsertarImagen->execute()) {
                    echo json_encode(["message" => "Usuario e imagen actualizados con éxito"]);
                } else {
                    echo json_encode(["message" => "Error al guardar la imagen", "error" => $stmtInsertarImagen->error]);
                }
            }
        } else {
            echo json_encode(["message" => "Usuario actualizado con éxito"]);
        }
    } else {
        echo json_encode(["message" => "Error al actualizar usuario", "error" => $stmt->error]);
    }
}

// Verificar si la solicitud es PUT y procesar
if ($_SERVER['REQUEST_METHOD'] == 'PUT') {
    handlePutRequest();
} else {
    echo json_encode(["message" => "Método no permitido"]);
}

$conn->close();
?>
