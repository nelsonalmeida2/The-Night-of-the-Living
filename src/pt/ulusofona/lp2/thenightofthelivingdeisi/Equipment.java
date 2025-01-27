package pt.ulusofona.lp2.thenightofthelivingdeisi;

abstract class Equipment {
    public static final int TIPO_ESCUDO = 0;
    public static final int TIPO_ESPADA = 1;
    public static final int TIPO_PISTOLA = 2;
    public static final int TIPO_LIXIVIA = 3;


    protected int id;
    protected int tipo;
    protected int linha;
    protected int coluna;


    public Equipment(int id, int tipo, int coluna, int linha) {
        this.id = id;
        this.tipo = tipo;
        this.coluna = coluna;
        this.linha = linha;
    }

    static Equipment criarEquipamento(String[] equipamentoInfo) throws IllegalArgumentException {
        Equipment equip = null;
        switch (Integer.parseInt(equipamentoInfo[1])) { // a linha é o 5 e a coluna o 4
            case 0:
                equip = new Escudo(Integer.parseInt(equipamentoInfo[0]), Integer.parseInt(equipamentoInfo[2]),
                        Integer.parseInt(equipamentoInfo[3]));
                break;
            case 1:
                equip = new EspadaSamurai(Integer.parseInt(equipamentoInfo[0]), Integer.parseInt(equipamentoInfo[2]),
                        Integer.parseInt(equipamentoInfo[3]));
                break;
            case 2:
                equip = new Pistola(Integer.parseInt(equipamentoInfo[0]), Integer.parseInt(equipamentoInfo[2]),
                        Integer.parseInt(equipamentoInfo[3]), 3);
                break;
            case 3:
                equip = new Lixivia(Integer.parseInt(equipamentoInfo[0]), Integer.parseInt(equipamentoInfo[2]),
                        Integer.parseInt(equipamentoInfo[3]), 1.0);
                break;
            default:
                throw new IllegalArgumentException("Tipo de Equipamento Desconhecido");
        }
        return equip;
    }

    public abstract int getTipo();

    String getSquareInfoEquipment() {
        return "E:" + id;
    }

    public int getId() {
        return id;
    }

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }

    abstract public String[] getInfo();

    abstract String getInfoComoString();

    abstract void usar();

    abstract boolean temQuantidade();

    boolean comparaId(int id) {
        return id == this.id;
    }

    boolean comparaTipo(int tipo) {
        return tipo == this.tipo;
    }

}
