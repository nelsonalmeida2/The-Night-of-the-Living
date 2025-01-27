package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class NullCreature extends Creature {

    public NullCreature(int id, int equipa, int tipo, String nome, int coluna, int linha, String nomeTipo) {
        super(id, equipa, tipo, nome, coluna, linha, nomeTipo);
    }

    public NullCreature() {

    }

    @Override
    boolean mover(int xO, int yO, int xD, int yD, Board tabuleiroJogo, int equipaAtual, int turno) {
        return false;
    }

    @Override
    String getInfoComoString() {
        return null;
    }

    @Override
    public String[] getInfo() {
        return new String[GameManager.NR_COMPONENTES_CRIATURA];
    }
}
