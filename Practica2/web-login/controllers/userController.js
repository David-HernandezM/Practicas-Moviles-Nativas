const User = require('../models/userModel');
const jwt = require('jsonwebtoken');
const userJWTActions = require('../middleware/jwtMiddleware');
const bcrypt = require('bcrypt');
require('dotenv').config();

// Se accede a la clave secreta
const secretKey = process.env.JWT_SECRET;

const userProfile = async (req, res) => {
  const token = req.cookies.token;

  if (!token) {
    return res.redirect('/');
  }

  const result = await userJWTActions.verifyJWT(token);

  if (!result) {
    res.clearCookie('token');
    return res.redirect('/');
  }

  const { username } = result;

  const userData = await User.userDataByUsername(username);
  const editData = false;
  const error = req.query.error || null;
  const isAdmin = await User.userIsAdmin(username);

  if (!isAdmin) {
    res.render('vistausuario', { userData, editData, error });
  } else {
    res.redirect('/admin');
  }
}

const userProfileEdit = async (req, res) => {
  const token = req.cookies.token;

  if (!token) {
    return res.redirect('/');
  }

  const result = await userJWTActions.verifyJWT(token);

  if (!result) {
    res.clearCookie('token');
    return res.redirect('/');
  }

  const { username } = result;

  const userData = await User.userDataByUsername(username);
  const editData = true;
  const error = req.query.error || null;

  const isAdmin = await User.userIsAdmin(username);

  if (!isAdmin) {
    res.render('vistausuario', { userData, editData, error });
  } else {
    res.redirect('/');
  }
}

// Controlador para editar la informacion del usuario
const updateUserData = async (req, res) => {
  const token = req.cookies.token;

  if (!token) {
    return res.redirect('/');
  }

  const result = await userJWTActions.verifyJWT(token);

  if (!result) {
    res.clearCookie('token');
    return res.redirect('/');
  }

  const { username } = result;

  const userData = await User.userDataByUsername(username);

  const {
    userCorreo,
    userUsuario: temp,
  } = req.body;

  const userUsuario = temp.trim();

  const [usernameExists, mailExists] = await User.userNameOrMailExists(userUsuario, userCorreo);

  if (usernameExists && userUsuario != userData.userU) {
    return res.redirect('/user/profile/edit?error=Username%20ya%20existe');
  }

  if (mailExists && userCorreo != userData.correo) {
    return res.redirect('/user/profile/edit?error=Correo%20ya%20existe');
  }

  const isAdmin = await User.userIsAdmin(username);

  if (isAdmin) {
    res.redirect('/');
  }

  try {
    await User.updateUserData(username, req.body);

    res.clearCookie('token');

    const token = jwt.sign({ username: userUsuario }, secretKey, { expiresIn: '1h' })

    res.cookie('token', token, { httpOnly: true, maxAge: 3600000 });

    res.redirect('/user/profile');
  } catch (error) {
    console.log('Error!', error);
    res.redirect('/user/profile/');
  }
}

// Controlador para registrar un usuario
const registerUser = async (req, res) => {
  const { nombre, apellidoP, apellidoM, correo, userU, passwordU, edad, genero } = req.body;

  const [usernameExists, mailExists] = await User.userNameOrMailExists(userU, correo);

  if (usernameExists) {
    return res.redirect('/user/register?error=Username%20ya%20existe');
  }

  if (mailExists) {
    return res.redirect('/user/register?error=Correo%20ya%20existe');
  }

  const fecha_creacion = new Date();
  const saltRounds = 10;
  const password = await bcrypt.hash(passwordU, saltRounds);
  
  try {
    await User.createUser({ nombre, apellidoP, apellidoM, correo, userU, password, fecha_creacion, edad, genero });  
    const userIdFromBD = await User.userIdFromUsername(userU);
    await User.bindRolToUser(userIdFromBD, 2);
    const username = userU;

    const token = jwt.sign({ username }, secretKey, { expiresIn: '1h' })
  
    // La cookie expirara en una hora
    res.cookie('token', token, { httpOnly: true, maxAge: 3600000 });

    res.redirect('/user/profile');
  } catch (error) {
    console.error('Error al registrar el usuario:', err);
    res.status(500).send('Error al registrar el usuario');
  }
};

// Controlador para el inicio de sesión
const loginUser = async (req, res) => {
  const { username, password } = req.body;

  const usernameExists = await User.usernameExists(username);

  if (!usernameExists) {
    return res.redirect('/user/login?error=Username%20no%20existe');
  }

  let userPassword;

  try {
    userPassword = await User.passwordFromUsername(username);
  } catch (error) {
    return res.redirect('/user/login?error=Contraseña%20es%20incorrecta');
  }
  
  const match = await bcrypt.compare(password, userPassword);

  if (match) { 
    const token = jwt.sign({ username }, secretKey, { expiresIn: '2h' })
    
    res.cookie('token', token, { httpOnly: true, maxAge: 3600000 });

    const isAdmin = await User.userIsAdmin(username);

    if (!isAdmin) {
      res.redirect('/user/profile');
    } else {
      res.redirect('/admin');
    }
  } else {
    return res.redirect('/user/login?error=Contraseña%20es%20incorrecta');
  }
};

const logoutUser = async (req, res) => {
  const token = req.cookies.token;

  if (token) {
    res.clearCookie('token');
  }

  res.redirect('/');
}

module.exports = {
  userProfile,
  updateUserData,
  userProfileEdit,
  loginUser,
  registerUser,
  logoutUser
}; 