package com.scratchgame.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scratchgame.model.Probabilities;
import com.scratchgame.model.Symbol;
import com.scratchgame.model.WinCombination;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class GameConfig {

    private int columns = 3;
    private int rows = 3;
    private Map<String, Symbol> symbols;
    private Probabilities probabilities;

    @JsonProperty("win_combinations")
    private Map<String, WinCombination> winCombinations;

    public int getColumns() { return columns; }
    public void setColumns(int columns) { this.columns = columns; }

    public int getRows() { return rows; }
    public void setRows(int rows) { this.rows = rows; }

    public Map<String, Symbol> getSymbols() { return symbols; }
    public void setSymbols(Map<String, Symbol> symbols) { this.symbols = symbols; }

    public Probabilities getProbabilities() { return probabilities; }
    public void setProbabilities(Probabilities probabilities) { this.probabilities = probabilities; }

    public Map<String, WinCombination> getWinCombinations() { return winCombinations; }
    public void setWinCombinations(Map<String, WinCombination> winCombinations) { this.winCombinations = winCombinations; }

}
