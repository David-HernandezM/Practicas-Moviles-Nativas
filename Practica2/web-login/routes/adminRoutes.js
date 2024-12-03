const adminController = require('../controllers/adminController.js');
const express = require('express');
const jwt = require('jsonwebtoken');
const path = require('path');
const adminMiddlewares = require('../middleware/adminMIddleware.js');

const router = express.Router();

// Ruta para servir la vista principal del administrador
router.get('/', adminMiddlewares.checkAdminToken, adminController.adminMenu);

// Ruta para obtener datos de un usuario
router.get('/userinfo', adminMiddlewares.checkAdminToken, adminController.userInformation);

// Ruta para crear un usuario nuevo
router.post('/createuser', adminMiddlewares.checkAdminToken, adminController.createUser);

// Ruta para actualizar los datos de un usuario
router.post('/updateuser', adminMiddlewares.checkAdminToken, adminController.updateUser);

// Ruta para eliminar un usuario por su id
router.post('/deleteuser', adminMiddlewares.checkAdminToken, adminController.deleteUser);

module.exports = router;