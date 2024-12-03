const mysql = require('mysql');

// Configuración de la conexión a MySQL
const db = mysql.createConnection({
  host: '127.0.0.1',
  user: 'david',
  password: '123456', // Cambia esta línea si tienes una contraseña en MySQL
  database: 'sistemaUsuarios' // Nombre de tu base de datos en XAMPP
});

// Conectar a la base de datos
db.connect((err) => {
  if (err) {
    console.error('Error conectando a la base de datos:', err);
    throw new Error('NO se pudo conectar a la base de datos.');
  }
  console.log('Conectado a la base de datos.');
});

process.on('SIGINT', () => {
  db.end((err) => {
    if (err) {
      console.log('Error al cerrar la conexion con la base de datos!');
    } else {
      console.log('Se cerro la conexion con la base de datos');
    }

    process.exit();
  });
});

module.exports = db;