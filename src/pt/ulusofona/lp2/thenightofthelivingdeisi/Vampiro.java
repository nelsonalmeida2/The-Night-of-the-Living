package pt.ulusofona.lp2.thenightofthelivingdeisi;

import java.util.ArrayList;
//TODO QUANDO O VAMPIRO MATA O OUTRO VIRA UMA COPIA DO VAMPIRO ARRANJAR
public class Vampiro extends Creature {

    public Vampiro(int id, int equipa, int tipo, String nome, int coluna, int linha) {
        super(id, equipa, tipo, nome, coluna, linha, "Vampiro");
        this.equipamentos = new ArrayList<>();
        this.numeroEquipamentosDestruidos = 0;
    }


    @Override
    public boolean mover(int xO, int yO, int xD, int yD, Board tabuleiroJogo, int equipaAtual, int turno) {
        if (equipa == equipaAtual) {
            // Verificar se é noite
            if (isDay(turno)) { // Supondo que noite é turno ímpar
                return false;
            }

            // Verificar se o destino está dentro dos limites do tabuleiro
            if (!tabuleiroJogo.dentroDosLimites(xD, yD)) {
                return false;
            }

            // Permitir movimento de 1 casa em qualquer direção
            if (Math.abs(this.coluna - xD) > 1 || Math.abs(this.linha - yD) > 1) {
                return false;
            }

            if (tabuleiroJogo.temCreature(xD, yD)) {
                Creature alvo = tabuleiroJogo.getCreature(xD, yD);
                if (alvo.getEquipa() != ID_EQUIPA_MORTOS) {
                    if (!alvo.equipamentos.isEmpty()) {
                        Equipment equipamento = alvo.equipamentos.get(0);


                        if (!equipamento.temQuantidade()) {
                            alvo.equipamentos.remove(equipamento);
                            alvo.equipa = ID_EQUIPA_MORTOS;
                            alvo.transformado = true;
                            alvo.numeroEquipamentosDestruidos = alvo.numeroEquipamentosCapturados;
                            tabuleiroJogo.resetMudancas();
                        }
                        equipamento.usar();
                        return true;
                    }
                    alvo.numeroEquipamentosDestruidos = alvo.numeroEquipamentosCapturados;
                    alvo.equipa = ID_EQUIPA_MORTOS;
                    alvo.transformado = true;
                    tabuleiroJogo.resetMudancas();
                    return true;
                }
            }

            tabuleiroJogo.decrementarMudancas();
            // Verificar se há um equipamento na célula de destino
            if (tabuleiroJogo.temEquipment(xD, yD)) {
                Equipment equipamentoAlvo = tabuleiroJogo.getEquipment(xD, yD);
                // Destruir o equipamento
                tabuleiroJogo.getTabuleiro()[xD][yD].setEquipment(null);
                numeroEquipamentosDestruidos++;
                tabuleiroJogo.getTabuleiro()[xO][yO].setCreature(null); // Remove o Vampiro da célula de origem
                tabuleiroJogo.getTabuleiro()[xD][yD].setCreature(this); // Move o Vampiro para a célula do equipamento destruído
                this.coluna = xD;
                this.linha = yD;
                return true;
                //destruir um equipamento
            }

            // Movimento normal para uma célula vazia
            if (!tabuleiroJogo.tileOcupado(xD, yD)) {
                tabuleiroJogo.getTabuleiro()[xO][yO].setCreature(null);  // Remove o Vampiro da célula de origem
                tabuleiroJogo.getTabuleiro()[xD][yD].setCreature(this); // Move o Vampiro para a célula de destino
                this.coluna = xD;
                this.linha = yD;
                return true;
            }
        }
        return false;
    }



    @Override
    String getSquareInfoCreature() {
        return "Z:" + id;
    }


    private boolean isDay(int turno) {
        return (turno / 2) % 2 == 0;
    }

    @Override
    public String getInfoComoString() {
        return id + " | " + nomeTipo + " | " + nome + " | " + "-" + numeroEquipamentosDestruidos + " @ (" + coluna + ", " + linha + ")";
    }
}