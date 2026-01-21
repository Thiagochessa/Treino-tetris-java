package tetris;

import java.awt.Color;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Tetromino {
    private int[][] matriz;

    public static final Color[] CORES = {
            Color.CYAN, Color.RED, Color.GREEN, Color.BLUE,
            Color.MAGENTA, Color.ORANGE, Color.YELLOW
    };

    // Formatos padronizados 4x4 (SRS-like, valores 1-7)
    private static final int[][][] FORMATOS = {
            // I
            {{0,0,0,0},{1,1,1,1},{0,0,0,0},{0,0,0,0}},
            // O
            {{0,0,0,0},{0,2,2,0},{0,2,2,0},{0,0,0,0}},
            // T
            {{0,0,0,0},{0,3,0,0},{1,3,3,0},{0,0,0,0}},
            // S
            {{0,0,0,0},{0,4,4,0},{4,4,0,0},{0,0,0,0}},
            // Z
            {{0,0,0,0},{5,5,0,0},{0,5,5,0},{0,0,0,0}},
            // L
            {{0,0,0,0},{6,0,0,0},{6,6,6,0},{0,0,0,0}},
            // J
            {{0,0,0,0},{0,0,7,0},{7,7,7,0},{0,0,0,0}}
    };

    public Tetromino(int[][] matriz) {
        this.matriz = matriz;
    }

    public int[][] matriz() { return matriz; }
    public void setMatriz(int[][] nova) { matriz = nova; }

    public static Tetromino gerarAleatorio() {
        int idx = ThreadLocalRandom.current().nextInt(FORMATOS.length);
        return new Tetromino(cloneMatriz(FORMATOS[idx]));
    }

    private static int[][] cloneMatriz(int[][] origem) {
        int[][] c = new int[origem.length][origem[0].length];
        for (int i = 0; i < origem.length; i++) System.arraycopy(origem[i], 0, c[i], 0, origem[0].length);
        return c;
    }

    public int[][] rotacionar() {
        int linhas = matriz.length;
        int colunas = matriz[0].length;
        int[][] rot = new int[colunas][linhas];
        for (int r = 0; r < linhas; r++)
            for (int c = 0; c < colunas; c++)
                rot[c][linhas - 1 - r] = matriz[r][c];
        return rot;
    }
}
