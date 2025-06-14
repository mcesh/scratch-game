import com.fasterxml.jackson.databind.ObjectMapper;
import com.scratchgame.config.GameConfig;
import com.scratchgame.model.GameEngine;
import com.scratchgame.model.GameResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ScratchGameTest {

    private GameConfig config;
    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() throws Exception {
        mapper = new ObjectMapper();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.json")) {
            if (is == null) {
                throw new FileNotFoundException("config.json not found in test resources");
            }
            config = mapper.readValue(is, GameConfig.class);
            System.out.println("CONFIG: " + config);
        }
    }

    @Test
    public void testWinningScenarioWithBonusAndMultipleCombinations() {
        GameEngine engine = new GameEngine(config);
        BigDecimal bet = new BigDecimal("100");

        String[][] matrix = {
                {"A", "A", "B"},
                {"A", "+1000", "B"},
                {"A", "A", "B"}
        };

        GameResult result = engine.play(bet, matrix);

        assertEquals(new BigDecimal("3600"), result.getReward());
        assertEquals("+1000", result.getAppliedBonusSymbol());

        Map<String, List<String>> winCombos = result.getAppliedWinningCombinations();
        assertTrue(winCombos.containsKey("A"));
        assertTrue(winCombos.get("A").contains("same_symbol_5_times"));
        assertTrue(winCombos.get("A").contains("same_symbols_vertically"));
        assertTrue(winCombos.containsKey("B"));
        assertTrue(winCombos.get("B").contains("same_symbol_3_times"));
        assertTrue(winCombos.get("B").contains("same_symbols_vertically"));
    }

    @Test
    public void testWinningWith10xBonusMultiplier() {
        GameEngine engine = new GameEngine(config);
        BigDecimal bet = new BigDecimal("100");

        String[][] matrix = {
                {"A", "B", "C"},
                {"E", "B", "10x"},
                {"F", "D", "B"}
        };

        GameResult result = engine.play(bet, matrix);

        assertEquals(new BigDecimal("3000"), result.getReward());
        assertEquals("10x", result.getAppliedBonusSymbol());
        assertTrue(result.getAppliedWinningCombinations().containsKey("B"));
    }

    @Test
    public void testLostGameReturnsZeroReward() {
        GameEngine engine = new GameEngine(config);
        BigDecimal bet = new BigDecimal("100");

        String[][] matrix = {
                {"A", "B", "C"},
                {"E", "B", "5x"},
                {"F", "D", "C"}
        };

        GameResult result = engine.play(bet, matrix);

        assertEquals(BigDecimal.ZERO, result.getReward());
        assertNull(result.getAppliedBonusSymbol());
        assertTrue(result.getAppliedWinningCombinations().isEmpty());
    }

    @Test
    public void testInvalidSymbolThrowsException() {
        GameEngine engine = new GameEngine(config);
        BigDecimal bet = new BigDecimal("100");

        String[][] matrix = {
                {"A", "Z", "C"},
                {"E", "B", "F"},
                {"F", "D", "C"}
        };

        assertThrows(NullPointerException.class, () -> engine.play(bet, matrix));
    }
}
