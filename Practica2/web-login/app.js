const express = require('express');
const bodyParser = require('body-parser');
const path = require('path');
const userRoutes = require('./routes/userRoutes.js');
const adminRoutes = require('./routes/adminRoutes.js');
const cookieParser = require('cookie-parser');
const middlewares = require('./middleware/jwtMiddleware.js');
require('dotenv').config();

const app = express();

app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));

// Configuración del servidor y el body-parser para leer datos del formulario
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.json());
app.use(cookieParser());

// Configuracion de la carpeta para archivos estaticos
// app.use(express.static(path.join(__dirname, 'public')));
app.use(express.static('./public'));


// Se establecen las rutas de los usuarios para el registro e inicio de sesion
app.use('/user', userRoutes);
app.use('/admin', adminRoutes)

app.get('/', middlewares.verifyExistingJWT, (_, res) => {
  res.sendFile(path.resolve(__dirname + '/views/index.html'));
});

// Inicia el servidor en el puerto 3000
app.listen(3000, () => {
  console.log('Servidor corriendo en http://localhost:3000');
});







// const path = require('path');


// // Configuración de la conexión a MySQL
// const db = mysql.createConnection({
//   host: 'localhost',
//   user: 'david',
//   password: '123456', // Cambia esta línea si tienes una contraseña en MySQL
//   database: 'sistemaUsuarios' // Nombre de tu base de datos en XAMPP
// });

// // Conectar a la base de datos
// db.connect(err => {
//   if (err) {
//     console.error('Error conectando a la base de datos:', err);
//   } else {
//     console.log('Conectado a la base de datos.');
//   }
// });

// // Ruta para servir el formulario de registro
// app.get('/register', (req, res) => {
//   res.sendFile(path.resolve(__dirname + '/../Vista/register.html')); 
// });

// // Ruta para servir el formulario de inicio de sesión
// app.get('/login', (req, res) => {
//   res.sendFile(path.resolve(__dirname + '/../Vista/login.html')); // Asegúrate de que el archivo login.html exista en la carpeta correcta
// });

// // Ruta para manejar el envío del formulario de registro
// app.post('/register', (req, res) => {
//   const { nombre, apellidoP, apellidoM, correo, userU, passwordU, edad, genero } = req.body;
//   const fecha_creacion = new Date();

//   const query = 'INSERT INTO usuario (nombre, apellidoP, apellidoM, correo, userU, passwordU, fecha_creacion, edad, genero) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)';
//   db.query(query, [nombre, apellidoP, apellidoM, correo, userU, passwordU, fecha_creacion, edad, genero], (err, result) => {
//     if (err) {
//       console.error('Error al insertar datos:', err);
//       res.status(500).send('Error al registrar usuario');
//     } else {
//       res.send('Usuario registrado con éxito');
//     }
//   });
// });

// // Ruta para manejar el inicio de sesión
// app.post('/login', (req, res) => {
//   const { username, password } = req.body;

//   const query = 'SELECT * FROM usuario WHERE userU = ? AND passwordU = ?';
//   db.query(query, [username, password], (err, result) => {
//     if (err) {
//       console.error('Error al iniciar sesión:', err);
//       res.status(500).send('Error al iniciar sesión');
//     } else if (result.length > 0) {
//       res.json({ message: 'Inicio de sesión exitoso' });
//       // Aquí podrías redirigir al usuario a otra página o enviar información adicional
//     } else {
//       res.status(401).send('Credenciales incorrectas');
//     }
//   });
// });



// // Inicia el servidor en el puerto 3000
// app.listen(3000, () => {
//   console.log('Servidor corriendo en http://localhost:3000');
// });
