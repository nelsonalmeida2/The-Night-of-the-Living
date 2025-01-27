package pt.ulusofona.lp2.thenightofthelivingdeisi;

import java.util.ArrayList;

abstract class Creature {

    final static int ID_EQUIPA_HUMANOS = 20;
    final static int ID_EQUIPA_MORTOS = 10;
    protected int numeroEquipamentosCapturados;
    protected int id;
    protected int equipa;// 10 maus da fita | 20 bons da fita
    protected int tipo;
    protected String nome;
    protected int coluna;
    protected int linha;
    protected String nomeTipo;
    protected ArrayList<Equipment> equipamentos;
    protected int numeroEquipamentosDestruidos;
    protected boolean transformado;
    protected boolean estaNoSafeHeaven;

    public Creature(int id, int equipa, int tipo, String nome, int coluna, int linha, String nomeTipo) {
        this.id = id;
        this.equipa = equipa;
        this.tipo = tipo;
        this.nome = nome;
        this.coluna = coluna;
        this.linha = linha;
        this.nomeTipo = nomeTipo;
        this.equipamentos = new ArrayList<>();
        this.numeroEquipamentosDestruidos = 0;
        this.transformado = false;
        this.estaNoSafeHeaven = false;
    }

    public Creature() {
    }

    static Creature criarCreature(String[] criaturaInfo) throws IllegalArgumentException {
        switch (Integer.parseInt(criaturaInfo[2])) { // a linha é o 5 e a coluna o 4
            case 0: // Peste
                return new Crianca(Integer.parseInt(criaturaInfo[0]), Integer.parseInt(criaturaInfo[1]),
                        Integer.parseInt(criaturaInfo[2]), criaturaInfo[3],
                        Integer.parseInt(criaturaInfo[4]), Integer.parseInt(criaturaInfo[5]));
            case 1: // Cota
                return new Adulto(Integer.parseInt(criaturaInfo[0]), Integer.parseInt(criaturaInfo[1]),
                        Integer.parseInt(criaturaInfo[2]), criaturaInfo[3],
                        Integer.parseInt(criaturaInfo[4]), Integer.parseInt(criaturaInfo[5]));
            case 2: // Terceira Idade
                return new Idoso(Integer.parseInt(criaturaInfo[0]), Integer.parseInt(criaturaInfo[1]),
                        Integer.parseInt(criaturaInfo[2]), criaturaInfo[3],
                        Integer.parseInt(criaturaInfo[4]), Integer.parseInt(criaturaInfo[5]));
            case 3: // Bobi
                if (Integer.parseInt(criaturaInfo[1]) == ID_EQUIPA_MORTOS) {
                    throw new IllegalArgumentException("O cão não pode ser zombie");
                }
                return new Cao(Integer.parseInt(criaturaInfo[0]), Integer.parseInt(criaturaInfo[1]),
                        Integer.parseInt(criaturaInfo[2]), criaturaInfo[3],
                        Integer.parseInt(criaturaInfo[4]), Integer.parseInt(criaturaInfo[5]));
            case 4: // Batman
                if (Integer.parseInt(criaturaInfo[1]) == ID_EQUIPA_HUMANOS) {
                    throw new IllegalArgumentException("O vampiro não pode ser humano");
                }
                return new Vampiro(Integer.parseInt(criaturaInfo[0]), Integer.parseInt(criaturaInfo[1]),
                        Integer.parseInt(criaturaInfo[2]), criaturaInfo[3],
                        Integer.parseInt(criaturaInfo[4]), Integer.parseInt(criaturaInfo[5]));
            default: // Algo certo está errado, era bem !
                throw new IllegalArgumentException("Tipo de Equipamento Desconhecido");
        }
    }

    public void entrarNoSafeHeaven() {
        this.estaNoSafeHeaven = true;
    }

    abstract boolean mover(int xO, int yO, int xD, int yD, Board tabuleiroJogo, int equipaAtual, int turno);

    public int nrEquipamentos() {
        return equipamentos.size();
    }

    public int getId() {
        return id;
    }

    public int getColuna() {
        return coluna;
    }

    public int getLinha() {
        return linha;
    }

    public String getNomeTipo() {
        return nomeTipo;
    }

    public String imprimir() {
        if (equipa == ID_EQUIPA_MORTOS) {
            return id + " (antigamente conhecido como " + nome + ")";
        }
        return id + " " + nome;
    }

    public String[] getInfo() {
        String[] info = new String[7];

        info[0] = String.valueOf((this.id));
        info[1] = nomeTipo;

        if (equipa == ID_EQUIPA_HUMANOS) {
            info[2] = "Humano";
        }
        if (equipa == ID_EQUIPA_MORTOS) {
            info[2] = "Zombie";
        }
        if (transformado) {
            info[2] = "Zombie (Transformado)";
        }

        info[3] = nome;

        if (estaNoSafeHeaven) {
            info[4] = null;

            info[5] = null;
        } else {
            info[4] = String.valueOf(coluna);

            info[5] = String.valueOf(linha);
        }


        info[6] = (null);

        return info;
    }

    boolean equipamento() {
        return false;
    }

    boolean creature() {
        return true;
    }

    String getSquareInfoCreature() {
        if (equipa == ID_EQUIPA_MORTOS) {
            return "Z:" + id;
        }
        if (equipa == ID_EQUIPA_HUMANOS) {
            return "H:" + id;
        }
        return "";
    }

    public ArrayList<Equipment> getEquipamentos() {
        return equipamentos;
    }

    public int getEquipa() {
        return equipa;
    }

    abstract String getInfoComoString();

    public String getNome() {
        return nome;
    }

    public boolean temEquipamento(int equipmentTypeId) {
        for (Equipment equipment : equipamentos) {
            if (equipment.comparaTipo(equipmentTypeId)) {
                return true;
            }
        }
        return false;
    }

    public int getTipo() {
        return tipo;
    }
}
