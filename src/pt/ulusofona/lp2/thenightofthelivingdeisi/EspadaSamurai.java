package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class EspadaSamurai extends Equipment {
    public EspadaSamurai(int id, int coluna, int linha) {
        super(id, 1, coluna, linha);
    }


    public String[] getInfo() {
        String[] info = new String[5];

        info[0] = (String.valueOf(this.id));

        info[1] = String.valueOf(1);

        info[2] = (String.valueOf(coluna));

        info[3] = (String.valueOf(linha));

        info[4] = (null);

        return info;
    }

    public String getInfoComoString() {
        return getId() + " | Espada samurai @ (" + coluna + ", " + linha + ")";
    }

    @Override
    public void usar() {

    }

    @Override
    public int getTipo() {
        return TIPO_ESPADA;
    }

    public boolean temQuantidade() {
        return true;
    }


}
