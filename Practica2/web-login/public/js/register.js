// Se espera a que el DOM se cargue completamente 
document.addEventListener('DOMContentLoaded', () => {
    console.log('AVERRRRRRRRRRRRRRRRRRRRR');
    
    const form = document.querySelector('form'); // se obtiene el componente
    // se optienen los inputs del username y password
    const usernameInput = document.getElementById('username'); 
    const passwordInput = document.getElementById('password');

    // Agregar el evento submit al form 
    form.addEventListener('submit', async (event) => {
        // se prevee el comportamiento por default
        event.preventDefault();

        // Validar campos del formulario
        const username = usernameInput.value.trim();
        const password = passwordInput.value.trim();

        // Si el nombre de usuario o la contraseña están vacíos, mostrar advertencia
        if (username === '' || password === '') {
            alert('tiene que llenar ambos campos!');
            return;
        } 
            // Si ambos campos están llenos, mostrar un mensaje o proceder con la lógica de autenticación
            // Aquí puedes usar fetch o axios para enviar una petición AJAX al servidor
            // Si quieres proceder con el envío del formulario, descomenta la siguiente línea
            // form.submit();

        // Se establece el 'json' a mandar
        const data = {
            username,
            password
        };

        try {
            const response = await fetch('/user/login', {
                method: 'POST', // El metodo http
                headers: {
                    'Content-Type': 'application/json' // Se pone el header para enviar un JSON
                },
                body: JSON.stringify(data) // Se pone el cuerpo de la 'consulta'
            });

            if (response.status == 401) {
                console.log('Usuario o contraseña incorrectos');
                alert('Usuario o contraseña incorrectos!');
                return;
            }
    
            const jsonResponse = await response.json();
            console.log(jsonResponse); 
        } catch(e) {
            console.log("Ocurrio un error!");
            console.log(e);
        }

        


        
        // .then(data => {
        //     console.log('LO QUE SE RECIVIO:');
        //     console.log(data);

        //     if (data.status == 401) {
        //         console.log("USUARIO O CONTRASEÑA INCORRECTOS");
        //         alert('Usuario o contraseña incorrectos!');
        //     }

        //     console.log('TODO PASO BIEN');
        //     console.log(data.body);
        // })
        // .catch(error => {
        //     console.log('HUBO UN ERRROOOOOR!!');
        //     console.log(error);
        // });
    });
  });