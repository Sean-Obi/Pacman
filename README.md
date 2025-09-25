# 🟡 Java Pac-Man Game

A classic **Pac-Man** clone built in Java using **Swing** for graphics and input handling. The game includes wall collisions, ghosts with randomized movement, collectible food, cherries with cooldowns, scoring, and a simple game loop.

---

## 📚 Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Controls](#controls)
- [Configuration](#configuration)
- [Project Structure](#project-structure)
- [Dependencies](#dependencies)
- [Known Issues](#known-issues)
- [Troubleshooting](#troubleshooting)
- [Contributors](#contributors)
- [License](#license)

---

## 🧠 Introduction

This project is a simple recreation of the **Pac-Man** arcade game implemented entirely in Java. It uses `javax.swing` for rendering and event handling. The game logic includes:

- Ghost AI (random movement)
- Food and cherry collection
- Score tracking
- Pause and restart capabilities
- Wall and entity collision detection

---

## ✨ Features

- 🎮 Classic Pac-Man mechanics
- 👻 Four ghost types with unique sprites
- 🍒 Randomly spawning cherries with timed cooldown
- 🧱 Maze built using a tile map system
- 🧠 Basic collision detection
- 🕹️ Responsive controls
- ⏸️ Pause/Unpause functionality
- 💥 Game over and restart

---

## 💾 Installation

1. **Clone this repository**:

    ```bash
    git clone https://github.com/your-username/java-pacman.git
    cd java-pacman
    ```

2. **Compile the code**:

    Ensure you have `javac` and `java` installed.

    ```bash
    javac PacMan.java
    ```

3. **Run the game**:

    ```bash
    java PacMan
    ```

---

## ▶️ Usage

Just run the game using `java PacMan`. The game window will open and you can start playing using your keyboard.

Collect all the food to win the level. Avoid the ghosts! Pick up cherries for bonus points.

---

## 🎮 Controls

| Key        | Action              |
|------------|---------------------|
| Arrow Keys | Move Pac-Man        |
| Space      | Pause / Unpause     |

Once the game ends, press any arrow key or spacebar to **restart** the game.

---

## ⚙️ Configuration

All game settings such as tile size, game speed, and frame rate are hardcoded in the class:

- **Tile Size:** `32px`
- **Frame Rate:** 20 FPS (`Timer(50, this)`)
- **Cherry Duration:** 30 seconds
- **Cherry Cooldown:** 10 seconds
- **Cherry Points:** 100

To adjust game difficulty or speed, modify these constants in the source code.

---

## 📁 Project Structure

