package pt.ulusofona.lp2.thenightofthelivingdeisi;

import java.util.ArrayList;

public class Idoso extends Creature {
    public Idoso(int id, int equipa, int tipo, String nome, int coluna, int linha) {
        super(id, equipa, tipo, nome, coluna, linha, "Idoso");
        this.equipamentos = new ArrayList<>();
        this.numeroEquipamentosDestruidos = 0;
    }

    @Override
    public boolean mover(int xO, int yO, int xD, int yD, Board tabuleiroJogo, int equipaAtual, int turno) {
        if (equipa != equipaAtual) {
            return false; // Movimento permitido apenas para a equipa atual
        }
        tabuleiroJogo.decrementarMudancas();
        if (equipa == ID_EQUIPA_HUMANOS) {
            return moverHumano(xO, yO, xD, yD, tabuleiroJogo, turno);
        }
        return moverZombie(xO, yO, xD, yD, tabuleiroJogo);
    }

    // Movimento dos Humanos
    private boolean moverHumano(int xO, int yO, int xD, int yD, Board tabuleiroJogo, int turno) {
        if (!tabuleiroJogo.dentroDosLimites(xD, yD) || !isDay(turno)) {
            return false; // Movimento fora dos limites ou durante a noite
        }
        if (!movimentoDiagonalValido(xO, yO, xD, yD)) {
            return false; // Movimento não é diagonal de 1 casa
        }

        if (tabuleiroJogo.temSafeHeaven(xD, yD)) {
            moverParaSafeHeaven(xO, yO, xD, yD, tabuleiroJogo);
            return true;
        }

        if (tabuleiroJogo.temEquipment(xD, yD)) {
            pegarEquipamento(xO, yO, xD, yD, tabuleiroJogo);
            return true;
        }

        if (!tabuleiroJogo.tileOcupado(xD, yD)) {
            moverParaTileVazio(xO, yO, xD, yD, tabuleiroJogo);
            return true;
        }
        return false;
    }

    // Movimento dos Zombies
    private boolean moverZombie(int xO, int yO, int xD, int yD, Board tabuleiroJogo) {
        if (!tabuleiroJogo.dentroDosLimites(xD, yD) || tabuleiroJogo.temSafeHeaven(xD, yD)) {
            return false; // Movimento fora dos limites ou em Safe Haven
        }
        if (!movimentoDiagonalValido(xO, yO, xD, yD)) {
            return false; // Movimento não é diagonal de 1 casa
        }

        if (tabuleiroJogo.temEquipment(xD, yD)) {
            destruirEquipamento(xO, yO, xD, yD, tabuleiroJogo);
            return true;
        }

        if (tabuleiroJogo.temCreature(xD, yD)) {
            return atacarHumano(xD, yD, tabuleiroJogo);
        }

        if (!tabuleiroJogo.tileOcupado(xD, yD)) {
            moverParaTileVazio(xO, yO, xD, yD, tabuleiroJogo);
            return true;
        }
        return false;
    }

    // Verifica se o movimento é diagonal
    private boolean movimentoDiagonalValido(int xO, int yO, int xD, int yD) {
        return Math.abs(xO - xD) == 1 && Math.abs(yO - yD) == 1;
    }

    private void moverParaSafeHeaven(int xO, int yO, int xD, int yD, Board tabuleiroJogo) {
        tabuleiroJogo.adicionaHumanoSafeHeaven(this, xD, yD);
        tabuleiroJogo.removeCreature(this, xO, yO);
    }

    private void pegarEquipamento(int xO, int yO, int xD, int yD, Board tabuleiroJogo) {
        Equipment novoEquipamento = tabuleiroJogo.getEquipment(xD, yD);
        deixarEquipamentoAtual(xO, yO, tabuleiroJogo);

        equipamentos.add(novoEquipamento);
        numeroEquipamentosCapturados++;
        tabuleiroJogo.getTabuleiro()[xD][yD].setEquipment(null);
        atualizarPosicao(xO, yO, xD, yD, tabuleiroJogo);
    }

    private void destruirEquipamento(int xO, int yO, int xD, int yD, Board tabuleiroJogo) {
        tabuleiroJogo.getTabuleiro()[xD][yD].setEquipment(null);
        atualizarPosicao(xO, yO, xD, yD, tabuleiroJogo);
        numeroEquipamentosDestruidos++;
    }

    private boolean atacarHumano(int xD, int yD, Board tabuleiroJogo) {
        Creature humano = tabuleiroJogo.getCreature(xD, yD);
        if (humano.getEquipa() != ID_EQUIPA_MORTOS && !humano.getNomeTipo().equals("Cão")) {
            if (!humano.equipamentos.isEmpty()) {
                Equipment equipamento = humano.equipamentos.get(0);
                if (!equipamento.temQuantidade()) {
                    humano.equipamentos.remove(equipamento);
                    humano.equipa = ID_EQUIPA_MORTOS;
                    humano.transformado = true;
                    humano.numeroEquipamentosDestruidos = humano.numeroEquipamentosCapturados;
                    tabuleiroJogo.resetMudancas();
                }
                equipamento.usar();
                return true;
            }
            humano.transformado = true;
            humano.equipa = ID_EQUIPA_MORTOS;
            tabuleiroJogo.resetMudancas();
            return true;
        }
        return false;
    }

    private void moverParaTileVazio(int xO, int yO, int xD, int yD, Board tabuleiroJogo) {
        deixarEquipamentoAtual(xO, yO, tabuleiroJogo);
        atualizarPosicao(xO, yO, xD, yD, tabuleiroJogo);
    }

    private void deixarEquipamentoAtual(int xO, int yO, Board tabuleiroJogo) {
        if (!equipamentos.isEmpty()) {
            Equipment equipamentoAtual = equipamentos.remove(0);
            equipamentoAtual.linha = linha;
            equipamentoAtual.coluna = coluna;
            tabuleiroJogo.getTabuleiro()[xO][yO].setEquipment(equipamentoAtual);
        }
    }

    private void atualizarPosicao(int xO, int yO, int xD, int yD, Board tabuleiroJogo) {
        tabuleiroJogo.getTabuleiro()[xD][yD].setCreature(this);
        tabuleiroJogo.getTabuleiro()[xO][yO].setCreature(null);
        this.coluna = xD;
        this.linha = yD;
    }

    private boolean isDay(int turno) {
        return (turno / 2) % 2 == 0;
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
