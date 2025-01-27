package pt.ulusofona.lp2.thenightofthelivingdeisi;

import java.util.ArrayList;

public class Cao extends Creature {

    public Cao(int id, int equipa, int tipo, String nome, int coluna, int linha) {
        super(id, equipa, tipo, nome, coluna, linha, "Cão");
        this.equipamentos = new ArrayList<>();
        this.numeroEquipamentosDestruidos = 0;
    }

    @Override
    public boolean mover(int xO, int yO, int xD, int yD, Board tabuleiroJogo, int equipaAtual, int turno) {
        if (!podeMover(equipaAtual, xD, yD, tabuleiroJogo)) {
            return false;
        }

        if (tabuleiroJogo.temSafeHeaven(xD, yD)) {
            return moverParaSafeHeaven(xO, yO, xD, yD, tabuleiroJogo);
        }

        return moverParaNovaPosicao(xO, yO, xD, yD, tabuleiroJogo);
    }

    private boolean podeMover(int equipaAtual, int xD, int yD, Board tabuleiroJogo) {
        if (equipa != equipaAtual) {
            return false;
        }

        if (!tabuleiroJogo.dentroDosLimites(xD, yD)) {
            return false;
        }

        int casa = 0;

        // Verifica se a movimentação é válida (não é mais que 2 casas e na mesma linha ou coluna)
        if ((Math.abs(this.coluna - xD) > 2 || Math.abs(this.linha - yD) > 2 || (this.coluna != xD && this.linha != yD))) {
            casa++;
        }

        if (Math.abs(this.coluna - xD) > 1 || Math.abs(this.linha - yD) > 1 || (this.coluna != xD && this.linha != yD)) {
            casa++;
        }

        return casa != 2 && !tabuleiroJogo.temCreature(xD, yD) && !tabuleiroJogo.temEquipment(xD, yD);
    }

    private boolean moverParaSafeHeaven(int xO, int yO, int xD, int yD, Board tabuleiroJogo) {
        tabuleiroJogo.adicionaHumanoSafeHeaven(this, xD, yD);
        tabuleiroJogo.removeCreature(this, xO, yO);
        return true;
    }

    private boolean moverParaNovaPosicao(int xO, int yO, int xD, int yD, Board tabuleiroJogo) {
        tabuleiroJogo.decrementarMudancas();
        tabuleiroJogo.getTabuleiro()[xD][yD].setCreature(this);
        this.coluna = xD;
        this.linha = yD;
        tabuleiroJogo.getTabuleiro()[xO][yO].setCreature(null);
        return true;
    }

    @Override
    public String getInfoComoString() {
        if (estaNoSafeHeaven) {
            return id + " | " + nomeTipo + " | " + nome + " @ Safe Haven";
        }
        return id + " | " + nomeTipo + " | " + nome + " @ (" + coluna + ", " + linha + ")";
    }
}
