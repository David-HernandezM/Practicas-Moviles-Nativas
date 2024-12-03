const jwt = require('jsonwebtoken');


// Funcion que verifica el estado del token JWT en la sesion del 
// usuario actual
const checkTokenJWT = (req, callback) => {
    const authHeader = req.headers.authorization;
    if (authHeader) {
        const token = authHeader.split(' ')[1];

        // Se verifica el token
        jwt.verify(token, process.env.JWT_SECRET, (err, user) => {
            if (err) {
                // El token no es valido o expiro, se reedirecciona
                // al apartado del regiostro o inicio de sesion
                callback(false);
                return;
                // return res.status(403).json({ message: 'Token no válido o ha expirado' });
            }

            req.user = user; // Guardar la información del usuario
            callback(true);
        });
        return;
    } 

    callback(false);
}

const verifyExistingJWT = async (req, res, next) => {
    const token = req.cookies.token;
    
    // const authHeader = false;//req.headers.authorization;

    if (token) {
        // const token = authHeader.split(' ')[1];
        const tokenValue = await verifyJWT(token);

        if (tokenValue) {
            // Si hay un token valido, se reedirecciona a la
            // pagina del perfil del usuario
            res.redirect('/user/profile');
        } else {
            // No hay un token valido, se manda a la pagina de 
            // register o login.
            next();
        }
    } else {
        // No existe el token, se manda a la pagina
        // de login o register.
        next();
    }
}

// Verifica el token JWT y retorna el resultado de esta, o en su caso, 
// retornara nulo en caso de error.
const verifyJWT = (token) => {
    // Promesa para recuperar el valor 
    return new Promise((resolve) => {
        // Se verifica el token
        jwt.verify(token, process.env.JWT_SECRET, (err, user) => {
            if (err) {
                // El token no es valido o expiro, se reedirecciona
                // al apartado del regiostro o inicio de sesion
                resolve(null);
                return;
                // return res.status(403).json({ message: 'Token no válido o ha expirado' });
            }

            // req.user = user; // Guardar la información del usuario
            resolve(user);
        });
    });
}

module.exports = {
    verifyExistingJWT,
    verifyJWT
};