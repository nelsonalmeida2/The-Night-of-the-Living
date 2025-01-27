package pt.ulusofona.lp2.thenightofthelivingdeisi;

import java.util.ArrayList;

public class SafeHeaven {
    private final int linha;
    private final int coluna;
    private final ArrayList<Creature> survivors;

    public SafeHeaven(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
        this.survivors = new ArrayList<>();
    }

    public ArrayList<Creature> getSurvivors() {
        return survivors;
    }

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }

    String getSquareInfoSafeHeaven() { //TODO IMPLEMNTAR
        return "SH";
    }


}
