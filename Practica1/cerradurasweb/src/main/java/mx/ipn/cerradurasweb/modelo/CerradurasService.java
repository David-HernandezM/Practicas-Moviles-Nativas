package mx.ipn.cerradurasweb.modelo;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CerradurasService {
    // Método para la cerradura de Kleene (Σ^*)
    public Map<String, String> kleeneCerradura(int n) {
      Map<String, String> response = new HashMap<>();
      StringBuilder kleene = new StringBuilder("{λ}, ");

      for (int i = 1; i <= n; i++) {
          kleene.append(generarBinarios(i)).append(", ");
      }

      // Eliminar la última coma y espacio
      response.put("Σ^*", kleene.substring(0, kleene.length() - 2));
      return response;
  }

  // Método para la cerradura positiva (Σ^+)
  public Map<String, String> kleeneClausuraPositiva(int n) {
      Map<String, String> response = new HashMap<>();
      StringBuilder positiva = new StringBuilder();

      for (int i = 1; i <= n; i++) {
          positiva.append(generarBinarios(i)).append(", ");
      }

      // Eliminar la última coma y espacio
      response.put("Σ^+", positiva.substring(0, positiva.length() - 2));
      return response;
  }

  // Método auxiliar para generar cadenas binarias de longitud 'n'
  private String generarBinarios(int n) {
      return java.util.stream.IntStream.range(0, (int) Math.pow(2, n))
              .mapToObj(i -> String.format("%" + n + "s", Integer.toBinaryString(i)).replace(' ', '0'))
              .collect(Collectors.joining(", "));
  }

}
