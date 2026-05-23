package Controller;

import Model.Inimigo;

class BatalhaController {

    private static Inimigo inimigoAtual;

    static void setInimigoAtual(Inimigo inimigo) {
        inimigoAtual = inimigo;
    }

    static Inimigo getInimigoAtual() {
        return inimigoAtual;
    }
}