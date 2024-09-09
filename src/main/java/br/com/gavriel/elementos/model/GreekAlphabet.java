package br.com.gavriel.elementos.model;

public class GreekAlphabet {
    private static final String[] GREEK_LETTERS = {
            "α", "β", "γ", "δ", "ε", "ζ", "η", "θ", "ι", "κ", "λ", "μ", "ν", "ξ", "ο", "π",
            "ρ", "σ", "τ", "υ", "φ", "χ", "ψ", "ω"
    };

    // Método que retorna a letra grega com base no índice (começando de 1)
    public static String getGreekLetter(int index) {
        if (index < 0 || index > GREEK_LETTERS.length) {
            throw new IllegalArgumentException("Índice fora do intervalo: deve ser entre 1 e " + GREEK_LETTERS.length);
        }
        return GREEK_LETTERS[index];
    }

    public static void main(String[] args) {
        System.out.println(getGreekLetter(1));  // α
        System.out.println(getGreekLetter(3));  // γ
        System.out.println(getGreekLetter(24)); // ω
    }
}

