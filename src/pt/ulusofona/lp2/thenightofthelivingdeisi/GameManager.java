package pt.ulusofona.lp2.thenightofthelivingdeisi;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class GameManager {

    static final int NR_COMPONENTES_CRIATURA = 6;
    static final int NR_COMPONENTES_EQUIPMENT = 4;
    private Board tabuleiroJogo;
    private int equipaInicial;
    private int equipaAtual;
    private int turno = 0;
    private boolean dia = true;
    private boolean terminado = false;

    private void resetGameState() {
        tabuleiroJogo = null;
        equipaInicial = 0;
        equipaAtual = 0;
        turno = 0;
        dia = true;
        terminado = false;
    }

    public void loadGame(File file) throws InvalidFileException, FileNotFoundException {
        int currentLine = 0;
        resetGameState();
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) { //Tabuleiro de Jogo
                currentLine++;
                String[] tamanho = scanner.nextLine().split(" ");
                if (tamanho.length == 2) { // linha vem depois da coluna
                    tabuleiroJogo = new Board(Integer.parseInt(tamanho[1]), Integer.parseInt(tamanho[0]));
                } else {
                    throw new InvalidFileException("Erro na linha ->", currentLine);
                }
            }
            if (scanner.hasNextLine()) {// Lê a equipa que começa a jogar
                currentLine++;
                this.equipaAtual = Integer.parseInt(scanner.nextLine());
                this.equipaInicial = this.equipaAtual;
            } else {
                throw new InvalidFileException("Erro na linha ->", currentLine);
            }
            if (scanner.hasNextLine()) { // Criaturas
                int numCriaturas;
                currentLine++;
                try {
                    numCriaturas = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    throw new InvalidFileException("Erro na linha ->", currentLine);
                }
                for (int i = 0; i < numCriaturas; i++) {
                    currentLine++;
                    if (scanner.hasNextLine()) {
                        String[] criaturaInfo = scanner.nextLine().split(" : ");
                        if (!(criaturaInfo.length == NR_COMPONENTES_CRIATURA)) {
                            throw new InvalidFileException("Erro na linha ->", currentLine); // TODO TRANSFORMAR ISTO NA NOSSA PROPRIA EXPETION
                        }
                        try {
                            Creature creature = Creature.criarCreature(criaturaInfo);
                            if (!tabuleiroJogo.adicionarCreatura(creature)) {
                                throw new InvalidFileException("Erro na linha ->", currentLine);
                            }
                        } catch (IllegalArgumentException e) {
                            throw new InvalidFileException("Erro na linha ->", currentLine);
                        }
                    } else {
                        throw new InvalidFileException("Erro na linha ->", currentLine);
                    }
                }
            }
            if (scanner.hasNextLine()) { // Equipamento
                currentLine++;
                int numEquipamentos;
                try {
                    numEquipamentos = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    throw new InvalidFileException("Erro na linha ->", currentLine);
                }
                for (int i = 0; i < numEquipamentos; i++) {
                    currentLine++;
                    if (scanner.hasNextLine()) {
                        String[] equipamentoInfo = scanner.nextLine().split(" : ");
                        if (!(equipamentoInfo.length == NR_COMPONENTES_EQUIPMENT)) {
                            throw new InvalidFileException("Erro na linha ->", currentLine); // TODO TRANSFORMAR ISTO NA NOSSA PROPRIA EXPETION
                        }
                        try {
                            Equipment equip = Equipment.criarEquipamento(equipamentoInfo);
                            if (!tabuleiroJogo.adicionarEquipamento(equip)) {
                                throw new InvalidFileException("Erro na linha ->", currentLine);
                            }
                        } catch (IllegalArgumentException e) {
                            String mensagem = "Erro " + " | " + e.getMessage() + " na linha ->";
                            throw new InvalidFileException(mensagem, currentLine);
                        }
                    } else {
                        throw new InvalidFileException("Erro na linha ->", currentLine);
                    }
                }
            }
            if (scanner.hasNextLine()) { // SafeHeaven
                int numPortas;
                currentLine++;
                try {
                    numPortas = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    throw new InvalidFileException("Número portas inválidas linha ->", currentLine);
                }
                for (int i = 0; i < numPortas; i++) {
                    currentLine++;
                    if (scanner.hasNextLine()) {
                        String[] posicao = scanner.nextLine().split(" : ");
                        int x = Integer.parseInt(posicao[0]);
                        int y = Integer.parseInt(posicao[1]);
                        tabuleiroJogo.adicionarSafeHeaven(new SafeHeaven(y, x));
                    }
                }
            }
        }
    }

    public int[] getWorldSize() {
        return new int[]{tabuleiroJogo.getLinhas(), tabuleiroJogo.getColunas()};
    }

    public int getInitialTeamId() {
        return equipaInicial;
    }

    public int getCurrentTeamId() {
        return equipaAtual;
    }

    public boolean isDay() {
        if (turno % 2 == 0 && turno != 0) {
            dia = !dia;
        }
        return dia;
    }

    public String getSquareInfo(int x, int y) {
        if (tabuleiroJogo.dentroDosLimites(x, y)) {
            return tabuleiroJogo.getSquareInfo(x, y);
        }
        return null;
    }

    public String[] getCreatureInfo(int id) {
        Creature creature = procuraCriaturaPorID(id);
        if (creature != null) {
            return creature.getInfo();
        }
        return new String[NR_COMPONENTES_CRIATURA];
    }

    public String getCreatureInfoAsString(int id) {
        Creature animal = procuraCriaturaPorID(id);
        if (animal != null) {
            return animal.getInfoComoString();
        }
        return null;
    }

    public Creature procuraCriaturaPorID(int criaturaID) {
        return tabuleiroJogo.procuraCriaturaPorID(criaturaID);
    }

    public Equipment procuraEquipamentoPorID(int equipamentoID) {
        return tabuleiroJogo.procuraEquipamentoPorID(equipamentoID);
    }

    public String[] getEquipmentInfo(int id) {
        Equipment equipment = procuraEquipamentoPorID(id);
        if (equipment != null) {
            return equipment.getInfo();
        }
        return null;
    }

    public String getEquipmentInfoAsString(int id) {
        Equipment equipment = procuraEquipamentoPorID(id);
        if (equipment != null) {
            return equipment.getInfoComoString();
        }
        return null;
    }

    public boolean hasEquipment(int creatureId, int equipmentTypeId) {
        Creature creature = procuraCriaturaPorID(creatureId);
        return creature.temEquipamento(equipmentTypeId);
    }

    public boolean move(int xO, int yO, int xD, int yD) {
        Creature criatura = tabuleiroJogo.getCreature(xO, yO);

        if (criatura == null) {
            return false;
        }

        if (criatura.mover(xO, yO, xD, yD, tabuleiroJogo, equipaAtual, turno)) {
            mudaTurno();
            return true;
        }
        return false;
    }

    private void mudaTurno() {
        turno++;
        equipaAtual = (equipaAtual == 20) ? 10 : 20;
    }

    public boolean gameIsOver() {
        if (tabuleiroJogo.getMudancas() == 0) {
            terminado = true;
        }
        if (tabuleiroJogo.soHumanos()) {
            terminado = true;
        }
        if (tabuleiroJogo.soZom()) {
            terminado = true;
        }
        return terminado;
    }

    public ArrayList<String> getSurvivors() {
        ArrayList<String> info = new ArrayList<>();
        ArrayList<Creature> humanos = tabuleiroJogo.getHumanos();
        ArrayList<Creature> zombies = tabuleiroJogo.getZombies();
        info.add("Nr. de turnos terminados:");
        info.add(String.valueOf(turno));
        info.add("");
        info.add("OS VIVOS");

        for (int i = 0; i < humanos.size(); i++) {
            info.add(humanos.get(i).imprimir());
        }
        info.add("");
        info.add("OS OUTROS");
        for (int i = 0; i < zombies.size(); i++) {
            info.add(zombies.get(i).imprimir());
        }
        info.add("-----");
        return info;
    }

    public JPanel getCreditsPanel() {
        return new JPanel();
    }

    public HashMap<String, String> customizeBoard() {
        return new HashMap<>();
    }

    public List<Integer> getIdsInSafeHaven() {
        List<Integer> idsInSafeHaven = new ArrayList<>();
        for (int i = 0; i < tabuleiroJogo.getTabuleiro().length; i++) {
            for (int j = 0; j < tabuleiroJogo.getTabuleiro()[i].length; j++) {
                if (tabuleiroJogo.getTabuleiro()[i][j].getSafeHeaven() != null) {
                    ArrayList<Creature> survivors = tabuleiroJogo.getTabuleiro()[i][j].getSafeHeaven().getSurvivors();
                    if (!survivors.isEmpty()) {
                        for (Creature survivor : survivors) {
                            idsInSafeHaven.add(survivor.getId());
                        }
                    }

                }
            }
        }
        return idsInSafeHaven;
    }

    public void saveGame(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // 1. Dimensões do tabuleiro
            writer.write(tabuleiroJogo.getColunas() + " " + tabuleiroJogo.getLinhas());
            writer.newLine();


            writer.write(String.valueOf(equipaAtual));
            writer.newLine();


            List<Creature> criaturas = tabuleiroJogo.getAllCreatures();
            writer.write(String.valueOf(criaturas.size()));
            writer.newLine();


            for (Creature criatura : criaturas) {
                writer.write(criatura.getId() + " : " +
                        criatura.getEquipa() + " : " +
                        criatura.getTipo() + " : " +
                        criatura.getNome() + " : " +
                        criatura.getColuna() + " : " +
                        criatura.getLinha());
                writer.newLine();
            }

            // 5. Número de equipamentos
            List<Equipment> equipamentos = tabuleiroJogo.getAllEquipments();
            writer.write(String.valueOf(equipamentos.size()));
            writer.newLine();

            // 6. Detalhes dos equipamentos
            for (Equipment equipamento : equipamentos) {
                writer.write(equipamento.getId() + " : " +
                        equipamento.getTipo() + " : " +
                        equipamento.getColuna() + " : " +
                        equipamento.getLinha());
                writer.newLine();
            }

            // 7. Número de Safe Heavens
            List<SafeHeaven> safeHeavens = tabuleiroJogo.getAllSafeHeavens();
            writer.write(String.valueOf(safeHeavens.size()));
            writer.newLine();

            // 8. Detalhes dos Safe Heavens
            for (SafeHeaven safeHeaven : safeHeavens) {
                writer.write(safeHeaven.getColuna() + " : " + safeHeaven.getLinha());
                writer.newLine();
            }
        }
    }
}
