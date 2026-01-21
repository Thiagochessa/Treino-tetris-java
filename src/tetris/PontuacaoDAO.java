package tetris;

import com.mongodb.client.*;
import org.bson.Document;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PontuacaoDAO {
    private static volatile PontuacaoDAO instancia;
    private final MongoCollection<Document> col;

    private PontuacaoDAO() {
        try {
            MongoClient cli = MongoClients.create("mongodb://localhost:27017");
            MongoDatabase db = cli.getDatabase("tetris");
            col = db.getCollection("pontuacoes");
        } catch (Exception e) {
            throw new RuntimeException("Falha conexão MongoDB: " + e.getMessage());
        }
    }

    public static PontuacaoDAO getInstancia() {
        if (instancia == null) {
            synchronized (PontuacaoDAO.class) {
                if (instancia == null) instancia = new PontuacaoDAO();
            }
        }
        return instancia;
    }

    public void salvarPontuacao(ModeloPontuacao mp) {
        if (mp.getNome() == null || mp.getNome().trim().isEmpty() || mp.getPontos() < 0)
            throw new IllegalArgumentException("Nome e pontos válidos requeridos");
        Document d = new Document("nome", mp.getNome().trim())
                .append("pontos", mp.getPontos())
                .append("data", mp.getData().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        col.insertOne(d);
    }

    public List<ModeloPontuacao> obterTopPontuacoes(int n) {
        List<ModeloPontuacao> lista = new ArrayList<>();
        col.find().sort(new Document("pontos", -1).append("data", 1))
                .limit(Math.max(n, 0))
                .forEach(d -> lista.add(new ModeloPontuacao(
                        d.getString("nome"),
                        d.getInteger("pontos", 0)
                )));
        return lista;
    }
}
