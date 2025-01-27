package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class Crianca extends Creature {
    public Crianca(int id, int equipa, int tipo, String nome, int coluna, int linha) {
        super(id, equipa, tipo, nome, coluna, linha, "Criança");
    }

    @Override
    public boolean mover(int xO, int yO, int xD, int yD, Board tabuleiroJogo, int equipaAtual, int turno) {
        if (equipa != equipaAtual) {
            return false;
        }
        tabuleiroJogo.decrementarMudancas();
        return (equipa == ID_EQUIPA_HUMANOS) ? moverHumano(xO, yO, xD, yD, tabuleiroJogo) : moverZombie(xO, yO, xD, yD, tabuleiroJogo);
    }

    // ------------------------------------------
    // Movimentos Humanos
    // ------------------------------------------
    private boolean moverHumano(int xO, int yO, int xD, int yD, Board tabuleiroJogo) {
        if (!movimentoValido(xD, yD, tabuleiroJogo)) {
            return false;
        }

        if (tabuleiroJogo.temSafeHeaven(xD, yD)) {
            return entrarSafeHeaven(xO, yO, xD, yD, tabuleiroJogo);
        }

        if (tabuleiroJogo.temEquipment(xD, yD)) {
            return pegarEquipamento(xO, yO, xD, yD, tabuleiroJogo);
        }

        return moverParaTileLivre(xO, yO, xD, yD, tabuleiroJogo);
    }

    private boolean entrarSafeHeaven(int xO, int yO, int xD, int yD, Board tabuleiroJogo) {
        devolverEquipamentoSeNecessario(xO, yO);
        tabuleiroJogo.adicionaHumanoSafeHeaven(this, xD, yD);
        tabuleiroJogo.removeCreature(this, xO, yO);
        return true;
    }

    private boolean pegarEquipamento(int xO, int yO, int xD, int yD, Board tabuleiroJogo) {
        Equipment equipamentoDestino = tabuleiroJogo.getEquipment(xD, yD);
        if (equipamentoDestinoNaoPermitido(equipamentoDestino)) {
            return false;
        }
        devolverEquipamentoSeNecessario(xO, yO);
        armazenarNovoEquipamento(equipamentoDestino, xO, yO, xD, yD, tabuleiroJogo);
        return true;
    }

    private boolean movimentoValido(int xD, int yD, Board tabuleiroJogo) {
        return tabuleiroJogo.dentroDosLimites(xD, yD) &&
                Math.abs(this.coluna - xD) <= 1 && Math.abs(this.linha - yD) <= 1 &&
                (this.coluna == xD || this.linha == yD);
    }

    private void devolverEquipamentoSeNecessario(int xO, int yO) {
        if (!equipamentos.isEmpty()) {
            Equipment equipamentoAtual = equipamentos.get(0);
            equipamentoAtual.linha = yO;
            equipamentoAtual.coluna = xO;
        }
    }

    private boolean equipamentoDestinoNaoPermitido(Equipment equipamentoDestino) {
        return equipamentoDestino.getTipo() == Equipment.TIPO_PISTOLA ||
                equipamentoDestino.getTipo() == Equipment.TIPO_ESPADA;
    }

    private void armazenarNovoEquipamento(Equipment equipamentoDestino, int xO, int yO, int xD, int yD, Board tabuleiroJogo) {
        if (!equipamentos.isEmpty()) {
            Equipment equipamentoAtual = equipamentos.remove(0);
            tabuleiroJogo.getTabuleiro()[xO][yO].setEquipment(equipamentoAtual);
        }
        equipamentos.add(equipamentoDestino);
        numeroEquipamentosCapturados++;
        atualizarTabuleiroEquipamento(equipamentoDestino, xO, yO, xD, yD, tabuleiroJogo);
    }

    private void atualizarTabuleiroEquipamento(Equipment equipamentoDestino, int xO, int yO, int xD, int yD, Board tabuleiroJogo) {
        tabuleiroJogo.getTabuleiro()[xD][yD].setEquipment(null);
        tabuleiroJogo.getTabuleiro()[xD][yD].setCreature(this);
        tabuleiroJogo.getTabuleiro()[xO][yO].setCreature(null);
        this.coluna = xD;
        this.linha = yD;
    }

    private boolean moverParaTileLivre(int xO, int yO, int xD, int yD, Board tabuleiroJogo) {
        if (tabuleiroJogo.tileOcupado(xD, yD)) {
            return false;
        }
        tabuleiroJogo.getTabuleiro()[xD][yD].setCreature(this);
        atualizarPosicaoEquipamento(xD, yD);
        tabuleiroJogo.getTabuleiro()[xO][yO].setCreature(null);
        this.coluna = xD;
        this.linha = yD;
        return true;
    }

    private void atualizarPosicaoEquipamento(int xD, int yD) {
        if (!equipamentos.isEmpty()) {
            Equipment equipamentoAtual = equipamentos.get(0);
            equipamentoAtual.linha = yD;
            equipamentoAtual.coluna = xD;
        }
    }

    // ------------------------------------------
    // Movimentos Zumbis
    // ------------------------------------------
    private boolean moverZombie(int xO, int yO, int xD, int yD, Board tabuleiroJogo) {
        if (!movimentoValido(xD, yD, tabuleiroJogo) || tabuleiroJogo.temSafeHeaven(xD, yD)) {
            return false;
        }

        if (tabuleiroJogo.temEquipment(xD, yD)) {
            destruirEquipamento(xO, yO, xD, yD, tabuleiroJogo);
            return true;
        }

        if (tabuleiroJogo.temCreature(xD, yD)) {
            return atacarHumano(xD, yD, tabuleiroJogo);
        }

        return moverParaTileLivre(xO, yO, xD, yD, tabuleiroJogo);
    }

    private void destruirEquipamento(int xO, int yO, int xD, int yD, Board tabuleiroJogo) {
        tabuleiroJogo.getTabuleiro()[xD][yD].setEquipment(null);
        tabuleiroJogo.getTabuleiro()[xO][yO].setCreature(null);
        tabuleiroJogo.getTabuleiro()[xD][yD].setCreature(this);
        this.coluna = xD;
        this.linha = yD;
        numeroEquipamentosDestruidos++;
    }

    private boolean atacarHumano(int xD, int yD, Board tabuleiroJogo) {
        Creature humano = tabuleiroJogo.getCreature(xD, yD);
        if (humano.getEquipa() != ID_EQUIPA_MORTOS && !humano.getNomeTipo().equals("Cão")) {
            return processarAtaqueHumano(humano, tabuleiroJogo);
        }
        return false;
    }

    private boolean processarAtaqueHumano(Creature humano, Board tabuleiroJogo) {
        if (!humano.equipamentos.isEmpty()) {
            Equipment equipamento = humano.equipamentos.get(0);
            if (!equipamento.temQuantidade()) {
                transformarHumanoEmZombie(humano, tabuleiroJogo);
            }
            equipamento.usar();
            return true;
        }
        transformarHumanoEmZombie(humano, tabuleiroJogo);
        return true;
    }

    private void transformarHumanoEmZombie(Creature humano, Board tabuleiroJogo) {
        humano.equipa = ID_EQUIPA_MORTOS;
        humano.transformado = true;
        humano.numeroEquipamentosDestruidos = humano.numeroEquipamentosCapturados;
        tabuleiroJogo.resetMudancas();
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
