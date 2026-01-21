package tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

public class PainelJogo extends JPanel {
    private static final int COLUNAS = 10, LINHAS = 20, TAM_CELULA_PX = 30, QUEDA_MS = 500;
    private final int[][] tabuleiro = new int[LINHAS][COLUNAS];
    private Tetromino pecaAtual;
    private int xPeça, yPeça, pontosRodada = 0;
    private final Consumer<Integer> cbPontuacao, cbGameOver;
    private final Timer timerQueda = new Timer(QUEDA_MS, e -> passoQueda());

    public PainelJogo(Consumer<Integer> cbPontuacao, Consumer<Integer> cbGameOver) {
        this.cbPontuacao = cbPontuacao;
        this.cbGameOver = cbGameOver;
        setPreferredSize(new Dimension(COLUNAS * TAM_CELULA_PX, LINHAS * TAM_CELULA_PX));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) { tecla(e.getKeyCode()); }
        });
        iniciarNovaPartida();
    }

    public void reiniciarJogo() {
        timerQueda.stop();
        iniciarNovaPartida();
    }

    private void iniciarNovaPartida() {
        for (int[] row : tabuleiro) java.util.Arrays.fill(row, 0);
        pontosRodada = 0;
        cbPontuacao.accept(0);
        novaPeça();
        timerQueda.start();
        requestFocusInWindow();
        repaint();
    }

    private void novaPeça() {
        pecaAtual = Tetromino.gerarAleatorio();
        xPeça = COLUNAS / 2 - 2;
        yPeça = 0;
        if (!podeMover(xPeça, yPeça, pecaAtual.matriz())) {
            timerQueda.stop();
            cbGameOver.accept(pontosRodada);
        }
    }

    private void tecla(int code) {
        switch (code) {
            case KeyEvent.VK_LEFT  -> mover(-1, 0);
            case KeyEvent.VK_RIGHT -> mover(1, 0);
            case KeyEvent.VK_DOWN  -> mover(0, 1);
            case KeyEvent.VK_UP    -> rotacionar();
            case KeyEvent.VK_SPACE -> quedaInstantanea();
        }
    }

    private void mover(int dx, int dy) {
        if (podeMover(xPeça + dx, yPeça + dy, pecaAtual.matriz())) {
            xPeça += dx;
            yPeça += dy;
            repaint();
        }
    }

    private void rotacionar() {
        int[][] rot = pecaAtual.rotacionar();
        if (podeMover(xPeça, yPeça, rot)) {
            pecaAtual.setMatriz(rot);
            repaint();
        }
    }

    private void quedaInstantanea() {
        while (podeMover(xPeça, yPeça + 1, pecaAtual.matriz())) yPeça++;
        passoQueda();
    }

    private void passoQueda() {
        if (podeMover(xPeça, yPeça + 1, pecaAtual.matriz())) {
            yPeça++;
        } else {
            travarPeça();
            int linhas = limparLinhasCompletas();
            if (linhas > 0) {
                pontosRodada += linhas;
                cbPontuacao.accept(pontosRodada);
            }
            novaPeça();
        }
        repaint();
    }

    private boolean podeMover(int nx, int ny, int[][] mat) {
        for (int r = 0; r < mat.length; r++)
            for (int c = 0; c < mat[r].length; c++)
                if (mat[r][c] != 0) {
                    int x = nx + c, y = ny + r;
                    if (x < 0 || x >= COLUNAS || y >= LINHAS || (y >= 0 && tabuleiro[y][x] != 0))
                        return false;
                }
        return true;
    }

    private void travarPeça() {
        int[][] m = pecaAtual.matriz();
        for (int r = 0; r < m.length; r++)
            for (int c = 0; c < m[r].length; c++)
                if (m[r][c] != 0)
                    tabuleiro[yPeça + r][xPeça + c] = m[r][c];
    }

    private int limparLinhasCompletas() {
        int removidas = 0;
        for (int l = 0; l < LINHAS; l++) {
            boolean cheia = true;
            for (int c = 0; c < COLUNAS; c++)
                if (tabuleiro[l][c] == 0) { cheia = false; break; }
            if (cheia) {
                removidas++;
                for (int y = l; y > 0; y--) System.arraycopy(tabuleiro[y-1], 0, tabuleiro[y], 0, COLUNAS);
                java.util.Arrays.fill(tabuleiro[0], 0);
                l--;  // Re-check linha após shift
            }
        }
        return removidas;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Tabuleiro fixo
        for (int y = 0; y < LINHAS; y++)
            for (int x = 0; x < COLUNAS; x++)
                if (tabuleiro[y][x] != 0) desenharCelula(g, x, y, tabuleiro[y][x]);
        // Peça atual + sombra
        int[][] mat = pecaAtual.matriz();
        // Sombra
        g.setColor(new Color(0, 0, 0, 80));
        int sombraY = yPeça;
        while (podeMover(xPeça, sombraY + 1, mat)) sombraY++;
        for (int r = 0; r < mat.length; r++)
            for (int c = 0; c < mat[r].length; c++)
                if (mat[r][c] != 0) g.fillRect((xPeça + c) * TAM_CELULA_PX, sombraY * TAM_CELULA_PX, TAM_CELULA_PX, TAM_CELULA_PX);
        // Peça
        for (int r = 0; r < mat.length; r++)
            for (int c = 0; c < mat[r].length; c++)
                if (mat[r][c] != 0) desenharCelula(g, xPeça + c, yPeça + r, mat[r][c]);
        // Grade
        g.setColor(new Color(60, 60, 60));
        for (int i = 0; i <= COLUNAS; i++) g.drawLine(i * TAM_CELULA_PX, 0, i * TAM_CELULA_PX, LINHAS * TAM_CELULA_PX);
        for (int i = 0; i <= LINHAS; i++) g.drawLine(0, i * TAM_CELULA_PX, COLUNAS * TAM_CELULA_PX, i * TAM_CELULA_PX);
    }

    private void desenharCelula(Graphics g, int cx, int cy, int idCor) {
        Color cor = Tetromino.CORES[(idCor - 1) % Tetromino.CORES.length];
        int px = cx * TAM_CELULA_PX, py = cy * TAM_CELULA_PX;
        g.setColor(cor);
        g.fillRect(px, py, TAM_CELULA_PX, TAM_CELULA_PX);
        g.setColor(Color.BLACK);
        g.drawRect(px, py, TAM_CELULA_PX, TAM_CELULA_PX);
    }
}
