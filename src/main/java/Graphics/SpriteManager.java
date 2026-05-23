package Graphics;

import javafx.animation.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpriteManager {

    private static SpriteManager instance;

    public static SpriteManager getInstance() {
        if (instance == null) instance = new SpriteManager();
        return instance;
    }

    private SpriteManager() {}

    private final Map<String, Image> cache = new ConcurrentHashMap<>();

    public Image getImagem(String path) {
        if (path == null || path.isBlank()) return null;
        return cache.computeIfAbsent(path, p -> {
            URL url = getClass().getResource(p);
            if (url == null) {
                url = getClass().getResource("/fragmentoparanormal/images/ui/placeholder.png");
            }
            return url != null ? new Image(url.toExternalForm(), true) : null;
        });
    }

    public void setSprite(ImageView iv, String path) {
        if (iv == null) return;
        Image img = getImagem(path);
        if (img != null) iv.setImage(img);
    }

    public ScaleTransition animarIdle(ImageView iv) {
        if (iv == null) return null;
        ScaleTransition st = new ScaleTransition(Duration.millis(1400), iv);
        st.setFromX(1.0); st.setToX(1.04);
        st.setFromY(1.0); st.setToY(1.04);
        st.setAutoReverse(true);
        st.setCycleCount(Animation.INDEFINITE);
        st.setInterpolator(Interpolator.EASE_BOTH);
        st.play();
        return st;
    }

    public void animarAtaque(ImageView iv, Runnable aoTerminar) {
        if (iv == null) return;
        double ox = iv.getTranslateX();
        Timeline shake = new Timeline(
            new KeyFrame(Duration.ZERO,        e -> iv.setTranslateX(ox)),
            new KeyFrame(Duration.millis(60),  e -> iv.setTranslateX(ox - 14)),
            new KeyFrame(Duration.millis(120), e -> iv.setTranslateX(ox + 14)),
            new KeyFrame(Duration.millis(180), e -> iv.setTranslateX(ox - 9)),
            new KeyFrame(Duration.millis(240), e -> iv.setTranslateX(ox + 9)),
            new KeyFrame(Duration.millis(300), e -> iv.setTranslateX(ox))
        );
        if (aoTerminar != null) shake.setOnFinished(e -> aoTerminar.run());
        shake.play();
    }

    public void animarDano(ImageView iv) {
        if (iv == null) return;
        Timeline pisca = new Timeline(
            new KeyFrame(Duration.ZERO,        e -> iv.setOpacity(1.0)),
            new KeyFrame(Duration.millis(80),  e -> iv.setOpacity(0.15)),
            new KeyFrame(Duration.millis(160), e -> iv.setOpacity(1.0)),
            new KeyFrame(Duration.millis(240), e -> iv.setOpacity(0.15)),
            new KeyFrame(Duration.millis(320), e -> iv.setOpacity(1.0))
        );
        pisca.play();
    }

    public void animarMorte(ImageView iv, Runnable aoTerminar) {
        if (iv == null) return;
        FadeTransition  ft = new FadeTransition(Duration.millis(700), iv);
        ScaleTransition st = new ScaleTransition(Duration.millis(700), iv);
        ft.setFromValue(1.0); ft.setToValue(0.0);
        st.setToX(0.4);       st.setToY(0.4);
        ft.setInterpolator(Interpolator.EASE_IN);
        st.setInterpolator(Interpolator.EASE_IN);
        if (aoTerminar != null) ft.setOnFinished(e -> aoTerminar.run());
        new ParallelTransition(iv, ft, st).play();
    }

    public void exibirJumpscare(ImageView iv, String imagemPath, Runnable aoTerminar) {
        if (iv == null) return;
        Image img = getImagem(imagemPath);
        if (img == null) {
            if (aoTerminar != null) aoTerminar.run();
            return;
        }
        iv.setImage(img);
        iv.setOpacity(0.0);
        iv.setVisible(true);
        iv.setScaleX(1.2);
        iv.setScaleY(1.2);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(90), iv);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        fadeIn.setOnFinished(e1 -> {
            ScaleTransition zoom = new ScaleTransition(Duration.millis(150), iv);
            zoom.setToX(1.0); zoom.setToY(1.0);
            zoom.setInterpolator(Interpolator.EASE_OUT);

            double ox = iv.getTranslateX();
            Timeline shake = new Timeline(
                new KeyFrame(Duration.ZERO,        ev -> iv.setTranslateX(ox)),
                new KeyFrame(Duration.millis(50),  ev -> iv.setTranslateX(ox - 22)),
                new KeyFrame(Duration.millis(100), ev -> iv.setTranslateX(ox + 22)),
                new KeyFrame(Duration.millis(150), ev -> iv.setTranslateX(ox - 16)),
                new KeyFrame(Duration.millis(200), ev -> iv.setTranslateX(ox + 16)),
                new KeyFrame(Duration.millis(250), ev -> iv.setTranslateX(ox - 10)),
                new KeyFrame(Duration.millis(300), ev -> iv.setTranslateX(ox + 10)),
                new KeyFrame(Duration.millis(350), ev -> iv.setTranslateX(ox))
            );

            zoom.setOnFinished(e2 -> shake.play());

            shake.setOnFinished(e3 -> {
                PauseTransition pause = new PauseTransition(Duration.millis(600));
                pause.setOnFinished(e4 -> {
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(350), iv);
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);
                    fadeOut.setOnFinished(e5 -> {
                        iv.setVisible(false);
                        if (aoTerminar != null) aoTerminar.run();
                    });
                    fadeOut.play();
                });
                pause.play();
            });

            zoom.play();
        });

        fadeIn.play();
    }

    public void limparCache() {
        cache.clear();
    }
}