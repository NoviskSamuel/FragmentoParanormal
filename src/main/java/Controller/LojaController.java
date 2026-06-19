package Controller;

import Dao.PersonagemDAO;
import Model.Item;
import Model.Personagem;
import Util.ScreenManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LojaController {

    @FXML private Label labelMoedas;
    @FXML private HBox  painelItens;
    @FXML private Label labelMensagem;

    private Personagem jogador;
    private final List<Item> itensLoja = new ArrayList<>();

    // Itens já comprados nesta sessão de loja
    private final List<String> comprados = new ArrayList<>();

    private static final List<Item> CATALOGO = List.of(
        new Item("Poção de Cura",       "POCAO",     40,  "Restaura 40 HP."),
        new Item("Poção de Energia",     "POCAO",     30,  "Restaura 30 PE."),
        new Item("Faca Afiada",          "ARMA",      15,  "Bônus de +15 dano."),
        new Item("Amuleto de Proteção",  "ARTEFATO",  20,  "+20 de vida máxima."),
        new Item("Tomo Paranormal",      "ARTEFATO",  15,  "+15 poder paranormal."),
        new Item("Espada Ritual",        "ARMA",      25,  "Arma poderosa. +25 dano."),
        new Item("Elixir Raro",          "POCAO",     60,  "Restaura 60 HP e 30 PE."),
        new Item("Cristal de Força",     "ARTEFATO",  10,  "+10 de força.")
    );

    // Preços correspondentes ao catálogo
    private static final int[] PRECOS = {50, 40, 80, 90, 85, 150, 120, 70};

    @FXML
    public void initialize() {
        jogador = ScreenManager.getInstance().getPersonagemAtivo();
        gerarItensAleatorios();
        exibirLoja();
    }

    private void gerarItensAleatorios() {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < CATALOGO.size(); i++) indices.add(i);
        Collections.shuffle(indices, new Random());
        itensLoja.clear();
        for (int i = 0; i < 3 && i < indices.size(); i++) {
            itensLoja.add(CATALOGO.get(indices.get(i)));
        }
    }

    private void exibirLoja() {
        labelMoedas.setText("💰 Moedas: " + (jogador != null ? jogador.getMoedas() : 0));
        painelItens.getChildren().clear();

        for (int i = 0; i < itensLoja.size(); i++) {
            Item item  = itensLoja.get(i);
            int  preco = PRECOS[CATALOGO.indexOf(item)];
            final int idx = i;

            VBox card = new VBox(8);
            card.setAlignment(javafx.geometry.Pos.CENTER);
            card.setStyle("-fx-background-color: #150003; -fx-border-color: #550011; " +
                          "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 20;");
            card.setPrefWidth(240);

            Label nome = new Label(item.getNome());
            nome.setStyle("-fx-text-fill: #ff9966; -fx-font-family: 'Georgia', serif; -fx-font-size: 15px; -fx-font-weight: bold;");

            Label tipo = new Label(item.getTipo());
            tipo.setStyle("-fx-text-fill: #884433; -fx-font-family: 'Georgia', serif; -fx-font-size: 12px;");

            Label desc = new Label(item.getDescricao());
            desc.setWrapText(true);
            desc.setStyle("-fx-text-fill: #885533; -fx-font-family: 'Georgia', serif; -fx-font-size: 12px;");

            Label precoLabel = new Label("💰 " + preco + " moedas");
            precoLabel.setStyle("-fx-text-fill: #ddaa33; -fx-font-family: 'Georgia', serif; -fx-font-size: 14px; -fx-font-weight: bold;");

            Button btnComprar = new Button(comprados.contains(item.getNome()) ? "✓ Comprado" : "COMPRAR");
            btnComprar.setDisable(comprados.contains(item.getNome()));
            btnComprar.setStyle("-fx-background-color: #330000; -fx-text-fill: #cc4422; " +
                                "-fx-border-color: #660000; -fx-border-radius: 5; -fx-background-radius: 5; " +
                                "-fx-font-family: 'Georgia', serif; -fx-font-size: 13px; " +
                                "-fx-min-width: 120px; -fx-min-height: 36px; -fx-cursor: hand;");

            btnComprar.setOnAction(e -> comprar(item, preco, btnComprar));
            card.getChildren().addAll(nome, tipo, desc, precoLabel, btnComprar);
            painelItens.getChildren().add(card);
        }
    }

    private void comprar(Item item, int preco, Button btn) {
        if (jogador == null) return;
        if (jogador.getMoedas() < preco) {
            labelMensagem.setText("⚠ Moedas insuficientes!");
            return;
        }
        jogador.setMoedas(jogador.getMoedas() - preco);
        jogador.getInventario().adicionarItem(item);
        comprados.add(item.getNome());
        btn.setText("✓ Comprado");
        btn.setDisable(true);
        labelMoedas.setText("💰 Moedas: " + jogador.getMoedas());
        labelMensagem.setText("✅ " + item.getNome() + " adicionado ao inventário!");
        try { new PersonagemDAO().atualizar(jogador); } catch (SQLException ignored) {}
    }

    @FXML
    private void onFechar() {
        ScreenManager.getInstance().voltar();
    }
}