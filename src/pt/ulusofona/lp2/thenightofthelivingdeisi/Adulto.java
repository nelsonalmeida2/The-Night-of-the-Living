package pt.ulusofona.lp2.thenightofthelivingdeisi;

import java.util.ArrayList;

public class Adulto extends Creature {
    public Adulto(int id, int equipa, int tipo, String nome, int coluna, int linha) {
        super(id, equipa, tipo, nome, coluna, linha, "Adulto");
        this.equipamentos = new ArrayList<>();
        this.numeroEquipamentosDestruidos = 0;
    }

    @Override
    public boolean mover(int xO, int yO, int xD, int yD, Board tabuleiroJogo, int equipaAtual, int turno) {
        if (equipa == equipaAtual) {
            tabuleiroJogo.decrementarMudancas();
            if (equipa == ID_EQUIPA_HUMANOS) {
                return moverHumano(xO, yO, xD, yD, tabuleiroJogo, equipaAtual, turno);
            }
            return moverZombie(xO, yO, xD, yD, tabuleiroJogo, equipaAtual, turno);
        }
        return false;
    }

    private boolean destruirEquipamento(int xO, int yO, int xD, int yD, Board tabuleiroJogo) {
        tabuleiroJogo.getTabuleiro()[xD][yD].setEquipment(null);
        tabuleiroJogo.getTabuleiro()[xO][yO].setCreature(null);
        tabuleiroJogo.getTabuleiro()[xD][yD].setCreature(this);
        this.coluna = xD;
        this.linha = yD;
        numeroEquipamentosDestruidos++;
        return true;
    }

    private boolean atacarHumano(int xO, int yO, int xD, int yD, Board tabuleiroJogo) {
        Creature alvo = tabuleiroJogo.getCreature(xD, yD);

        if (alvo.getEquipa() != ID_EQUIPA_MORTOS) { // Ataca apenas humanos
            if (!alvo.equipamentos.isEmpty()) {
                Equipment equipamento = alvo.equipamentos.get(0);

                if (!equipamento.temQuantidade()) {
                    alvo.equipamentos.remove(equipamento);
                    alvo.equipa = ID_EQUIPA_MORTOS;
                    alvo.transformado = true;
                    alvo.numeroEquipamentosDestruidos = alvo.numeroEquipamentosCapturados;
                    tabuleiroJogo.resetMudancas();
                    return true;
                }

                equipamento.usar();
                return true;
            }

            // Sem equipamento, transforma diretamente em zumbi
            alvo.numeroEquipamentosDestruidos = alvo.numeroEquipamentosCapturados;
            alvo.equipa = ID_EQUIPA_MORTOS;
            alvo.transformado = true;
            tabuleiroJogo.resetMudancas();
            return true;
        }
        return false;
    }

    public boolean moverZombie(int xO, int yO, int xD, int yD, Board tabuleiroJogo, int equipaAtual, int turno) {
        if (!isMovimentoValido(xO, yO, xD, yD, tabuleiroJogo)) {
            return false;
        }

        if (tabuleiroJogo.temSafeHeaven(xD, yD)) {
            return false; // Zumbis não podem entrar em Safe Heavens
        }

        if (tabuleiroJogo.temEquipment(xD, yD)) {
            return destruirEquipamento(xO, yO, xD, yD, tabuleiroJogo);
        }

        if (tabuleiroJogo.temCreature(xD, yD)) {
            return atacarHumano(xO, yO, xD, yD, tabuleiroJogo);
        }

        if (!tabuleiroJogo.tileOcupado(xD, yD)) {
            return moverParaCelulaVaziaZombie(xO, yO, xD, yD, tabuleiroJogo);
        }

        return false;
    }

    public boolean moverHumano(int xO, int yO, int xD, int yD, Board tabuleiroJogo, int equipaAtual, int turno) {
        if (!isMovimentoValido(xO, yO, xD, yD, tabuleiroJogo)) {
            return false;
        }

        if (tabuleiroJogo.temCreature(xD, yD)) {
            return atacarInimigo(xO, yO, xD, yD, tabuleiroJogo);
        }

        if (tabuleiroJogo.temSafeHeaven(xD, yD)) {
            return moverParaSafeHeaven(xO, yO, xD, yD, tabuleiroJogo);
        }

        if (tabuleiroJogo.temEquipment(xD, yD)) {
            return interagirComEquipamento(xO, yO, xD, yD, tabuleiroJogo);
        }

        if (!tabuleiroJogo.tileOcupado(xD, yD)) {
            return moverParaCelulaVazia(xO, yO, xD, yD, tabuleiroJogo);
        }

        return false;
    }

    private boolean isMovimentoValido(int xO, int yO, int xD, int yD, Board tabuleiroJogo) {
        if (!tabuleiroJogo.dentroDosLimites(xD, yD)) {
            return false;
        }

        // Permitir movimentos de até 2 casas em qualquer direção
        if (Math.abs(xO - xD) > 2 || Math.abs(yO - yD) > 2) {
            return false;
        }

        // Permitir movimento em linha reta ou diagonal
        return (xO == xD || yO == yD || Math.abs(xO - xD) == Math.abs(yO - yD));
    }

    private boolean atacarInimigo(int xO, int yO, int xD, int yD, Board tabuleiroJogo) {
        Creature inimigo = tabuleiroJogo.getCreature(xD, yD);

        if (inimigo.getEquipa() == ID_EQUIPA_MORTOS) { // Verifica se é inimigo
            for (Equipment equipamento : equipamentos) {
                if (equipamento.getTipo() == Equipment.TIPO_ESPADA ||
                        (equipamento.getTipo() == Equipment.TIPO_PISTOLA && equipamento.temQuantidade())) {

                    equipamento.usar();
                    tabuleiroJogo.removeCreature(inimigo, xD, yD);
                    tabuleiroJogo.getTabuleiro()[xD][yD].setCreature(this);
                    tabuleiroJogo.getTabuleiro()[xO][yO].setCreature(null);

                    this.coluna = xD;
                    this.linha = yD;

                    // Atualizar coordenadas do equipamento
                    equipamento.linha = yD;
                    equipamento.coluna = xD;

                    tabuleiroJogo.resetMudancas();
                    return true;
                }
            }
        }
        return false;
    }

    private boolean moverParaSafeHeaven(int xO, int yO, int xD, int yD, Board tabuleiroJogo) {
        if (!equipamentos.isEmpty()) {
            Equipment equipamentoAtual = equipamentos.get(0);
            equipamentoAtual.linha = yO;
            equipamentoAtual.coluna = xO;
        }
        tabuleiroJogo.adicionaHumanoSafeHeaven(this, xD, yD); // adiciona criatura ao safe haven
        tabuleiroJogo.removeCreature(this, xO, yO); // remove a referência da criatura do local anterior
        return true;
    }

    private boolean interagirComEquipamento(int xO, int yO, int xD, int yD, Board tabuleiroJogo) {
        Equipment equipamentoDestino = tabuleiroJogo.getEquipment(xD, yD);

        if (!equipamentos.isEmpty()) {
            Equipment equipamentoInventario = equipamentos.get(0);
            tabuleiroJogo.getTabuleiro()[xO][yO].setEquipment(equipamentoInventario);
            equipamentoInventario.linha = yO;
            equipamentoInventario.coluna = xO;
            equipamentos.remove(equipamentoInventario);
        }

        equipamentos.add(equipamentoDestino);
        numeroEquipamentosCapturados++;
        tabuleiroJogo.getTabuleiro()[xD][yD].setEquipment(null);
        tabuleiroJogo.getTabuleiro()[xD][yD].setCreature(this);

        this.coluna = xD;
        this.linha = yD;
        tabuleiroJogo.getTabuleiro()[xO][yO].setCreature(null);

        return true;
    }

    private boolean moverParaCelulaVazia(int xO, int yO, int xD, int yD, Board tabuleiroJogo) {
        tabuleiroJogo.getTabuleiro()[xD][yD].setCreature(this);
        this.coluna = xD;
        this.linha = yD;

        // Atualiza as coordenadas do equipamento no inventário
        if (!equipamentos.isEmpty()) {
            Equipment equipamentoAtual = equipamentos.get(0);
            equipamentoAtual.linha = yD;
            equipamentoAtual.coluna = xD;
        }

        tabuleiroJogo.getTabuleiro()[xO][yO].setCreature(null);
        return true;
    }

    private boolean moverParaCelulaVaziaZombie(int xO, int yO, int xD, int yD, Board tabuleiroJogo) {
        tabuleiroJogo.getTabuleiro()[xD][yD].setCreature(this);
        this.coluna = xD;
        this.linha = yD;

        tabuleiroJogo.getTabuleiro()[xO][yO].setCreature(null);
        return true;
    }

    @Override
    String getInfoComoString() {
        String equipaNome = "";
        String prefixoEquipamento = "";

        if (equipa == ID_EQUIPA_HUMANOS) {
            equipaNome = "Humano";
            prefixoEquipamento = "+";
            String informacaoBase;
            if (!estaNoSafeHeaven) {
                informacaoBase = id + " | " + nomeTipo + " | " + equipaNome + " | " + nome + " | " + prefixoEquipamento +
                        numeroEquipamentosCapturados + " @ (" + coluna + ", " + linha + ")";
            } else {
                informacaoBase = id + " | " + nomeTipo + " | " + equipaNome + " | " + nome + " | " + prefixoEquipamento +
                        numeroEquipamentosCapturados + " @ Safe Haven";
            }

            if (!equipamentos.isEmpty()) {
                StringBuilder infoEquipamentos = new StringBuilder();
                for (Equipment equipamento : equipamentos) {
                    infoEquipamentos.append(equipamento.getInfoComoString());
                }
                return informacaoBase + " | " + infoEquipamentos;
            }
            return informacaoBase;
        }

        if (transformado) {
            equipaNome = "Zombie (Transformado)";
            prefixoEquipamento = "-";
            return id + " | " + nomeTipo + " | " + equipaNome + " | " + nome + " | " + prefixoEquipamento +
                    numeroEquipamentosDestruidos + " @ (" + coluna + ", " + linha + ")";
        }

        if (equipa == ID_EQUIPA_MORTOS) {
            equipaNome = "Zombie";
            prefixoEquipamento = "-";
            return id + " | " + nomeTipo + " | " + equipaNome + " | " + nome + " | " + prefixoEquipamento +
                    numeroEquipamentosDestruidos + " @ (" + coluna + ", " + linha + ")";
        }
        return null;
    }

}