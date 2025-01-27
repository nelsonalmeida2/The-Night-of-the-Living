package pt.ulusofona.lp2.thenightofthelivingdeisi;

import java.util.ArrayList;
import java.util.Comparator;

public class Board {
    private final Tile[][] tabuleiro;
    private int mudancas;

    public Board(int numeroColunas, int numeroLinhas) {
        tabuleiro = new Tile[numeroColunas][numeroLinhas];
        for (int i = 0; i < numeroColunas; i++) {
            for (int j = 0; j < numeroLinhas; j++) {
                tabuleiro[i][j] = new Tile(); // Inicializa cada posição do tabuleiro
            }
        }
        mudancas = 8;
    }

    public Tile[][] getTabuleiro() {
        return tabuleiro;
    }

    public int getMudancas() {
        return mudancas;
    }

    public void decrementarMudancas() {
        mudancas--;
    }

    public void resetMudancas() {
        mudancas = 8;
    }

    public int getLinhas() {
        return tabuleiro[0].length;
    }

    public int getColunas() {
        return tabuleiro.length;
    }

    public Creature getCreature(int x, int y) {
        return tabuleiro[x][y].getCreature();
    }

    public Equipment getEquipment(int x, int y) {
        return tabuleiro[x][y].getEquipment();
    }

    public SafeHeaven getSafeHeaven(int x, int y) {
        return tabuleiro[x][y].getSafeHeaven();
    }

    public boolean dentroDosLimites(int x, int y) {
        return x >= 0 && x < tabuleiro.length && y >= 0 && y < tabuleiro[0].length;
    }

    public Equipment procuraEquipamentoPorID(int equipamentoID) {
        for (int i = 0; i < tabuleiro.length; i++) {
            for (int j = 0; j < tabuleiro[0].length; j++) {
                if (tabuleiro[i][j].getEquipment() != null) {
                    if (tabuleiro[i][j].getEquipment().getId() == equipamentoID) {
                        return tabuleiro[i][j].getEquipment();
                    }
                }
            }
        }
        return null;
    }

    public Creature procuraCriaturaPorID(int criaturaID) {
        for (int i = 0; i < tabuleiro.length; i++) {
            for (int j = 0; j < tabuleiro[0].length; j++) {
                if (tabuleiro[i][j].getCreature() != null) {
                    if (tabuleiro[i][j].getCreature().getId() == criaturaID) {
                        return tabuleiro[i][j].getCreature();
                    }
                }
                if (tabuleiro[i][j].getSafeHeaven() != null) {
                    ArrayList<Creature> survivors = tabuleiro[i][j].getSafeHeaven().getSurvivors();
                    if (!survivors.isEmpty()) {
                        for (Creature survivor : survivors) {
                            if (survivor.getId() == criaturaID) {
                                return survivor;
                            }
                        }
                    }
                }
            }
        }
        return new NullCreature();
    }

    boolean temCreature(int coluna, int linha) {
        return tabuleiro[coluna][linha].getCreature() != null;
    }

    boolean temEquipment(int coluna, int linha) {
        return tabuleiro[coluna][linha].getEquipment() != null;
    }

    boolean temSafeHeaven(int coluna, int linha) {
        return tabuleiro[coluna][linha].getSafeHeaven() != null;
    }

    boolean tileOcupado(int coluna, int linha) {
        return temCreature(coluna, linha) || temEquipment(coluna, linha) || temSafeHeaven(coluna, linha);
    }

    boolean adicionarCreatura(Creature piece) {
        //verifica se ja foi colocada alguma peça nesse local
        if (tileOcupado(piece.getColuna(), piece.getLinha())) {
            return false;
        }

        //verifica se esta dentro dos limites
        if (!dentroDosLimites(piece.getColuna(), piece.getLinha())) {
            return false;
        }

        //Mete a peça no tabuleiro
        tabuleiro[piece.getColuna()][piece.getLinha()].setCreature(piece);

        return true;
    }

    boolean adicionarEquipamento(Equipment piece) { // Esta dividido porque não sabemos como funciona o load de um jogo que ja vai meio, pode ser que as peças fiquem no mesmo sitio de quem os possui
        //verifica se ja foi colocada alguma peça nesse local
        if (tileOcupado(piece.getColuna(), piece.getLinha())) {
            return false;
        }

        //verifica se esta dentro dos limites
        if (!dentroDosLimites(piece.getColuna(), piece.getLinha())) {
            return false;
        }

        //Mete a peça no tabuleiro
        tabuleiro[piece.getColuna()][piece.getLinha()].setEquipment(piece);

        return true;
    }

    boolean adicionarSafeHeaven(SafeHeaven piece) { // Esta dividido porque não sabemos como funciona o load de um jogo que ja vai meio, pode ser que as peças fiquem no mesmo sitio de quem os possui
        //verifica se ja foi colocada alguma peça nesse local
        if (tileOcupado(piece.getColuna(), piece.getLinha())) {
            return false;
        }

        //verifica se esta dentro dos limites
        if (!dentroDosLimites(piece.getLinha(), piece.getLinha())) {
            return false;
        }

        //Mete a peça no tabuleiro
        tabuleiro[piece.getColuna()][piece.getLinha()].setSafeHeaven(piece);

        return true;
    }

    boolean adicionaHumanoSafeHeaven(Creature humano, int x, int y) {
        if (tabuleiro[x][y].getSafeHeaven() != null) {
            tabuleiro[x][y].getSafeHeaven().getSurvivors().add(humano);
            humano.entrarNoSafeHeaven();
            return true;
        }
        return false;
    }

    void removeCreature(Creature creature, int x, int y) {
        tabuleiro[x][y].setCreature(null);
    }

    String getSquareInfo(int x, int y) {
        if (temCreature(x, y)) {
            return getCreature(x, y).getSquareInfoCreature();
        }
        if (temEquipment(x, y)) {
            return getEquipment(x, y).getSquareInfoEquipment();
        }
        if (temSafeHeaven(x, y)) {
            return getSafeHeaven(x, y).getSquareInfoSafeHeaven();
        }
        return "";
    }

    public ArrayList<Creature> getAllCreatures() {
        ArrayList<Creature> criaturas = new ArrayList<>();
        for (int i = 0; i < tabuleiro.length; i++) {
            for (int j = 0; j < tabuleiro[i].length; j++) {
                if (temCreature(i, j)) {
                    criaturas.add(getCreature(i, j));
                }
            }
        }
        return criaturas;
    }

    public ArrayList<Equipment> getAllEquipments() {
        ArrayList<Equipment> equipamentos = new ArrayList<>();
        for (int i = 0; i < tabuleiro.length; i++) {
            for (int j = 0; j < tabuleiro[i].length; j++) {
                if (temEquipment(i, j)) {
                    equipamentos.add(getEquipment(i, j));
                }
            }
        }
        return equipamentos;
    }

    public ArrayList<SafeHeaven> getAllSafeHeavens() {
        ArrayList<SafeHeaven> safeHeavens = new ArrayList<>();
        for (int i = 0; i < tabuleiro.length; i++) {
            for (int j = 0; j < tabuleiro[i].length; j++) {
                if (temSafeHeaven(i, j)) {
                    safeHeavens.add(getSafeHeaven(i, j));
                }
            }
        }
        return safeHeavens;
    }

    public boolean soHumanos() {
        int hum = 0;
        int zom = 0;
        for (int i = 0; i < tabuleiro.length; i++) {
            for (int j = 0; j < tabuleiro[i].length; j++) {
                if (tabuleiro[i][j].getCreature() != null && tabuleiro[i][j].getCreature().equipa == Creature.ID_EQUIPA_HUMANOS) {
                    hum++;
                }
                if (tabuleiro[i][j].getCreature() != null && tabuleiro[i][j].getCreature().equipa == Creature.ID_EQUIPA_MORTOS) {
                    zom++;
                }
            }
        }
        return zom == 0 && hum > 0;
    }

    public boolean soZom() {
        int hum = 0;
        int zom = 0;
        for (int i = 0; i < tabuleiro.length; i++) {
            for (int j = 0; j < tabuleiro[i].length; j++) {
                if (tabuleiro[i][j].getCreature() != null && tabuleiro[i][j].getCreature().equipa == Creature.ID_EQUIPA_HUMANOS) {
                    hum++;
                }
                if (tabuleiro[i][j].getCreature() != null && tabuleiro[i][j].getCreature().equipa == Creature.ID_EQUIPA_MORTOS) {
                    zom++;
                }
            }
        }
        return zom > 0 && hum == 0;
    }

    public ArrayList<Creature> getHumanos() {
        ArrayList<Creature> humanos = new ArrayList<>();
        for (int i = 0; i < tabuleiro.length; i++) {
            for (int j = 0; j < tabuleiro[i].length; j++) {
                if (tabuleiro[i][j].getCreature() != null
                        && tabuleiro[i][j].getCreature().equipa == Creature.ID_EQUIPA_HUMANOS) {
                    humanos.add(tabuleiro[i][j].getCreature());
                }
                if (tabuleiro[i][j].getSafeHeaven() != null) {
                    ArrayList<Creature> survivors = tabuleiro[i][j].getSafeHeaven().getSurvivors();
                    humanos.addAll(survivors);
                }
            }
        }
        humanos.sort(Comparator.comparing(Creature::getId));
        return humanos;
    }

    public ArrayList<Creature> getZombies() {
        ArrayList<Creature> humanos = new ArrayList<>();
        for (int i = 0; i < tabuleiro.length; i++) {
            for (int j = 0; j < tabuleiro[i].length; j++) {
                if (tabuleiro[i][j].getCreature() != null
                        && tabuleiro[i][j].getCreature().equipa == Creature.ID_EQUIPA_MORTOS) {
                    humanos.add(tabuleiro[i][j].getCreature());
                }
            }
        }
        humanos.sort(Comparator.comparing(Creature::getId));
        return humanos;
    }

}

