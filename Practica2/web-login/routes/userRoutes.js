// import  * as userController from '../controllers/userController.js';
// import express from 'express';
// import path from 'path';
const userController = require('../controllers/userController.js');
const express = require('express');
const jwt = require('jsonwebtoken');
const path = require('path');
const middlewares = require('../middleware/jwtMiddleware.js');

const router = express.Router();

// Ruta para servir el formulario de registro
router.get('/register', middlewares.verifyExistingJWT, (req, res) => {
  const error = req.query.error || null;
  res.render('register', { error });
    // res.sendFile(path.resolve(__dirname + '/../views/register.html'));
});

// Ruta para servir el formulario de inicio de sesión
router.get('/login', middlewares.verifyExistingJWT, (req, res) => {
  const error = req.query.error || null;
  res.render('login', { error });
  // res.sendFile(path.resolve(__dirname + '/../views/login.html'));
});

// Ruta para servir el perfil del usuario
router.get('/profile', userController.userProfile);

// Ruta para obtener el perfil para editar del usuario
router.get('/profile/edit', userController.userProfileEdit);

// Ruta para guardar los cambios del usuario
router.post('/profile/save', userController.updateUserData);

router.post('/logout', userController.logoutUser);  

router.post('/register', middlewares.verifyExistingJWT, userController.registerUser);

router.post('/login',middlewares.verifyExistingJWT,  userController.loginUser);

module.exports = router;  
