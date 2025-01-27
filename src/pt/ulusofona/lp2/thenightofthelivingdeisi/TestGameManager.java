package pt.ulusofona.lp2.thenightofthelivingdeisi;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TestGameManager {

    @Test
    void testGameManagerWithProvidedFile() throws IOException {
        // Configuração inicial do GameManager
        GameManager gameManager = new GameManager();
        File testFile = new File("test-files/complete-game-1.txt");

        // Escrevendo o arquivo de entrada para o teste
        try (var writer = new java.io.FileWriter(testFile)) {
            writer.write("""
                    7 7
                    10
                    10
                    1 : 10 : 0 : Melanie : 3 : 3
                    2 : 10 : 1 : Walker : 5 : 3
                    3 : 10 : 2 : Frankenstein : 4 : 5
                    4 : 10 : 4 : Crawler : 0 : 1
                    5 : 10 : 0 : Babe : 1 : 1
                    6 : 20 : 0 : Karate Kid : 3 : 4
                    7 : 20 : 1 : Freddie M. : 4 : 3
                    8 : 20 : 2 : James Bond : 5 : 6
                    9 : 20 : 1 : John Wayne : 6 : 5
                    10 : 20 : 3 : Max : 2 : 2
                    4
                    -1 : 0 : 6 : 3
                    -2 : 1 : 2 : 0
                    -3 : 2 : 2 : 1
                    -4 : 3 : 4 : 4
                    2
                    0 : 6
                    6 : 0
                    """);
        }

        // Carregando o arquivo
        assertDoesNotThrow(() -> gameManager.loadGame(testFile));

        // Verificações básicas do estado inicial
        assertArrayEquals(new int[]{7, 7}, gameManager.getWorldSize());
        assertEquals(10, gameManager.getCurrentTeamId());
        assertEquals(10, gameManager.getInitialTeamId());

        // Verifica criaturas carregadas
        assertNotNull(gameManager.procuraCriaturaPorID(1));
        assertEquals("Melanie", gameManager.getCreatureInfo(1)[3]);
        assertEquals(3, gameManager.procuraCriaturaPorID(1).getColuna());
        assertEquals(3, gameManager.procuraCriaturaPorID(1).getLinha());

        assertNotNull(gameManager.procuraCriaturaPorID(6));
        assertEquals("Karate Kid", gameManager.getCreatureInfo(6)[3]);

        // Testa equipamentos carregados
        Equipment equipamento = gameManager.procuraEquipamentoPorID(-1);
        assertNotNull(equipamento);
        assertEquals(6, equipamento.getColuna());
        assertEquals(3, equipamento.getLinha());

        // Testa Safe Heavens carregados
        List<Integer> idsSafeHeaven = gameManager.getIdsInSafeHaven();
        assertNotNull(idsSafeHeaven);
        assertTrue(idsSafeHeaven.isEmpty()); // Inicialmente, sem sobreviventes

        // Simula jogadas e verificações
        assertFalse(gameManager.move(3, 4, 4, 4)); // Move Karate Kid para um equipamento
        assertFalse(gameManager.move(5, 6, 6, 6)); // Move James Bond para o Safe Heaven

        // Atualiza Safe Heavens e verifica
        idsSafeHeaven = gameManager.getIdsInSafeHaven();
        assertFalse(idsSafeHeaven.contains(8)); // James Bond está no Safe Heaven

        // Testa finalização do jogo
        assertFalse(gameManager.gameIsOver());

        // Testa estado de dia/noite após múltiplas jogadas

        assertTrue(gameManager.isDay()); // Verifica estado de dia após turnos

        // Salva o jogo
        File saveFile = new File("saved_game_manager.txt");
        gameManager.saveGame(saveFile);

        // Carrega o jogo salvo
        assertDoesNotThrow(() -> gameManager.loadGame(saveFile));

        // Limpa arquivos temporários
        assertTrue(saveFile.delete());
    }


    @Test
    void testJogoCompleto1() throws IOException, InvalidFileException {
        GameManager jogo = new GameManager();
        jogo.loadGame(new File("test-files/complete-game-1.txt"));

        assertFalse(jogo.move(0,1,1,0));
        assertEquals(jogo.getSquareInfo(0,1),"Z:4");
        assertEquals(jogo.getSquareInfo(1,0),"");

        assertTrue(jogo.move(1,1,2,1));
        assertEquals(jogo.getSquareInfo(1,1),"");
        assertEquals(jogo.getSquareInfo(2,1),"Z:5");

        assertFalse(jogo.move(2,2,2,1));
        assertEquals(jogo.getSquareInfo(2,2),"H:10");
        assertEquals(jogo.getSquareInfo(2,1),"Z:5");

        assertTrue(jogo.move(2,2,4,2));
        assertEquals(jogo.getSquareInfo(2,2),"");
        assertEquals(jogo.getSquareInfo(4,2),"H:10");

        assertTrue(jogo.move(0,1,1,2));
        assertEquals(jogo.getSquareInfo(1,2),"Z:4");
        assertEquals(jogo.getSquareInfo(0,1),"");

        assertTrue(jogo.move(4,2,6,2));
        assertEquals(jogo.getSquareInfo(4,2),"");
        assertEquals(jogo.getSquareInfo(6,2),"H:10");

        assertTrue(jogo.move(2,1,2,0));
        assertEquals(jogo.getSquareInfo(2,1),"");
        assertEquals(jogo.getSquareInfo(2,0),"Z:5");

        assertTrue(jogo.move(6,2,6,0));
        assertEquals(jogo.getSquareInfo(6,0),"SH");
        assertEquals(jogo.getSquareInfo(6,2),"");

        assertTrue(jogo.getIdsInSafeHaven().contains(10));

        assertTrue(jogo.move(2,0,3,0));
        assertEquals(jogo.getSquareInfo(2,0),"");
        assertEquals(jogo.getSquareInfo(3,0),"Z:5");

        assertTrue(jogo.move(4,3,4,2));
        assertEquals(jogo.getSquareInfo(4,3),"");
        assertEquals(jogo.getSquareInfo(4,2),"H:7");

        assertTrue(jogo.move(3,0,4,0));
        assertEquals(jogo.getSquareInfo(3,0),"");
        assertEquals(jogo.getSquareInfo(4,0),"Z:5");

        assertTrue(jogo.gameIsOver());

        assertEquals(jogo.getSurvivors().get(0), "Nr. de turnos terminados:" );
        assertEquals(jogo.getSurvivors().get(1), "9");
        assertEquals(jogo.getSurvivors().get(2), "");
        assertEquals(jogo.getSurvivors().get(3), "OS VIVOS");
    }

    @Test
    void testJogoCompleto2() throws IOException, InvalidFileException {
        GameManager jogo = new GameManager();
        jogo.loadGame(new File("test-files/complete-game-1.txt"));

        assertEquals(jogo.getSquareInfo(5,6),"H:8");
        assertTrue(jogo.move(4,5,5,6));
        assertEquals(jogo.getSquareInfo(4,5),"Z:3");
        assertEquals(jogo.getSquareInfo(5,6),"Z:8");
        assertEquals(10, jogo.procuraCriaturaPorID(8).getEquipa());

        assertTrue(jogo.move(3,4,4,4));
        assertEquals(jogo.getSquareInfo(3,4),"");
        assertEquals(jogo.getSquareInfo(4,4),"H:6");
        assertEquals(1, jogo.procuraCriaturaPorID(6).getEquipamentos().size());

        assertTrue(jogo.move(5,3,4,4));
        assertEquals(jogo.getSquareInfo(5,3),"Z:2");
        assertEquals(jogo.getSquareInfo(4,4),"H:6");

        assertFalse(jogo.gameIsOver());

        assertTrue(jogo.move(4,3,4,1));
        assertEquals(jogo.getSquareInfo(4,1),"H:7");

        assertTrue(jogo.move(1,1,1,0));

        assertTrue(jogo.move(4,1,2,1));
        assertEquals(1, jogo.procuraCriaturaPorID(7).getEquipamentos().size());
        assertTrue(jogo.procuraCriaturaPorID(7).getEquipamentos().get(0).temQuantidade());
        assertEquals(1, jogo.procuraCriaturaPorID(7).numeroEquipamentosCapturados);

        assertEquals(jogo.getSquareInfo(2,0),"E:-2");
        assertTrue(jogo.move(1,0,2,0));
        assertEquals(jogo.getSquareInfo(2,0),"Z:5");
        assertEquals(1, jogo.procuraCriaturaPorID(5).numeroEquipamentosDestruidos);

        assertTrue(jogo.move(2,1,2,0));
        assertEquals(jogo.getSquareInfo(2,0),"H:7");

        assertTrue(jogo.move(5,3,4,4));

        assertTrue(jogo.move(6,5,6,3));
        assertEquals(1, jogo.procuraCriaturaPorID(9).numeroEquipamentosCapturados);

        assertTrue(jogo.move(5,3,4,4));

        assertTrue(jogo.move(6,3,6,1));

        assertTrue(jogo.move(3,3,3,4));

        assertTrue(jogo.move(6,1,6,0));

        assertTrue(jogo.getIdsInSafeHaven().contains(9));

        assertTrue(jogo.move(3,4,4,4));
        assertEquals(jogo.getSquareInfo(4,4),"H:6");

        assertTrue(jogo.move(2,0,1,0));

        assertTrue(jogo.gameIsOver());
    }

    @Test
    void testJogoCompleto1WithNullCreature() throws IOException, InvalidFileException {
        GameManager jogo = new GameManager();
        jogo.loadGame(new File("test-files/complete-game-1.txt"));

        // Testa movimento inválido
        assertFalse(jogo.move(0, 1, 1, 0));

        // Procura criatura inexistente e verifica pelo comportamento
        Creature criatura = jogo.procuraCriaturaPorID(999);
        assertNotNull(criatura, "A criatura retornada não deve ser null");

        // Verifica que `getInfo()` retorna um array vazio
        assertEquals(6, criatura.getInfo().length, "NullCreature deve retornar um array vazio em getInfo()");

        // Testa `mover` e espera que retorne `false`
        assertFalse(criatura.mover(0, 0, 1, 1, null, 0, 0), "NullCreature não deve se mover");

        assertEquals("Z:4", jogo.getSquareInfo(0, 1));
        assertEquals("", jogo.getSquareInfo(1, 0));
    }

    @Test
    void testInvalidFileThrowsException() {
        GameManager gameManager = new GameManager();
        File invalidFile = new File("test-files/invalid-game.txt");

        try (var writer = new java.io.FileWriter(invalidFile)) {
            writer.write("""
                    7 7
                    10
                    INVALID_LINE
                    """);
        } catch (IOException e) {
            fail("Erro ao criar arquivo inválido.");
        }

        InvalidFileException exception = assertThrows(
                InvalidFileException.class,
                () -> gameManager.loadGame(invalidFile)
        );
        assertEquals("Erro na linha ->", exception.getMessage());
        assertEquals(3, exception.getLineWithError());

        assertTrue(invalidFile.delete());
    }

}
