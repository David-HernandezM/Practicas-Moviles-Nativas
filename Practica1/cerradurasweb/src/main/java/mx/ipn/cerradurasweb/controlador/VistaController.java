package mx.ipn.cerradurasweb.controlador;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import mx.ipn.cerradurasweb.modelo.CerraduraService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/cerradura")
public class VistaController {

    private final CerraduraService cerradurasService;

    public VistaController(CerraduraService cerradurasService){
        this.cerradurasService = cerradurasService;
    }
    

    //Manejo de vista principal para ingresar "n"
    @GetMapping("/")
    public String mostrarFormulario() {
        return "formulario"; //Cargar la vista formulario.html
    }

    //Manejo de vista con Thymeleaf para mostrar los resultados
    @GetMapping
    public String mostrarCerraduras(@RequestParam("n") int n, Model model) {
        //Obtener los resultados de la cerradura de Kleene y positiva
        Map<String, String> kleene = cerradurasService.kleeneCerradura(n);
        Map<String, String> positiva = cerradurasService.kleeneClausuraPositiva(n);
        
        //Pasar los resultados al modelo para Thymeleaf
        model.addAttribute("numero",n);
        model.addAttribute("kleene",kleene.get("Σ^*"));
        model.addAttribute("positiva", positiva.get("Σ^+"));

        //Devolver la vista Thymeleaf 'cerradura.html'
        return "cerradura";
    }
}
