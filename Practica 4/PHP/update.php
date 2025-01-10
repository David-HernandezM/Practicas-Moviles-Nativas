<?php
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

// Manejo de la solicitud PUT
function handlePutRequest() {
    global $conn;

    // Recuperar los datos JSON enviados desde la aplicación
    $input = json_decode(file_get_contents('php://input'), true);

    if (!$input) {
        echo json_encode(["message" => "No se recibieron datos válidos"]);
        return;
    }

    // Extraer los datos del JSON
    $id_usuario = intval($input['id_usuario']);
    $nombre = $input['nombre'];
    $apellidoP = $input['apellidoP'];
    $apellidoM = $input['apellidoM'];
    $userU = $input['userU'];
    $passwordU = isset($input['passwordU']) ? password_hash($input['passwordU'], PASSWORD_BCRYPT) : null;
    $edad = $input['edad'];
    $correo = $input['correo'];
    $genero = $input['genero']; // Agregar el género
    $rol = isset($input['rol']) ? $input['rol'] : null;

    // Actualizar los datos del usuario
    $sql = "UPDATE usuario SET nombre=?, apellidoP=?, apellidoM=?, userU=?, passwordU=?, edad=?, correo=?, genero=? WHERE id_usuario=?";
    $stmt = $conn->prepare($sql);

    if ($passwordU) {
        $stmt->bind_param("ssssssssi", $nombre, $apellidoP, $apellidoM, $userU, $passwordU, $edad, $correo, $genero, $id_usuario);
    } else {
        $stmt->bind_param("sssssssi", $nombre, $apellidoP, $apellidoM, $userU, $edad, $correo, $genero, $id_usuario);
    }

    if ($stmt->execute()) {
        // Si se pasó un nuevo rol, actualizarlo
        if ($rol) {
            $sqlRol = "UPDATE usuario_rol SET id_rol=? WHERE id_usuario=?";
            $stmtRol = $conn->prepare($sqlRol);
            $stmtRol->bind_param("ii", $rol, $id_usuario);
            $stmtRol->execute();
        }

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
            $stmtImagenExistente->bind_param("i", $id_usuario);
            $stmtImagenExistente->execute();
            $stmtImagenExistente->store_result();

            if ($stmtImagenExistente->num_rows > 0) {
                // Si el usuario ya tiene una imagen, actualizarla
                $sqlActualizarImagen = "UPDATE imagenes SET imagen = ? WHERE id_usuario = ?";
                $stmtActualizarImagen = $conn->prepare($sqlActualizarImagen);
                $null = null; // Para manejar el parámetro binario correctamente
                $stmtActualizarImagen->bind_param("bi", $null, $id_usuario);
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
                $stmtInsertarImagen->bind_param("bi", $null, $id_usuario);
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
