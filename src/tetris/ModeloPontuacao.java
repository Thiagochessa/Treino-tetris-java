package tetris;

import java.time.LocalDateTime;

public class ModeloPontuacao {
    private final String nome;
    private final int pontos;
    private final LocalDateTime data;

    public ModeloPontuacao(String nome, int pontos) {
        this.nome = nome;
        this.pontos = pontos;
        this.data = LocalDateTime.now();
    }

    public String getNome() {
        return nome;
    }

    public int getPontos() {
        return pontos;
    }

    public LocalDateTime getData() {
        return data;
    }

    @Override
    public String toString() {
        return String.format("%s: %d pts (%s)", nome, pontos, data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModeloPontuacao that = (ModeloPontuacao) o;
        return pontos == that.pontos && nome.equals(that.nome) && data.equals(that.data);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(nome, pontos, data);
    }
}
