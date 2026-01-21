package tetris;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DialogRanking extends JDialog {
    public DialogRanking(JFrame owner, List<ModeloPontuacao> ranking) {
        super(owner, "Ranking", true);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-4s %-20s %s%n", "Pos", "Nome", "Pontos"));
        sb.append("──────────────────────────────────────────────\n");

        if (ranking == null || ranking.isEmpty()) {
            sb.append("Nenhum ranking disponível ainda.%n");
        } else {
            int pos = 1;
            for (ModeloPontuacao mp : ranking) {
                sb.append(String.format("%-4d %-20s %d%n", pos++, mp.getNome(), mp.getPontos()));
            }
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setEditable(false);
        area.setBackground(getBackground());  // Fundo neutro para melhor legibilidade

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(300, 250));  // Tamanho otimizado
        add(scroll);

        // Botão OK para fechar explicitamente
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> dispose());
        add(okButton, BorderLayout.SOUTH);

        pack();  // Ajusta tamanho automaticamente
        setLocationRelativeTo(owner);
        setVisible(true);
    }
}
