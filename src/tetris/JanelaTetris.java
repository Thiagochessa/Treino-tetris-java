package tetris;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JanelaTetris extends JFrame {

    private final JLabel lblPontuacao = new JLabel();
    private final PainelJogo painelJogo;

    public JanelaTetris() {
        super("Tetris – Pontuação em MongoDB");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        // =========================
        // BARRA SUPERIOR
        // =========================
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        lblPontuacao.setFont(new Font("Arial", Font.BOLD, 18));
        lblPontuacao.setText("Pontuação da Rodada: 0");
        barra.add(lblPontuacao);
        add(barra, BorderLayout.NORTH);

        // =========================
        // PAINEL DO JOGO
        // =========================
        PainelJogo tempPainel;

        try {
            tempPainel = new PainelJogo(
                this::atualizarPontuacao,
                this::fimDeJogo
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Erro ao inicializar jogo: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
            );
            throw new RuntimeException(e);
        }

        painelJogo = tempPainel;
        add(painelJogo, BorderLayout.CENTER);

        // =========================
        // FINALIZAÇÃO
        // =========================
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        painelJogo.requestFocusInWindow();
    }

    // =========================
    // CALLBACKS
    // =========================
    private void atualizarPontuacao(int pontos) {
        lblPontuacao.setText("Pontuação da Rodada: " + pontos);
    }

    private void fimDeJogo(int pontosObtidos) {
        String nome = JOptionPane.showInputDialog(
            this,
            "Fim de Jogo!\nDigite seu nome para o ranking:",
            "Salvar Pontuação",
            JOptionPane.QUESTION_MESSAGE
        );

        if (nome == null || nome.trim().isEmpty()) {
            nome = "Jogador";
        }

        try {
            PontuacaoDAO dao = PontuacaoDAO.getInstancia();
            dao.salvarPontuacao(new ModeloPontuacao(nome, pontosObtidos));

            List<ModeloPontuacao> top10 = dao.obterTopPontuacoes(10);
            new DialogRanking(this, top10);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Erro ao salvar ranking: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
            );
        }

        int opc = JOptionPane.showConfirmDialog(
            this,
            "Deseja jogar novamente?",
            "Novo Jogo",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (opc == JOptionPane.YES_OPTION) {
            painelJogo.reiniciarJogo();
        } else {
            System.exit(0);
        }
    }

    // =========================
    // MAIN
    // =========================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(JanelaTetris::new);
    }
}
