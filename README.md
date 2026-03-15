# Project Astrum

[![Project Status: Active](https://img.shields.io/badge/Project%20Status-Active-green.svg)](https://github.com/Developer-lfierro743/ProjectAstrum)
[![Java Version: 21+](https://img.shields.io/badge/Java-21+-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Gradle Version: 9.3.1](https://img.shields.io/badge/Gradle-9.3.1-blue.svg)](https://gradle.org/releases/)

> **"More ambitious than Minecraft. More independent than Hytale."**

Project Astrum is a next-generation sandbox engine and game built from the ground up to reclaim the original ambitious vision of the voxel genre. Designed for the 2026 baseline, it eliminates the technical debt of legacy "spaghetti OOP code" by employing a modern, high-performance architecture centered around **The Formula**.

---

## 🌌 The Vision: "The Formula"

Project Astrum is not just another voxel game; it is a technical and conceptual blueprint designed to deliver unlimited freedom, cutting-edge performance, and a hardcoded ethical environment.

### Core Pillars
*   **Performance First**: Engineered with a custom **Entity Component System (ECS)** and **Vulkan** backend to harness the full power of modern multi-core CPUs (Threadripper-class) and high-end GPUs.
*   **Unlimited Scale**: No world borders. No building height limits. Infinite terrain generation with zero overhead.
*   **Social Simulation**: Native support for massive 100-player social experiments, inspired by the "Purge" and simulation meta.
*   **Universal Access**: A unified codebase that delivers native performance on Desktop and Mobile, with **Instant Web Play** via WASM-GC and WebGPU (powered by TeaVM).
*   **SafetyGuardian**: A hardcoded, multi-layered security engine designed to protect players and prevent harmful content (CSAM, grooming) at the core level.

---

## 🛠 Tech Stack (2026 Baseline)

*   **Language**: Java 21+ (utilizing Virtual Threads and the Panama API for native interop).
*   **Build System**: Gradle 9.3.1 (Multiproject architecture).
*   **Graphics API**: Vulkan (Desktop/Mobile) & WebGPU (Web).
*   **Architecture**: High-performance ECS optimized for 100k+ entities at 60FPS.
*   **World Gen**: Optimized 1D byte array chunk storage (32x32x32) using **FastNoiseLite**.
*   **Web Target**: TeaVM 0.11+ (AOT compilation to WASM-GC).

---

## ✨ Key Features

*   **Scientific Ores**: Replaced non-scientific names with real equivalents (e.g., *Ferrous*, *Aurum*, *Cuprous*).
*   **Greenstone**: The high-performance, unified counterpart to Redstone.
*   **Matte-Vibrant Aesthetics**: A unique art style designed for high-contrast, vibrant visuals that outshine muted legacy palettes.
*   **Tiered Progression**: A circular gameplay loop: Basic → Advanced → Overpowered.
*   **Baked-in Modding API**: A stable, unified API designed for "Universal Unification"—never suffer from breaking updates again.
*   **Zero Microtransactions**: A free, community-driven marketplace for skins and assets.

---

## 📂 Project Structure

Project Astrum is organized into specialized modules for maximum maintainability:

| Module | Purpose |
| :--- | :--- |
| `astrum-api` | Stable modding API and unified registry system. |
| `astrum-common` | Shared data structures, scientific ores, and math (JOML). |
| `astrum-core` | ECS World manager, entity systems, and "The Purge" logic. |
| `astrum-engine` | Rendering backend (Vulkan/WebGPU) and mesh management. |
| `astrum-world` | Procedural infinite world generation (FastNoiseLite). |
| `astrum-security` | The **SafetyGuardian** rule engine and mod verifier. |
| `astrum-client` | Game client entry point, input, and UI systems. |
| `astrum-server` | Headless, high-performance server software. |
| `astrum-web` | WASM-GC and WebGPU glue for browser-based play. |

---

## 🚀 Getting Started

### Prerequisites
- **OpenJDK 21**
- **Gradle 9.3.1**

### Building from Source
```bash
# Clone the repository
git clone https://github.com/Developer-lfierro743/ProjectAstrum.git

# Navigate to directory
cd ProjectAstrum

# Build all modules
./gradlew build -x test
```

### Running the Client
```bash
./gradlew :astrum-client:run
```

---

## 🛡 Ethics & Privacy
*   **Privacy First**: No telemetry or censorship in single-player worlds.
*   **Transparent Security**: The **SafetyGuardian** is a hardcoded set of rules designed solely for player safety and ethical modding.

---

**Novusforge Studios**  
*Building the Future of the Sandbox.*
