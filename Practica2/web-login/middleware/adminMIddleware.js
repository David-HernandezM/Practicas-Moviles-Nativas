const userJWTActions = require('../middleware/jwtMiddleware');
const User = require('../models/userModel');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcrypt');
require('dotenv').config();

// Middleware para verificar que el usuario sea el admin
// si no, este se retornara al menu principal
const checkAdminToken = async (req, res, next) => {
    const token = req.cookies.token;

    if (token) {
        const tokenValue = await userJWTActions.verifyJWT(token);

        if (!tokenValue) {
            // No hay un token valido, se manda a la pagina de 
            // register o login.
            return res.redirect('/');
        }

        const isAdmin = await User.userIsAdmin(tokenValue.username);

        // Se checa si es admiminstrador, si no, se regresa el menu principal
        if (isAdmin) {
            next();
        } else {
            res.redirect('/');
        }
    } else {
        // No existe el token, se manda a la pagina
        // de login o register.
        res.redirect('/');
    }
}

module.exports = {
    checkAdminToken
}