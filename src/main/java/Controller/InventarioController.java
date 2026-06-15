package Controller;

import Graphics.SpriteManager;
import Model.Item;
import Model.Personagem;
import Util.ScreenManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class InventarioController {

    private enum Aba { ARMAS, HABILIDADES, RITUAIS, ITENS, ARTEFATOS }

    @FXML private ListView<String> listaItens;
    @FXML private VBox   painelDetalhes;
    @FXML private Label  labelNomeItem;
    @FXML private Label  labelDescItem;

    @FXML private Button btnAbaArmas;
    @FXML private Button btnAbaHabilidades;
    @FXML private Button btnAbaRituais;
    @FXML private Button btnAbaItens;
    @FXML private Button btnAbaArtefatos;

    @FXML private ImageView imgPersonagem;
    @FXML private Label labelNomePersonagem;
    @FXML private Label labelClassePersonagem;
    @FXML private Label labelElementoPersonagem;
    @FXML private Label labelVidaPersonagem;
    @FXML private Label labelStaminaPersonagem;

    @FXML private Label labelArmaEquipada;
    @FXML private Label labelHabilidadeEquipada;
    @FXML private Label labelRitualEquipado;
    @FXML private Label labelArtefatos;

    private Personagem jogador;
    private Aba abaAtual = Aba.ARMAS;
    private List<Item> itensExibidos = new ArrayList<>();

    @FXML
    public void initialize() {
        jogador = ScreenManager.getInstance().getPersonagemAtivo();

        listaItens.getSelectionModel().selectedIndexProperty().addListener((obs, o, n) -> {
            int i = n.intValue();
            if (i >= 0 && i < itensExibidos.size()) {
                exibirDetalhes(itensExibidos.get(i));
            } else {
                limparDetalhes();
            }
        });

        if (jogador == null) {
            limparDetalhes();
            return;
        }

        atualizarPainelStatus();
        selecionarAba(Aba.ARMAS);
    }

    @FXML private void onAbaArmas()       { selecionarAba(Aba.ARMAS); }
    @FXML private void onAbaHabilidades() { selecionarAba(Aba.HABILIDADES); }
    @FXML private void onAbaRituais()     { selecionarAba(Aba.RITUAIS); }
    @FXML private void onAbaItens()       { selecionarAba(Aba.ITENS); }
    @FXML private void onAbaArtefatos()   { selecionarAba(Aba.ARTEFATOS); }

    private void selecionarAba(Aba aba) {
        this.abaAtual = aba;
        marcarAbaAtiva();
        carregarLista();
        limparDetalhes();
    }

    private void marcarAbaAtiva() {
        for (Button b : new Button[]{btnAbaArmas, btnAbaHabilidades, btnAbaRituais, btnAbaItens, btnAbaArtefatos}) {
            b.getStyleClass().remove("btn-aba-ativo");
        }
        Button ativo = switch (abaAtual) {
            case ARMAS       -> btnAbaArmas;
            case HABILIDADES -> btnAbaHabilidades;
            case RITUAIS     -> btnAbaRituais;
            case ITENS       -> btnAbaItens;
            case ARTEFATOS   -> btnAbaArtefatos;
        };
        if (!ativo.getStyleClass().contains("btn-aba-ativo")) {
            ativo.getStyleClass().add("btn-aba-ativo");
        }
    }

    private void carregarLista() {
        if (jogador == null) return;

        itensExibidos = switch (abaAtual) {
            case ARMAS       -> jogador.getInventario().getArmas();
            case HABILIDADES -> jogador.getInventario().getHabilidades();
            case RITUAIS     -> jogador.getInventario().getRituais();
            case ITENS       -> jogador.getInventario().getConsumiveis();
            case ARTEFATOS   -> jogador.getInventario().getArtefatos();
        };

        listaItens.getItems().clear();
        if (itensExibidos.isEmpty()) {
            listaItens.getItems().add(mensagemVazia());
            listaItens.setDisable(true);
        } else {
            listaItens.setDisable(false);
            for (Item item : itensExibidos) {
                listaItens.getItems().add(formatarNomeItem(item));
            }
        }
    }

    private String mensagemVazia() {
        return switch (abaAtual) {
            case ARMAS       -> "Nenhuma arma encontrada ainda.";
            case HABILIDADES -> "Nenhuma habilidade especial aprendida ainda.";
            case RITUAIS     -> "Nenhum ritual aprendido ainda.";
            case ITENS       -> "Nenhum item consumível no inventário.";
            case ARTEFATOS   -> "Nenhum artefato encontrado ainda.";
        };
    }

    private String formatarNomeItem(Item item) {
        boolean equipado = jogador.getInventario().estaEquipado(item);
        String prefixo = equipado ? "✓ " : "   ";
        String sufixo  = equipado ? "  (equipado)" : "";
        return prefixo + item.getNome() + sufixo;
    }

    private void exibirDetalhes(Item item) {
        labelNomeItem.setText(item.getNome());
        StringBuilder desc = new StringBuilder(item.getDescricao() != null ? item.getDescricao() : "");
        if (item.getValor() > 0) {
            String rotulo = switch (item.getTipo() != null ? item.getTipo().toUpperCase() : "") {
                case "ARMA"       -> "\nDano bônus: +" + item.getValor();
                case "POCAO"      -> "\nRecupera: "    + item.getValor() + " HP";
                case "RITUAL"     -> "\nPoder: "       + item.getValor();
                case "HABILIDADE" -> "\nCusto PE: "    + item.getValor();
                default           -> "\nValor: "       + item.getValor();
            };
            desc.append(rotulo);
        }
        if (jogador.getInventario().estaEquipado(item)) {
            desc.append("\n✓ Equipado");
        }
        labelDescItem.setText(desc.toString());
    }

    private void limparDetalhes() {
        labelNomeItem.setText("");
        labelDescItem.setText(itensExibidos.isEmpty() ? "" : "Selecione um item para ver detalhes.");
    }

    @FXML
    private void onEquipar() {
        int i = listaItens.getSelectionModel().getSelectedIndex();
        if (i < 0 || i >= itensExibidos.size()) return;

        Item sel = itensExibidos.get(i);
        boolean ok = jogador.getInventario().equiparItem(sel);
        if (ok) {
            labelDescItem.setText("✓ Equipado: " + sel.getNome());
            carregarLista();
            listaItens.getSelectionModel().select(i);
            atualizarPainelStatus();
        } else {
            labelDescItem.setText("Este item não pode ser equipado nesta aba.");
        }
    }

    @FXML
    private void onUsarPocao() {
        int i = listaItens.getSelectionModel().getSelectedIndex();
        if (i < 0 || i >= itensExibidos.size()) return;

        Item sel = itensExibidos.get(i);
        if (sel.isPocao()) {
            jogador.curar(sel.getValor());
            jogador.getInventario().removerItem(sel);
            carregarLista();
            limparDetalhes();
            atualizarPainelStatus();
            labelDescItem.setText("✓ Poção usada. +" + sel.getValor() + " HP!");
        } else {
            labelDescItem.setText("Este item não é consumível.");
        }
    }

    private void atualizarPainelStatus() {
        SpriteManager.getInstance().setSprite(imgPersonagem, jogador.getImagemPath());

        labelNomePersonagem.setText(jogador.getNome() + "  •  Nível " + jogador.getNivel());
        labelClassePersonagem.setText(jogador.getClasse().getNome());
        labelElementoPersonagem.setText("Elemento: " + jogador.getElemento().getNome());
        labelVidaPersonagem.setText("❤ " + jogador.getVidaAtual() + " / " + jogador.getVidaMaxima());
        labelStaminaPersonagem.setText("⚡ " + jogador.getStaminaAtual() + " / " + jogador.getStaminaMaxima());

        Item arma      = jogador.getInventario().getArmaEquipada();
        Item ritual    = jogador.getInventario().getRitualEquipado();
        Item hab       = jogador.getInventario().getHabilidadeEquipada();
        List<Item> art = jogador.getInventario().getArtefatosEquipados();

        labelArmaEquipada.setText("🗡 Arma: "         + (arma   != null ? arma.getNome()   : "Nenhuma"));
        labelRitualEquipado.setText("🜂 Ritual: "      + (ritual != null ? ritual.getNome() : "Nenhum"));
        labelHabilidadeEquipada.setText("✦ Habilidade: " + (hab  != null ? hab.getNome()   : "Nenhuma"));

        if (art.isEmpty()) {
            labelArtefatos.setText("💎 Artefatos: Nenhum");
        } else {
            StringBuilder sb = new StringBuilder("💎 Artefatos: ");
            for (int idx = 0; idx < art.size(); idx++) {
                if (idx > 0) sb.append(", ");
                sb.append(art.get(idx).getNome());
            }
            labelArtefatos.setText(sb.toString());
        }
    }

    @FXML private void onVoltar() { ScreenManager.getInstance().voltar(); }
}