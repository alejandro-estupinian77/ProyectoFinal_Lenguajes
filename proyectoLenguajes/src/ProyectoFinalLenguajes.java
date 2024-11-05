import java.io.*;
import java.util.*;

public class ProyectoFinalLenguajes {

    public static void main(String[] args) {
        String archivo = "C:\\Users\\aleja\\OneDrive\\Documentos\\cadenasParcial.txt"; // Cambia esta ruta a la ubicación de tu archivo

        File file = new File(archivo);
        if (!file.exists()) {
            System.out.println("El archivo no existe. Verifique la ruta e inténtelo de nuevo.");
            return;
        }

        System.out.println("Simbología de la Máquina de Turing:");
        System.out.println("M = (Q, Σ, Γ, δ, q0, B, F)");
        System.out.println("Q = {q0, q1, q2, q3, q4}");
        System.out.println("S = {a, b, *, #}");
        System.out.println("q0 = Estado inicial");
        System.out.println("F = {q4} (Estado de aceptación)");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("\nProcesando la cadena: " + line);
                mostrarInformacionCadena(line);
                procesarCadena(line, true);  // Procesar de izquierda a derecha
                procesarCadena(line, false); // Procesar de derecha a izquierda
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void mostrarInformacionCadena(String cadena) {
        System.out.println("Cadena leída de izquierda a derecha: " + cadena);
        System.out.println("Cadena leída de derecha a izquierda: " + new StringBuilder(cadena).reverse());

        // Extraer alfabeto y estados
        Set<Character> alfabeto = new HashSet<>();
        for (char c : cadena.toCharArray()) {
            alfabeto.add(c);
        }
        System.out.println("Alfabeto de la Máquina de Turing: " + alfabeto);

        // Identificar caracteres pares e impares
        List<Character> pares = new ArrayList<>();
        List<Character> impares = new ArrayList<>();
        for (int i = 0; i < cadena.length(); i++) {
            if (i % 2 == 0) {
                pares.add(cadena.charAt(i));
            } else {
                impares.add(cadena.charAt(i));
            }
        }
        System.out.println("Caracteres en posiciones pares: " + pares);
        System.out.println("Caracteres en posiciones impares: " + impares);
    }

    static void procesarCadena(String cadena, boolean izquierdaADerecha) {
        Stack<Character> stackCaracteresValidos = new Stack<>();
        Stack<String> stackEstadosValidos = new Stack<>();
        Stack<Character> stackEpsilon = new Stack<>();

        String sentido = izquierdaADerecha ? "Izquierda a Derecha" : "Derecha a Izquierda";
        System.out.println("\nLectura en sentido: " + sentido);
        String estadoActual = "q0";
        boolean formatoCorrecto = true;

        TreeNode root = new TreeNode(estadoActual);

        int i = izquierdaADerecha ? 0 : cadena.length() - 1;
        int increment = izquierdaADerecha ? 1 : -1;

        while ((izquierdaADerecha ? i < cadena.length() : i >= 0)) {
            char letra = cadena.charAt(i);
            String estadoSiguiente = estadoActual;

            if (!formatoCorrecto)
                break;

            // Definir las transiciones de la máquina de Turing
            switch (estadoActual) {
                case "q0":
                    estadoSiguiente = (letra == 'a') ? "q1" : "q0";
                    break;

                case "q1":
                    estadoSiguiente = (letra == 'b') ? "q2" : "q1";
                    break;

                case "q2":
                    if (letra == 'a') {
                        estadoSiguiente = "q3";
                    } else {
                        estadoSiguiente = "ERROR";
                        formatoCorrecto = false;
                    }
                    break;

                case "q3":
                    if (letra == 'a') {
                        estadoSiguiente = "q3";
                    } else if (letra == '*') {
                        estadoSiguiente = "q4";
                    } else if (letra == 'b') {
                        estadoSiguiente = "q1";
                    } else {
                        estadoSiguiente = "ERROR";
                        formatoCorrecto = false;
                    }
                    break;

                case "q4":
                    if (letra == '#') {
                        estadoSiguiente = "q4";
                    } else if (letra == 'a') {
                        estadoSiguiente = "q3";
                    } else if (letra == 'b') {
                        estadoSiguiente = "q1";
                    } else {
                        estadoSiguiente = "ERROR";
                        formatoCorrecto = false;
                    }
                    break;
            }

            if (estadoSiguiente.equals("ERROR")) {
                crearTabla(estadoActual, letra, "ERROR");
            } else {
                crearTabla(estadoActual, letra, estadoSiguiente);
                TreeNode nodoActual = new TreeNode(estadoSiguiente, Character.toString(letra));
                root.children.add(nodoActual);

                stackCaracteresValidos.push(letra);
                stackEstadosValidos.push(estadoSiguiente);

                if (letra == '*' || letra == '#')
                    stackEpsilon.push(letra);
            }

            if (estadoSiguiente.equals("ERROR")) {
                formatoCorrecto = false;
                break;
            }

            estadoActual = estadoSiguiente;
            i += increment;
        }

        if (!formatoCorrecto) {
            root.children.add(new TreeNode("Error", "Error"));
        }

        System.out.println("\nÁrbol de Transiciones:");
        imprimirArbol(root, "", true);

        if (!formatoCorrecto) {
            System.out.println("Cadena no aceptada.");
        } else {
            System.out.println("Cadena aceptada.");
        }
    }

    static void crearTabla(String estadoActual, char letra, String estadoSiguiente) {
        System.out.println("| " + estadoActual + " | " + letra + " | " + estadoSiguiente + " |");
    }

    static void imprimirArbol(TreeNode nodo, String indent, boolean last) {
        System.out.print(indent);
        System.out.print(last ? "└── " : "├── ");
        System.out.println(nodo.state + " (" + nodo.label + ")");

        indent += last ? "    " : "│   ";
        for (int i = 0; i < nodo.children.size(); i++) {
            imprimirArbol(nodo.children.get(i), indent, i == nodo.children.size() - 1);
        }
    }
}

class TreeNode {
    String state;
    String label;
    List<TreeNode> children = new ArrayList<>();

    public TreeNode(String state, String label) {
        this.state = state;
        this.label = label;
    }

    public TreeNode(String state) {
        this.state = state;
    }
}
