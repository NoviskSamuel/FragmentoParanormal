package Controller;

import Dao.PersonagemDAO;
import Graphics.SpriteManager;
import Model.Personagem;
import Util.Classe;
import Util.Elemento;
import Util.Genero;
import Util.ScreenManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.sql.SQLException;

public class NovoJogador {

    @FXML private TextField    campNome;
    @FXML private ToggleGroup  grupoClasse;
    @FXML private RadioButton  rbEspecialista;
    @FXML private RadioButton  rbCombatente;
    @FXML private RadioButton  rbOcultista;
    @FXML private ToggleGroup  grupoGenero;
    @FXML private RadioButton  rbMasculino;
    @FXML private RadioButton  rbFeminino;
    @FXML private ComboBox<String> comboElemento;
    @FXML private ImageView    imgPreview;
    @FXML private Label        labelDescClasse;
    @FXML private Label        labelNomePersonagem;
    @FXML private Label        labelClassePreview;
    @FXML private Label        labelErro;

    @FXML
    public void initialize() {
        for (Elemento e : Elemento.values()) {
            comboElemento.getItems().add(e.getNome());
        }
        comboElemento.getSelectionModel().selectFirst();

        grupoClasse.selectedToggleProperty().addListener((obs, o, n) -> atualizarPreview());
        grupoGenero.selectedToggleProperty().addListener((obs, o, n) -> atualizarPreview());

        rbEspecialista.setSelected(true);
        rbMasculino.setSelected(true);
        atualizarPreview();
    }

    @FXML
    private void onConfirmar() {
        labelErro.setText("");
        String nome = campNome.getText().trim();

        if (nome.isBlank()) {
            labelErro.setText("⚠ Digite um nome para o agente."); return;
        }
        if (nome.length() > 30) {
            labelErro.setText("⚠ Nome muito longo (máx 30 caracteres)."); return;
        }

        Classe   classe = classeEscolhida();
        Genero   genero = generoEscolhido();
        Elemento elem   = elementoEscolhido();

        if (classe == null) { labelErro.setText("⚠ Escolha uma classe."); return; }
        if (genero == null) { labelErro.setText("⚠ Escolha um gênero."); return; }

        Personagem novo = new Personagem(nome, classe, genero, elem);
        try {
            if (new PersonagemDAO().buscarPorNome(nome) != null) {
                labelErro.setText("⚠ Já existe um agente com esse nome.");
                return;
            }
            new PersonagemDAO().salvar(novo);
            ScreenManager.getInstance().setPersonagemAtivo(novo);
            ScreenManager.getInstance().ir(ScreenManager.TELA_CONFIRMACAO);
        } catch (SQLException e) {
            labelErro.setText("Erro ao salvar: " + e.getMessage());
        }
    }

    @FXML private void onSair() { ScreenManager.getInstance().voltar(); }

    private void atualizarPreview() {
        Classe c = classeEscolhida();
        Genero g = generoEscolhido();
        if (c == null || g == null) return;

        Personagem temp = new Personagem("preview", c, g, elementoEscolhido());
        SpriteManager.getInstance().setSprite(imgPreview, temp.resolverImagem());
        labelDescClasse.setText(c.getDescricao());

        String nomePersonagem = switch (c) {
            case ESPECIALISTA -> g == Genero.MASCULINO ? "Arthur"  : "Erin Parker";
            case COMBATENTE   -> g == Genero.MASCULINO ? "Dominic" : "Carina";
            case OCULTISTA    -> g == Genero.MASCULINO ? "Dante"   : "Agatha";
        };
        labelNomePersonagem.setText(nomePersonagem);
        if (labelClassePreview != null) labelClassePreview.setText(c.getNome());
    }

    private Classe classeEscolhida() {
        if (rbEspecialista.isSelected()) return Classe.ESPECIALISTA;
        if (rbCombatente.isSelected())   return Classe.COMBATENTE;
        if (rbOcultista.isSelected())    return Classe.OCULTISTA;
        return null;
    }

    private Genero generoEscolhido() {
        if (rbMasculino.isSelected()) return Genero.MASCULINO;
        if (rbFeminino.isSelected())  return Genero.FEMININO;
        return null;
    }

    private Elemento elementoEscolhido() {
        String nome = comboElemento.getValue();
        if (nome == null) return Elemento.SANGUE;
        for (Elemento e : Elemento.values()) {
            if (e.getNome().equals(nome)) return e;
        }
        try { return Elemento.valueOf(nome.toUpperCase()); }
        catch (IllegalArgumentException ex) { return Elemento.SANGUE; }
    }
}