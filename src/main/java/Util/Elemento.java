package Util;

public enum Elemento {
    SANGUE("Sangue"),
    MORTE("Morte"),
    CONHECIMENTO("Conhecimento"),
    ENERGIA("Energia"),
    NEUTRO("Neutro");

    private final String nome;

    Elemento(String nome) { this.nome = nome; }

    public String getNome() { return nome; }

    public String getImagemPath() {
        return switch (this) {
            case SANGUE       -> "/fragmentoparanormal/images/elementos/sangue.png";
            case MORTE        -> "/fragmentoparanormal/images/elementos/morte.png";
            case CONHECIMENTO -> "/fragmentoparanormal/images/elementos/conhecimento.webp";
            case ENERGIA      -> "/fragmentoparanormal/images/elementos/energia.webp";
            case NEUTRO       -> null;
        };
    }

    public String getDescricao() {
        return switch (this) {
            case SANGUE       -> "Vitalidade e sacrifício. Amplifica cura e resistência física.";
            case MORTE        -> "Ceifa e decadência. Aumenta dano e enfraquece inimigos.";
            case CONHECIMENTO -> "Saber proibido. Eleva investigação e poder dos rituais.";
            case ENERGIA      -> "Força bruta paranormal. Maximiza ataques e reflexos.";
            case NEUTRO       -> "Sem afinidade elemental.";
        };
    }
}