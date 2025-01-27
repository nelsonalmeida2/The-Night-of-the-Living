package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class Escudo extends Equipment {

    public Escudo(int id, int coluna, int linha) {
        super(id, 0, coluna, linha);
    }


    @Override
    public String[] getInfo() {
        String[] info = new String[5];
        info[0] = String.valueOf(this.id);

        info[1] = String.valueOf(0);

        info[2] = String.valueOf(coluna);

        info[3] = String.valueOf(linha);

        info[4] = null;

        return info;
    }

    public String getInfoComoString() {
        return id + " | Escudo de madeira @ (" + coluna + ", " + linha + ")";
    }

    @Override
    public int getTipo() {
        return TIPO_ESCUDO;
    }

    @Override
    public void usar() {

    }

    public boolean temQuantidade() {
        return true;
    }
}