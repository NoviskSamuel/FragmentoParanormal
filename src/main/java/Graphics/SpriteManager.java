package Graphics;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

/**
 * Gerenciador de Sprites do Fragmento Paranormal.
 *
 * Responsabilidades:
 *  - Carregar imagens com cache para não reabrir arquivos
 *  - Aplicar animações de idle, ataque, dano e morte em ImageViews
 *  - Exibir jumpscare do inimigo com fade + shake
 */
public class SpriteManager {

    // ── Singleton ──────────────────────────────────────────────────────────
    private static SpriteManager instance;
    public static SpriteManager getInstance() {
        if (instance == null) instance = new SpriteManager();
        return instance;
    }
    private SpriteManager() {}

    // ── Cache de imagens ────────────────────────────────────────────────────
    private final Map<String, Image> cache = new HashMap<>();

    /**
     * Carrega imagem com cache.
     * @param path caminho dentro do classpath, ex: /images/personagens/arthur.png
     */
    public Image getImagem(String path) {
        return cache.computeIfAbsent(path, p -> {
            var url = getClass().getResource(p);
            if (url == null) {
                // Fallback para placeholder
                url = getClass().getResource("/images/placeholder.png");
            }
            return url != null ? new Image(url.toExternalForm()) : null;
        });
    }

    /**
     * Define a imagem de um ImageView pelo caminho.
     */
    public void setSprite(ImageView iv, String path) {
        Image img = getImagem(path);
        if (img != null) iv.setImage(img);
    }

    // ── Animações ─────────────────────────────────────────────────────────

    /**
     * Animação idle: leve pulso (escala 1.0 → 1.03 → 1.0) em loop.
     */
    public Timeline animarIdle(ImageView iv) {
        ScaleTransition st = new ScaleTransition(Duration.millis(1200), iv);
        st.setFromX(1.0); st.setToX(1.03);
        st.setFromY(1.0); st.setToY(1.03);
        st.setAutoReverse(true);
        st.setCycleCount(Timeline.INDEFINITE);
        st.play();
        // Retornamos um Timeline "wrapper" para poder parar de fora
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(1)));
        tl.setOnFinished(e -> st.stop());
        return tl;
    }

    /**
     * Animação de ataque: shake horizontal rápido.
     */
    public void animarAtaque(ImageView iv, Runnable aoTerminar) {
        double origX = iv.getTranslateX();
        Timeline shake = new Timeline(
            new KeyFrame(Duration.millis(0),   e -> iv.setTranslateX(origX)),
            new KeyFrame(Duration.millis(50),  e -> iv.setTranslateX(origX - 12)),
            new KeyFrame(Duration.millis(100), e -> iv.setTranslateX(origX + 12)),
            new KeyFrame(Duration.millis(150), e -> iv.setTranslateX(origX - 8)),
            new KeyFrame(Duration.millis(200), e -> iv.setTranslateX(origX + 8)),
            new KeyFrame(Duration.millis(250), e -> iv.setTranslateX(origX))
        );
        shake.setOnFinished(e -> { if (aoTerminar != null) aoTerminar.run(); });
        shake.play();
    }

    /**
     * Animação de dano: pisca em vermelho (opacity) e abana.
     */
    public void animarDano(ImageView iv) {
        Timeline pisca = new Timeline(
            new KeyFrame(Duration.millis(0),   e -> iv.setOpacity(1.0)),
            new KeyFrame(Duration.millis(80),  e -> iv.setOpacity(0.2)),
            new KeyFrame(Duration.millis(160), e -> iv.setOpacity(1.0)),
            new KeyFrame(Duration.millis(240), e -> iv.setOpacity(0.2)),
            new KeyFrame(Duration.millis(320), e -> iv.setOpacity(1.0))
        );
        pisca.play();
    }

    /**
     * Animação de morte: fade out + scale down.
     */
    public void animarMorte(ImageView iv, Runnable aoTerminar) {
        FadeTransition ft = new FadeTransition(Duration.millis(600), iv);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ScaleTransition st = new ScaleTransition(Duration.millis(600), iv);
        st.setToX(0.5);
        st.setToY(0.5);
        ft.setOnFinished(e -> { if (aoTerminar != null) aoTerminar.run(); });
        ft.play();
        st.play();
    }

    /**
     * JUMPSCARE: exibe imagem do inimigo com fade-in rápido + shake intenso.
     * Chame quando o jogador entra numa sala com inimigo.
     *
     * @param iv          ImageView sobreposto (deve estar visível, opacity 0 inicialmente)
     * @param imagemPath  caminho do jumpscare
     * @param aoTerminar  callback após o jumpscare (ex.: mostrar botões de batalha)
     */
    public void exibirJumpscare(ImageView iv, String imagemPath, Runnable aoTerminar) {
        setSprite(iv, imagemPath);
        iv.setOpacity(0);
        iv.setVisible(true);

        // Fase 1: fade in abrupto
        FadeTransition fadeIn = new FadeTransition(Duration.millis(80), iv);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        fadeIn.setOnFinished(e -> {
            // Fase 2: shake intenso
            double ox = iv.getTranslateX();
            Timeline shake = new Timeline(
                new KeyFrame(Duration.millis(0),   ev -> iv.setTranslateX(ox)),
                new KeyFrame(Duration.millis(40),  ev -> iv.setTranslateX(ox - 20)),
                new KeyFrame(Duration.millis(80),  ev -> iv.setTranslateX(ox + 20)),
                new KeyFrame(Duration.millis(120), ev -> iv.setTranslateX(ox - 15)),
                new KeyFrame(Duration.millis(160), ev -> iv.setTranslateX(ox + 15)),
                new KeyFrame(Duration.millis(200), ev -> iv.setTranslateX(ox - 10)),
                new KeyFrame(Duration.millis(240), ev -> iv.setTranslateX(ox + 10)),
                new KeyFrame(Duration.millis(280), ev -> iv.setTranslateX(ox))
            );

            shake.setOnFinished(ev2 -> {
                // Fase 3: pequeno pause depois fade out suave
                Timeline pause = new Timeline(
                    new KeyFrame(Duration.millis(500))
                );
                pause.setOnFinished(ev3 -> {
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(300), iv);
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);
                    fadeOut.setOnFinished(ev4 -> {
                        iv.setVisible(false);
                        if (aoTerminar != null) aoTerminar.run();
                    });
                    fadeOut.play();
                });
                pause.play();
            });
            shake.play();
        });

        fadeIn.play();
    }

    /** Limpa o cache (use ao trocar de fase para liberar memória). */
    public void limparCache() {
        cache.clear();
    }
}