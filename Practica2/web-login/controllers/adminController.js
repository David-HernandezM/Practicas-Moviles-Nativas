const User = require('../models/userModel');
const jwt = require('jsonwebtoken');
const userJWTActions = require('../middleware/jwtMiddleware');
const bcrypt = require('bcrypt');
const path = require('path');
require('dotenv').config();

// Se accede a la clave secreta
const secretKey = process.env.JWT_SECRET;

const adminMenu = async (req, res) => {
    const keys = Object.keys(req.query);

    const updateUserError = req.query.updateUserError || null;
    const updateUserMessage = req.query.updateUserMessage || null;
    const deleteUserError = req.query.deleteUserError || null;
    const deleteUserMessage = req.query.deleteUserMessage || null;
    const createUserError = req.query.createUserError || null;
    const createUserMessage = req.query.createUserMessage || null;
    const showUsersInfo = false;

    res.render('vistaadmin', { 
        showUsersInfo, 
        createUserError, 
        createUserMessage,
        updateUserError,
        updateUserMessage,
        deleteUserError,
        deleteUserMessage
    });
    // res.sendFile(path.resolve(__dirname + '/../views/vistaadmin.html'));
}

const createUser = async (req, res) => {
    const { name, apellidoP, apellidoM, correo, username, password, edad, genero } = req.body;

    const [usernameExists, mailExists] = await User.userNameOrMailExists(username, correo);

    if (usernameExists) {
        return res.redirect('/admin?createUserError=Username%20ya%20existe');
    }

    if (mailExists) {
        return res.redirect('/admin?createUserError=Correo%20ya%20existe');
    }

    const fecha_creacion = new Date();
    const saltRounds = 10;
    const userPassword = await bcrypt.hash(password, saltRounds);

    try {
        await User.createUser({ nombre: name, apellidoP, apellidoM, correo, userU: username, password: userPassword, fecha_creacion, edad, genero });  
        const userIdFromBD = await User.userIdFromUsername(username);
        await User.bindRolToUser(userIdFromBD, 2);

        res.redirect('/admin?createUserMessage=Usuario%20creado');
      } catch (error) {
        console.error(error);
        res.redirect('/admin?createUserError=Error%20al%20crear%20usuario');
      }
}

const updateUser = async (req,res) => {
    const {
        userId,
        userNombre,
        userApellidoP,
        userApellidoM,
        userCorreo,
        userUsername: userUsuario,
        userEdad,
        userGenero
    } = req.body; 

    const userData = await User.userDataById(userId);

    if (Object.keys(userData).length == 0) {
        return res.redirect(`/admin?updateUserError=User%20id%20${userId}%20no%20existe`);
    }

    if (userUsuario !== '' && await User.usernameExists(userUsuario)) {
        return res.redirect(`/admin?updateUserError=Username%20${userUsuario}%20ya%20existe`);
    }

    if (userCorreo !== '' && await User.userMailExists(userCorreo)) {
        return res.redirect(`/admin?updateUserError=Correo%20${userCorreo}%20ya%20existe`);
    }

    const data = { 
        userNombre: userNombre == '' ? userData.nombre : userNombre, 
        userApellidoP: userApellidoP == '' ? userData.apellidoP : userApellidoP, 
        userApellidoM: userApellidoM == '' ? userData.apellidoM : userApellidoM, 
        userCorreo: userCorreo == '' ? userData.correo : userCorreo, 
        userUsuario: userUsuario == '' ? userData.userU : userUsuario, 
        userEdad: userEdad == '' ? userData.edad : userEdad, 
        userGenero: userGenero == '' ? userData.genero : userGenero 
    };

    try {
        await User.updateUserDataById(userId, data);
    } catch(error) {
        console.log(error);
        return res.redirect(`/admin?updateUserError=Error%20al%20actualizad%20usuario`);
    }

    res.redirect('/admin?updateUserMessage=Usuario%20actualizado');
}

const deleteUser = async (req, res) => {
    const { userId } = req.body;

    const userData = await User.userDataById(userId);
    
    if (Object.keys(userData).length == 0) {
        return res.redirect(`/admin?deleteUserError=User%20id%20${userId}%20no%20existe`);
    }

    await User.deleteUserById(userId);
    
    res.redirect(`/admin?deleteUserMessage=Usuario%20eliminado`);
}

const userInformation = async (req, res) => {
    const { userdata } = req.query;

    const updateUserError = null;
    const updateUserMessage = null;
    const createUserMessage = null;
    const createUserError = null;
    const deleteUserError = null;
    const deleteUserMessage = null;
    const showUsersInfo = true;
    const usersInfo = await User.userDataByInfo(userdata);

    res.render('vistaadmin', { 
        showUsersInfo, 
        usersInfo, 
        createUserError, 
        createUserMessage, 
        updateUserError, 
        updateUserMessage,
        deleteUserError,
        deleteUserMessage
    }); 
}

module.exports = {
    adminMenu,
    createUser,
    updateUser,
    userInformation,
    deleteUser
}