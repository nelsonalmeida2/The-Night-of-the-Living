package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class Pistola extends Equipment {
    private int balas;

    public Pistola(int id, int coluna, int linha, int balas) {
        super(id, 2, coluna, linha);
        this.balas = balas;
    }

    @Override
    public String[] getInfo() {
        String[] info = new String[5];

        info[0] = String.valueOf(this.id);

        info[1] = String.valueOf(2);

        info[2] = String.valueOf(coluna);

        info[3] = String.valueOf(linha);

        info[4] = null;

        return info;
    }

    @Override
    public String getInfoComoString() {
        return id + " | " + "Pistola Walther PPK" + " @ (" + coluna + ", " + linha + ")" +
                " | " + balas + " balas";
    }

    @Override
    public void usar() {
        if (balas > 0) {
            balas--;
        }
    }

    @Override
    public int getTipo() {
        return TIPO_PISTOLA;
    }

    @Override
    public boolean temQuantidade() {
        return balas > 0;
    }

}