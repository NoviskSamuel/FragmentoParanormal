package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Missao {

    // ── Classe interna: local do mapa ─────────────────────────────
    public static class LocalMapa {
        private final int    ordem;
        private final String nome;
        private final String descricao;
        private final String imagemCenario;
        private final boolean bossRoom;
        private boolean paginaEncontrada = false;

        public LocalMapa(int ordem, String nome, String descricao,
                         String imagemCenario, boolean bossRoom) {
            this.ordem         = ordem;
            this.nome          = nome;
            this.descricao     = descricao;
            this.imagemCenario = imagemCenario;
            this.bossRoom      = bossRoom;
        }

        public int     getOrdem()            { return ordem; }
        public String  getNome()             { return nome; }
        public String  getDescricao()        { return descricao; }
        public String  getImagemCenario()    { return imagemCenario; }
        public boolean isBossRoom()          { return bossRoom; }
        public boolean isPaginaEncontrada()  { return paginaEncontrada; }
        public void    setPaginaEncontrada(boolean v) { this.paginaEncontrada = v; }
    }

    // ── Campos principais ─────────────────────────────────────────
    private int     id;
    private String  titulo;
    private String  descricao;
    private String  objetivo;
    private int     totalObjetivo;
    private int     nivelMinimo;
    private int     totalSalas;
    private int     progressoAtual;
    private int     salaAtual;
    private boolean concluida;
    private int     vezesRetornou;
    private boolean fugiu;

    // ── Mapa de locais ────────────────────────────────────────────
    private List<LocalMapa> locais     = new ArrayList<>();
    private int             localAtual = 0;

    // ── Estado por sala ───────────────────────────────────────────
    private final Map<Integer, Boolean> salasComInimigoDerrotado = new HashMap<>();
    private final Map<Integer, Boolean> salasJaInvestigadas      = new HashMap<>();

    public Missao() {}

    public Missao(int id, String titulo, String descricao, String objetivo,
                  int totalObjetivo, int nivelMinimo, int totalSalas) {
        this.id             = id;
        this.titulo         = titulo;
        this.descricao      = descricao;
        this.objetivo       = objetivo;
        this.totalObjetivo  = totalObjetivo;
        this.nivelMinimo    = nivelMinimo;
        this.totalSalas     = totalSalas;
        this.progressoAtual = 0;
        this.salaAtual      = 0;
        this.concluida      = false;
        this.vezesRetornou  = 0;
        this.fugiu          = false;
        inicializarLocais();
    }

    // ── Inicializa locais das 4 missões ───────────────────────────
    private void inicializarLocais() {
        locais.clear();
        switch (id) {
            case 1 -> inicializarLocaisSangue();
            case 2 -> inicializarLocaisMorte();
            case 3 -> inicializarLocaisEnergia();
            case 4 -> inicializarLocaisConhecimento();
        }
    }

    private void inicializarLocaisSangue() {
        locais.add(new LocalMapa(0, "Entrada do Hospital",  "Portões enferrujados e silêncio sepulcral.",   "Missao1_sala1.jpg",    false));
        locais.add(new LocalMapa(1, "Sala de Cirurgia",     "Instrumentos enferrujados sobre a mesa.",      "Missao1_sala2.jpg",    false));
        locais.add(new LocalMapa(2, "Necrotério",           "Câmaras abertas, fedor de decomposição.",      "Missao1_sala3.jpg",    false));
        locais.add(new LocalMapa(3, "Câmara de Sangue",     "O coração pulsante do horror. Boss aguarda.",  "Missao1_salaFinal.jpg",true));
    }

    private void inicializarLocaisMorte() {
        locais.add(new LocalMapa(0, "Portão do Cemitério",  "Grades retorcidas. Névoa rastejante.",         "Missao2_sala1.jpg",    false));
        locais.add(new LocalMapa(1, "Cripta Subterrânea",   "Escadaria que desce às trevas.",               "Missao2_sala2.jpg",    false));
        locais.add(new LocalMapa(2, "Catacumba Profunda",   "Ossos nas paredes. Sussurros constantes.",     "Missao2_sala3.jpg",    false));
        locais.add(new LocalMapa(3, "Salão da Decadência",  "O Senhor da Morte aguarda no trono de ossos.", "Missao2_salaFinal.jpg",true));
    }

    private void inicializarLocaisEnergia() {
        locais.add(new LocalMapa(0, "Entrada do Laboratório","Avisos de perigo ignorados. Luzes piscando.", "Missao3_sala1.jpg",    false));
        locais.add(new LocalMapa(1, "Reator Central",        "Energia crua pulsa nas paredes.",             "Missao3_sala2.jpg",    false));
        locais.add(new LocalMapa(2, "Núcleo Instável",       "A distorção da realidade aumenta aqui.",      "Missao3_sala3.jpg",    false));
        locais.add(new LocalMapa(3, "Epicentro Energético",  "A Tempestade Viva aguarda.",                  "Missao3_salaFinal.jpg",true));
    }

    private void inicializarLocaisConhecimento() {
        locais.add(new LocalMapa(0, "Hall da Biblioteca",   "Prateleiras até o teto. Silêncio opressor.",  "Missao1_sala1.jpg",    false));
        locais.add(new LocalMapa(1, "Acervo Proibido",      "Livros que não deveriam existir.",             "Missao1_sala2.jpg",    false));
        locais.add(new LocalMapa(2, "Arquivo Oculto",       "A verdade está enterrada aqui.",               "Missao1_sala3.jpg",    false));
        locais.add(new LocalMapa(3, "O Olho que Tudo Vê",  "O Guardião do Conhecimento aguarda.",          "Missao1_salaFinal.jpg",true));
    }

    // ── Lógica do mapa ────────────────────────────────────────────
    public LocalMapa getLocalAtualObj() {
        if (locais.isEmpty() || localAtual >= locais.size()) return null;
        return locais.get(localAtual);
    }

    public boolean localAtualTemPagina() {
        LocalMapa l = getLocalAtualObj();
        return l != null && l.isPaginaEncontrada();
    }

    public boolean podeAvancarLocal() {
        if (locais.isEmpty()) return true;
        LocalMapa atual = getLocalAtualObj();
        if (atual == null) return false;
        if (atual.isBossRoom()) return false;
        return atual.isPaginaEncontrada();
    }

    public boolean bossLiberado() {
        // Boss liberado quando todas as páginas (locais não-boss) foram encontradas
        return locais.stream()
                .filter(l -> !l.isBossRoom())
                .allMatch(LocalMapa::isPaginaEncontrada);
    }

    public void marcarPaginaEncontrada() {
        LocalMapa l = getLocalAtualObj();
        if (l != null) l.setPaginaEncontrada(true);
        // Sincroniza progressoAtual com páginas encontradas
        this.progressoAtual = (int) locais.stream()
                .filter(l2 -> !l2.isBossRoom() && l2.isPaginaEncontrada())
                .count();
    }

    public void avancarLocal() {
        if (localAtual < locais.size() - 1) {
            localAtual++;
            // Sincroniza salaAtual com localAtual
            this.salaAtual = localAtual;
        }
    }

    public void restaurarPaginasDoProgresso() {
        // Chamado ao carregar progresso do banco — marca as páginas já encontradas
        int paginas = this.progressoAtual;
        for (int i = 0; i < locais.size() && paginas > 0; i++) {
            if (!locais.get(i).isBossRoom()) {
                locais.get(i).setPaginaEncontrada(true);
                paginas--;
            }
        }
        // Restaura localAtual baseado na sala salva
        this.localAtual = Math.min(this.salaAtual, locais.size() - 1);
    }

    // ── Métodos originais ─────────────────────────────────────────
    public boolean objetivoConcluido() {
        return progressoAtual >= totalObjetivo;
    }

    public boolean ultimaSala() {
        return salaAtual >= totalSalas;
    }

    public boolean primeiraSala() {
        return salaAtual <= 0;
    }

    public int avancarSala() {
        salaAtual = Math.min(salaAtual + 1, totalSalas);
        localAtual = salaAtual;
        return salaAtual;
    }

    public int voltarSala() {
        salaAtual = Math.max(0, salaAtual - 1);
        localAtual = salaAtual;
        return salaAtual;
    }

    public int incrementarProgresso() {
        progressoAtual = Math.min(progressoAtual + 1, totalObjetivo);
        return progressoAtual;
    }

    public String getProgressoTexto() {
        return progressoAtual + "/" + totalObjetivo;
    }

    public void marcarInimigoDerrotado(int sala) {
        salasComInimigoDerrotado.put(sala, true);
    }

    public boolean salaTemInimigoDerrotado(int sala) {
        return salasComInimigoDerrotado.getOrDefault(sala, false);
    }

    public void marcarSalaInvestigada(int sala) {
        salasJaInvestigadas.put(sala, true);
    }

    public boolean salaJaFoiInvestigada(int sala) {
        return salasJaInvestigadas.getOrDefault(sala, false);
    }

    // ── Getters / Setters ─────────────────────────────────────────
    public int     getId()                          { return id; }
    public void    setId(int id)                    { this.id = id; inicializarLocais(); }
    public String  getTitulo()                      { return titulo; }
    public void    setTitulo(String titulo)         { this.titulo = titulo; }
    public String  getDescricao()                   { return descricao; }
    public void    setDescricao(String d)           { this.descricao = d; }
    public String  getObjetivo()                    { return objetivo; }
    public void    setObjetivo(String o)            { this.objetivo = o; }
    public int     getTotalObjetivo()               { return totalObjetivo; }
    public void    setTotalObjetivo(int t)          { this.totalObjetivo = t; }
    public int     getNivelMinimo()                 { return nivelMinimo; }
    public void    setNivelMinimo(int n)            { this.nivelMinimo = n; }
    public int     getTotalSalas()                  { return totalSalas; }
    public void    setTotalSalas(int t)             { this.totalSalas = t; }
    public int     getProgressoAtual()              { return progressoAtual; }
    public void    setProgressoAtual(int p)         { this.progressoAtual = Math.max(0, p); }
    public int     getSalaAtual()                   { return salaAtual; }
    public void    setSalaAtual(int s)              { this.salaAtual = Math.max(0, s); this.localAtual = this.salaAtual; }
    public boolean isConcluida()                    { return concluida; }
    public void    setConcluida(boolean c)          { this.concluida = c; }
    public int     getVezesRetornou()               { return vezesRetornou; }
    public void    setVezesRetornou(int v)          { this.vezesRetornou = Math.max(0, v); }
    public boolean isFugiu()                        { return fugiu; }
    public void    setFugiu(boolean f)              { this.fugiu = f; }
    public List<LocalMapa> getLocais()              { return locais; }
    public int     getLocalAtual()                  { return localAtual; }
    public void    setLocalAtual(int l)             { this.localAtual = Math.max(0, l); }
}