package com.scratchgame.model;

import com.scratchgame.config.GameConfig;

import java.math.BigDecimal;
import java.util.*;

public class GameEngine {

    private final GameConfig config;
    private final Random random;

    public GameEngine(GameConfig config) {
        this.config = config;
        this.random = new Random();
    }

    public GameResult play(BigDecimal bettingAmount) {

        String[][] matrix = generateMatrix();

        Map<String, List<String>> appliedWinningCombinations = checkWinningCombinations(matrix);

        BigDecimal baseReward = calculateReward(bettingAmount, appliedWinningCombinations);

        String appliedBonusSymbol = null;
        BigDecimal finalReward = baseReward;

        if (baseReward.compareTo(BigDecimal.ZERO) > 0) {
            appliedBonusSymbol = findBonusSymbol(matrix);
            if (appliedBonusSymbol != null && !appliedBonusSymbol.equals("MISS")) {
                finalReward = applyBonus(baseReward, appliedBonusSymbol);
            }
        }

        return new GameResult(matrix, finalReward, appliedWinningCombinations, appliedBonusSymbol);
    }

    public GameResult play(BigDecimal bettingAmount, String[][] fixedMatrix) {

        Map<String, List<String>> appliedWinningCombinations = checkWinningCombinations(fixedMatrix);

        BigDecimal baseReward = calculateReward(bettingAmount, appliedWinningCombinations);

        String appliedBonusSymbol = null;
        BigDecimal finalReward = baseReward;

        if (baseReward.compareTo(BigDecimal.ZERO) > 0) {
            appliedBonusSymbol = findBonusSymbol(fixedMatrix);
            if (appliedBonusSymbol != null && !appliedBonusSymbol.equals("MISS")) {
                finalReward = applyBonus(baseReward, appliedBonusSymbol);
            }
        }

        return new GameResult(fixedMatrix, finalReward, appliedWinningCombinations, appliedBonusSymbol);
    }

    private String[][] generateMatrix() {
        String[][] matrix = new String[config.getRows()][config.getColumns()];

        for (int row = 0; row < config.getRows(); row++) {
            for (int col = 0; col < config.getColumns(); col++) {
                matrix[row][col] = generateStandardSymbol(row, col);
            }
        }

        if (shouldPlaceBonusSymbol()) {
            int bonusRow = random.nextInt(config.getRows());
            int bonusCol = random.nextInt(config.getColumns());
            matrix[bonusRow][bonusCol] = generateBonusSymbol();
        }

        return matrix;
    }

    private String generateStandardSymbol(int row, int col) {
        Map<String, Integer> probabilities = getCellProbabilities(row, col);
        return selectWeightedRandom(probabilities);
    }

    private Map<String, Integer> getCellProbabilities(int row, int col) {
        for (CellProbability cellProb : config.getProbabilities().getStandardSymbols()) {
            if (cellProb.getRow() == row && cellProb.getColumn() == col) {
                return cellProb.getSymbols();
            }
        }
        // Fallback to first cell's probabilities if not found
        return config.getProbabilities().getStandardSymbols().get(0).getSymbols();
    }

    private boolean shouldPlaceBonusSymbol() {
        return random.nextDouble() < 0.3;
    }

    private String generateBonusSymbol() {
        Map<String, Integer> bonusProbabilities = config.getProbabilities().getBonusSymbols().getSymbols();
        return selectWeightedRandom(bonusProbabilities);
    }

    private String selectWeightedRandom(Map<String, Integer> weights) {
        int totalWeight = weights.values().stream().mapToInt(Integer::intValue).sum();
        int randomValue = random.nextInt(totalWeight);

        int currentWeight = 0;
        for (Map.Entry<String, Integer> entry : weights.entrySet()) {
            currentWeight += entry.getValue();
            if (randomValue < currentWeight) {
                return entry.getKey();
            }
        }

        return weights.keySet().iterator().next();
    }

    private Map<String, List<String>> checkWinningCombinations(String[][] matrix) {
        Map<String, List<String>> winningCombinations = new HashMap<>();

        Map<String, Integer> symbolCounts = countStandardSymbols(matrix);

        checkSameSymbolCombinations(symbolCounts, winningCombinations);

        checkLinearCombinations(matrix, winningCombinations);

        return winningCombinations;
    }

    private Map<String, Integer> countStandardSymbols(String[][] matrix) {
        Map<String, Integer> counts = new HashMap<>();

        for (String[] row : matrix) {
            for (String symbol : row) {
                if (config.getSymbols().get(symbol) != null &&
                        config.getSymbols().get(symbol).isStandard()) {
                    counts.put(symbol, counts.getOrDefault(symbol, 0) + 1);
                }
            }
        }

        return counts;
    }

    private void checkSameSymbolCombinations(Map<String, Integer> symbolCounts,
                                             Map<String, List<String>> winningCombinations) {
        for (Map.Entry<String, Integer> entry : symbolCounts.entrySet()) {
            String symbol = entry.getKey();
            int count = entry.getValue();

            String bestCombination = null;
            int bestCount = 0;

            for (Map.Entry<String, WinCombination> combEntry : config.getWinCombinations().entrySet()) {
                WinCombination combination = combEntry.getValue();
                if ("same_symbols".equals(combination.getWhen()) &&
                        combination.getCount() != null &&
                        combination.getCount() <= count &&
                        combination.getCount() > bestCount) {
                    bestCombination = combEntry.getKey();
                    bestCount = combination.getCount();
                }
            }

            if (bestCombination != null) {
                winningCombinations.computeIfAbsent(symbol, k -> new ArrayList<>()).add(bestCombination);
            }
        }
    }

    private void checkLinearCombinations(String[][] matrix, Map<String, List<String>> winningCombinations) {
        for (Map.Entry<String, WinCombination> combEntry : config.getWinCombinations().entrySet()) {
            WinCombination combination = combEntry.getValue();

            if ("linear_symbols".equals(combination.getWhen()) &&
                    combination.getCoveredAreas() != null) {

                for (List<String> area : combination.getCoveredAreas()) {
                    String firstSymbol = getSymbolAtPosition(matrix, area.get(0));

                    if (firstSymbol != null && config.getSymbols().get(firstSymbol).isStandard()) {
                        boolean allMatch = true;

                        for (String position : area) {
                            if (!firstSymbol.equals(getSymbolAtPosition(matrix, position))) {
                                allMatch = false;
                                break;
                            }
                        }

                        if (allMatch) {
                            winningCombinations.computeIfAbsent(firstSymbol, k -> new ArrayList<>())
                                    .add(combEntry.getKey());
                        }
                    }
                }
            }
        }
    }

    private String getSymbolAtPosition(String[][] matrix, String position) {
        String[] parts = position.split(":");
        int row = Integer.parseInt(parts[0]);
        int col = Integer.parseInt(parts[1]);
        return matrix[row][col];
    }

    private BigDecimal calculateReward(BigDecimal bettingAmount, Map<String, List<String>> appliedWinningCombinations) {
        BigDecimal totalReward = BigDecimal.ZERO;

        for (Map.Entry<String, List<String>> entry : appliedWinningCombinations.entrySet()) {
            String symbol = entry.getKey();
            List<String> combinations = entry.getValue();

            BigDecimal symbolMultiplier = config.getSymbols().get(symbol).getRewardMultiplier();
            BigDecimal combinationMultiplier = BigDecimal.ONE;

            for (String combination : combinations) {
                combinationMultiplier = combinationMultiplier.multiply(
                        config.getWinCombinations().get(combination).getRewardMultiplier());
            }

            BigDecimal symbolReward = bettingAmount.multiply(symbolMultiplier).multiply(combinationMultiplier);
            totalReward = totalReward.add(symbolReward);
        }

        return totalReward;
    }

    private String findBonusSymbol(String[][] matrix) {
        for (String[] row : matrix) {
            for (String symbol : row) {
                if (config.getSymbols().get(symbol) != null &&
                        config.getSymbols().get(symbol).isBonus()) {
                    return symbol;
                }
            }
        }
        return null;
    }

    private BigDecimal applyBonus(BigDecimal baseReward, String bonusSymbol) {
        Symbol bonus = config.getSymbols().get(bonusSymbol);

        if ("multiply_reward".equals(bonus.getImpact())) {
            return baseReward.multiply(bonus.getRewardMultiplier());
        } else if ("extra_bonus".equals(bonus.getImpact())) {
            return baseReward.add(bonus.getExtra());
        }

        return baseReward;
    }
}
