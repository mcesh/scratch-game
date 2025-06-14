package com.scratchgame;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scratchgame.config.GameConfig;
import com.scratchgame.model.GameEngine;
import com.scratchgame.model.GameResult;

import java.io.File;
import java.math.BigDecimal;

public class ScratchGameApplication {

    public static void main(String[] args) {
        try {
            String configPath = null;
            BigDecimal bettingAmount = null;

            for (int i = 0; i < args.length; i++) {
                if ("--config".equals(args[i]) && i + 1 < args.length) {
                    configPath = args[i + 1];
                } else if ("--betting-amount".equals(args[i]) && i + 1 < args.length) {
                    bettingAmount = new BigDecimal(args[i + 1]);
                }
            }

            if (configPath == null || bettingAmount == null) {
                System.err.println("Usage: java -jar scratch-game.jar --config config.json --betting-amount 100");
                System.exit(1);
            }

            ObjectMapper mapper = new ObjectMapper();
            GameConfig config = mapper.readValue(new File(configPath), GameConfig.class);

            GameEngine gameEngine = new GameEngine(config);
            GameResult result = gameEngine.play(bettingAmount);

            String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
            System.out.println(jsonResult);

        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
