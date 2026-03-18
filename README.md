# Project Astrum

[![Project Status: Active](https://img.shields.io/badge/Project%20Status-Active-green.svg)](https://github.com/Developer-lfierro743/ProjectAstrum)
[![Java Version: 21+](https://img.shields.io/badge/Java-21+-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Vulkan: 1.3](https://img.shields.io/badge/Graphics-Vulkan%20%7C%20WebGPU-red.svg)](https://www.vulkan.org/)

> **"A reclaim of the sandbox vision. Independent, Resilient, and Secure."**

Project Astrum is a next-generation voxel engine and gaming platform designed to transcend the limitations of the modern "Games as a Service" (GaaS) model. It exists to provide a permanent, unyielding foundation for creative freedom, technical excellence, and player safety.

---

## 🏛 Why Project Astrum Exists

The voxel genre began with a promise of infinite worlds and absolute freedom. Over time, corporate acquisitions and "Spaghetti OOP" technical debt have led to stagnation, telemetry-heavy ecosystems, and the erosion of player privacy.

**Project Astrum is built to survive.**
*   **Corporate Resilience**: Built from the ground up with 100% original code and assets. Designed to be legally and technically independent of legacy platforms (Microsoft/Mojang), ensuring the project survives DMCAs and corporate shifts.
*   **The "Clean-Room" Approach**: By using modern standards (Java 21, Vulkan, WASM-GC), we bypass the legal and technical "gravity" of older engines.
*   **Safety as a Core Metric**: Unlike other platforms that rely on invasive AI or reactive reporting, Astrum implements the **SafetyGuardian**—a hardcoded, engine-level security layer designed to protect minors and prevent harmful content (CSAM, grooming) before it can be loaded.

---

## 🌌 The Vision: "The Formula"

### 1. Technical Dominance
*   **GPU-Driven Rendering**: Leveraging Vulkan and WebGPU to move beyond CPU bottlenecks.
*   **Data-Oriented ECS**: Optimized for modern hardware (Threadrippers, RTX GPUs, and the Snapdragon 6 Gen 1/Adreno 710).
*   **Universal Unification**: A single codebase that scales from budget school Chromebooks to high-end workstations.

### 2. Digital Heritage: Legacy Infiniminer
As a tribute to the roots of the genre, Project Astrum includes a full refactor of the original **Infiniminer** source. We are porting the legacy C#/XNA code to Java/Vulkan, integrating it as a core minigame to preserve the history of the voxel movement within a modern, high-performance environment.

### 3. Professional Aesthetics
Moving away from muted, muddy palettes, Astrum employs a **Matte-Vibrant** art style. High-contrast colors and cutting-edge lighting (SSAO, reflections) create a world that feels alive and visually superior to legacy sandboxes.

---

## 📂 Master Module Architecture

| Module | Status | Core Responsibility |
| :--- | :--- | :--- |
| `astrum-api` | 🏗️ | Stable, unified modding API; no more breaking updates. |
| `astrum-core` | 🏗️ | High-performance ECS World and "The Purge" event logic. |
| `astrum-engine` | 🏗️ | Universal Render Bridge (Vulkan & WebGPU) with Hardware Scaling. |
| `astrum-security` | 🏗️ | **SafetyGuardian**: Hardcoded ethics and asset verification. |
| `astrum-world` | 🏗️ | Infinite procedural generation via FastNoiseLite. |
| `astrum-web` | 🏗️ | TeaVM / WASM-GC bridge for instant browser play. |

---

## 🚀 Development Baseline

*   **OpenJDK 21**: Utilizing Virtual Threads (Loom) and the Panama API (FFM).
*   **Gradle 9.3.1**: Modern, modular build infrastructure.
*   **Target Hardware**: Snapdragon 6 Gen 1 (Adreno 710) optimization baseline.

### Quick Start
```bash
git clone https://github.com/Developer-lfierro743/ProjectAstrum.git
cd ProjectAstrum
./gradlew build -x test
```

---

**Novusforge Studios**  
*Building the Future of the Sandbox.*
