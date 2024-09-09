package br.com.gavriel.elementos.model;

public class Alphabet {
    private static final String[] LETTERS = {
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z"
    };

    public static String getLetter(int index) {
        if (index < 0 || index > LETTERS.length) {
            throw new IllegalArgumentException("Índice fora do intervalo: deve ser entre 0 e " + LETTERS.length);
        }
        return LETTERS[index];
    }
    public static String getUpperLetter(int index) {
        if (index < 0 || index > LETTERS.length) {
            throw new IllegalArgumentException("Índice fora do intervalo: deve ser entre 0 e " + LETTERS.length);
        }
        return LETTERS[index].toUpperCase();
    }
}

