package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class Lixivia extends Equipment {
    private double litros;

    public Lixivia(int id, int coluna, int linha, double litros) {
        super(id, 3, coluna, linha);
        this.litros = litros;
    }


    @Override
    public String[] getInfo() {
        String[] info = new String[5];
        info[0] = String.valueOf(this.id);

        info[1] = String.valueOf(3);

        info[2] = String.valueOf(coluna);

        info[3] = String.valueOf(linha);

        info[4] = null;

        return info;
    }

    @Override
    String getInfoComoString() {
        return id + " | " + "Lixívia" + " @ (" + coluna + ", " + linha + ")" +
                " | " + litros + " litros";
    }

    @Override
    public void usar() {
        if (temQuantidade()) {
            if (litros >= 0.3) {
                litros -= 0.3;
            } else {
                litros = 0;
            }
            // Arredonda para uma casa decimal
            litros = Math.round(litros * 10.0) / 10.0;
        }
    }

    @Override
    public int getTipo() {
        return TIPO_LIXIVIA;
    }

    @Override
    public boolean temQuantidade() {
        return litros > 0;
    }
}