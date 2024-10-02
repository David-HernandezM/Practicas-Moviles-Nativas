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

    public VistaController(CerraduraService cerradurasService) {
        this.cerradurasService = cerradurasService;
    }

    // Manejo de vista principal para ingresar "n"
    @GetMapping("/")
    public String mostrarFormulario() {
        return "formulario"; // Cargar la vista formulario.html
    }

    // Manejo de vista con Thymeleaf para mostrar los resultados
    @GetMapping
    public String mostrarCerraduras(@RequestParam(name = "n", required = false) Integer n, Model model) {
        if (n == null || n <= 0 || n >= 8) {
            model.addAttribute("error", "El valor de 'n' debe estar entre 1 y 7.");
            return "formulario"; // Redirige al formulario si 'n' es inválido
        }

        Map<String, String> kleene = cerradurasService.kleeneCerradura(n);
        Map<String, String> positiva = cerradurasService.kleeneClausuraPositiva(n);

        model.addAttribute("numero", n);
        model.addAttribute("kleene", kleene.get("Σ^*"));
        model.addAttribute("positiva", positiva.get("Σ^+"));

        return "cerradura";
    }

}
