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



