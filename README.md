# 🐝 Bee Colony Simulation with JADE

A multi-agent simulation of a bee colony using JADE (Java Agent DEvelopment Framework). Watch as intelligent bee agents collect nectar from flowers and return to their hive in a beautiful, real-time GUI.

## 📋 Overview

This project demonstrates multi-agent systems and autonomous behavior using JADE. The simulation features:

- **Bee Agents**: Autonomously search for flowers, collect nectar, and return to the hive
- **Flower Agents**: Provide nectar that regenerates over time
- **Hive Agent**: Coordinates the colony and stores collected honey
- **GUI Agent**: Displays the simulation in real-time with visual feedback

## ✨ Features

- 🐝 **Autonomous Bee Behavior**: Bees independently navigate, collect nectar, and return to the hive
- 🌸 **Dynamic Flower System**: Flowers regenerate nectar over time (1 nectar every 2 seconds)
- 🏠 **Smart Hive Management**: Hive capacity scales with the number of flowers (10 honey per flower)
- 🎨 **Real-time Visualization**: Beautiful GUI showing bees, flowers, and the hive with status indicators
- 📡 **Agent Communication**: Agents use ACL (Agent Communication Language) messages via JADE
- 🎯 **Mission-based Gameplay**: Simulation completes when the hive reaches maximum capacity

## 🚀 Getting Started

### Prerequisites

- **Java JDK 8 or higher**
- **JADE Framework** (Java Agent DEvelopment Framework)
  - Download from: [https://jade.tilab.com/](https://jade.tilab.com/)
  - Or use Maven dependency

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/bee-colony-simulation.git
   cd bee-colony-simulation
   ```

2. **Add JADE to your classpath**
   
   **Option A: Manual Setup**
   - Download JADE from the official website
   - Add `jade.jar` to your project's classpath

   **Option B: Maven**
   ```xml
   <dependency>
       <groupId>com.tilab.jade</groupId>
       <artifactId>jade</artifactId>
       <version>4.5.0</version>
   </dependency>
   ```

3. **Compile the project**
   ```bash
   javac -cp ".:jade.jar" *.java
   ```
   
   On Windows:
   ```bash
   javac -cp ".;jade.jar" *.java
   ```

### Running the Simulation

**Linux/Mac:**
```bash
java -cp ".:jade.jar" StartSimulation
```

**Windows:**
```bash
java -cp ".;jade.jar" StartSimulation
```

## 🎮 How It Works

### Agent Architecture

#### BeeAgent
- Starts at the hive location (400, 300)
- Learns about flower locations from the hive
- Navigates to flowers and collects nectar (max capacity: 5)
- Returns to hive when full
- Stops collection when hive is full

#### FlowerAgent
- Positioned at fixed locations across the field
- Starts with 10 nectar units
- Regenerates 1 nectar every 2 seconds
- Provides nectar to bees upon request

#### HiveAgent
- Central coordination point at (400, 300)
- Registers new bees and flowers
- Broadcasts flower locations to all bees
- Collects deposited nectar
- Maximum capacity: 10 × number of flowers
- Broadcasts stop signal when full

#### GUIAgent
- Visualizes all agents in real-time
- Shows bee status (searching, carrying, returning)
- Displays nectar/honey levels
- Updates status bar with mission progress

### Communication Protocol

Agents communicate using JADE's ACL messages:

- `BEE_READY`: Bee announces its presence to the hive
- `FLOWER:name:x:y`: Hive shares flower locations with bees
- `TAKE1`: Bee requests nectar from a flower
- `DEPOSIT:amount`: Bee deposits nectar at the hive
- `STOP_COLLECTION`: Hive signals mission completion
- `HIVE_FULL`: Hive rejects deposits when at capacity

## 🎯 Simulation Parameters

| Parameter | Value | Description |
|-----------|-------|-------------|
| Bee Count | 5 | Number of bee agents |
| Flower Count | 6 | Number of flower agents |
| Bee Max Nectar | 5 | Maximum nectar a bee can carry |
| Flower Max Nectar | 10 | Maximum nectar per flower |
| Hive Capacity | 60 | 10 × number of flowers |
| Collection Range | 40 pixels | Distance for nectar collection |
| Hive Range | 50 pixels | Distance for nectar deposit |
| Collection Cooldown | 1000ms | Time between collections |
| Regeneration Rate | 2000ms | Flower nectar regeneration |

## 🎨 Visual Indicators

- **Yellow Hexagon**: The hive
- **Yellow Bees**: Different shades indicate status
  - Light yellow: Searching for flowers
  - Medium yellow: Carrying nectar
  - Golden: Returning to hive
- **Pink Flowers**: Color intensity shows nectar level
  - Bright pink: High nectar (7-10)
  - Light pink: Medium nectar (4-6)
  - Gray: Low/empty nectar (0-3)

## 🔧 Customization

### Adding More Bees
In `StartSimulation.java`, modify the loop:
```java
for (int i = 1; i <= 10; i++) {  // Change to desired number
    container.createNewAgent("bee" + i, "BeeAgent", null).start();
    Thread.sleep(200);
}
```

### Adding More Flowers
Add more flower agents in `StartSimulation.java`:
```java
container.createNewAgent("flower7", "FlowerAgent", null).start();
container.createNewAgent("flower8", "FlowerAgent", null).start();
```

For flowers beyond `flower6`, random positions will be assigned.

### Adjusting Speed
Modify the `TickerBehaviour` timers in each agent:
- Bee movement: Change `200` in `BeeAgent`
- Flower regeneration: Change `2000` in `FlowerAgent`
- Status updates: Change `1000` in `HiveAgent` and `GUIAgent`

## 📚 Project Structure

```
bee-colony-simulation/
├── BeeAgent.java           # Autonomous bee behavior
├── FlowerAgent.java        # Flower nectar management
├── HiveAgent.java          # Colony coordination
├── GUIAgent.java           # Visualization
├── StartSimulation.java    # Entry point
└── README.md              # This file
```

## 🐛 Troubleshooting

**"NoClassDefFoundError: jade/core/Agent"**
- Ensure JADE JAR is in your classpath
- Verify the path to jade.jar is correct

**Agents not moving**
- Check that all agents are starting properly in the console
- Ensure sufficient flowers are spawned for bees to target

**GUI not appearing**
- Verify GUIAgent starts first (before other agents)
- Check for AWT/Swing compatibility on your system

## 🤝 Contributing

Contributions are welcome! Feel free to:
- Add new agent behaviors
- Enhance the GUI
- Implement new features (weather, predators, multiple hives)
- Optimize communication protocols

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Built with [JADE Framework](https://jade.tilab.com/)
- Inspired by natural bee colony behavior
- Created as a demonstration of multi-agent systems

---

**Happy simulating! 🐝🌸**
