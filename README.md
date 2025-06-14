# 🎰 Scratch Game Simulator

A Java-based scratch card game engine that simulates a 2D grid matrix filled with symbols, calculates rewards based on matching patterns, and applies bonus effects. This CLI tool is designed to be fully configurable via a JSON config file.

---

## 🧩 Features

- Configurable symbol rewards and bonus effects  
- Grid-based game logic with vertical, horizontal, and diagonal win conditions  
- Support for bonus multipliers and extra reward symbols  
- JSON-based output suitable for frontend or automation  
- Fully testable with JUnit 5  

---

## 🚀 Getting Started

### ✅ Prerequisites

- Java 17 or later  
- Maven 3.6+  

---

### 🔧 Build Instructions

```bash
mvn clean package
```

### 🕹️ Usage

```java -jar target/scratch-game.jar --config config.json --betting-amount 100```

### 📈 Output

```{
  "matrix": [
    ["A", "A", "B"],
    ["A", "+1000", "B"],
    ["A", "A", "B"]
  ],
  "reward": 3600,
  "applied_winning_combinations": {
    "A": ["same_symbol_5_times", "same_symbols_vertically"],
    "B": ["same_symbol_3_times", "same_symbols_vertically"]
  },
  "applied_bonus_symbol": "+1000"
}
```

### 🗂️ Project Structure

```.
├── src
│   ├── main
│   │   └── java
│   │       └── com.scratchgame
│   │           ├── ScratchGameApplication.java
│   │           ├── GameEngine.java
│   │           ├── GameResult.java
│   │           ├── GameConfig.java
│   │           └── models/...
│   └── test
│       └── java
│         └── ScratchGameTest.java
├── config.json
├── pom.xml
└── README.md
```

### 🧑‍💻 Author

```
Siyamcela Nxuseka
Java Developer | Backend | Cloud | DevOps
📍 South Africa
```
