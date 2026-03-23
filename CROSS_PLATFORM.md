# Project Astrum - Cross-Platform Development Guide

## Universal Platform Support

Project Astrum is designed to run on **all major platforms** using Vulkan as the primary graphics API, with WebGPU for web browsers.

```
┌─────────────────────────────────────────────────────────────┐
│                    Project Astrum                           │
├─────────────────────────────────────────────────────────────┤
│  Windows  │  Linux   │  macOS   │  Android  │   Web        │
│  Vulkan   │  Vulkan  │  MoltenVK│  Vulkan   │  WebGPU     │
│  Win32    │  X11/WL  │  Cocoa   │  NDK      │  Canvas     │
└─────────────────────────────────────────────────────────────┘
```

---

## Platform Support Matrix

| Platform | Graphics API | Surface | Status | Min Version |
|----------|-------------|---------|--------|-------------|
| **Windows 10/11** | Vulkan 1.2+ | Win32 | ✅ Full | Windows 10 |
| **Linux** | Vulkan 1.2+ | X11/Wayland | ✅ Full | Any distro |
| **macOS** | MoltenVK 1.2 | Cocoa | ⚠️ Via MoltenVK | macOS 10.15+ |
| **Android** | Vulkan 1.1+ | ANativeWindow | ⚠️ Needs NDK | Android 10+ |
| **iOS** | Metal (native) | UIKit | ❌ Planned | iOS 14+ |
| **Web** | WebGPU | Canvas | ⚠️ Planned | Any browser |

---

## Building for Each Platform

### Windows 10/11

```bash
# Prerequisites: Vulkan SDK, JDK 21
./gradlew build

# Run
./gradlew run
```

**Required:**
- Vulkan Runtime (from https://vulkan.lunarg.com)
- Java 21+

---

### Linux

```bash
# Install Vulkan drivers
sudo apt install vulkan-tools libvulkan-dev

# Build
./gradlew build

# Run
./gradlew run
```

**Supported:**
- X11 (all distros)
- Wayland (modern distros)

---

### macOS (via MoltenVK)

```bash
# Install MoltenVK
brew install molten-vk

# Build with MoltenVK
./gradlew build -Dorg.lwjgl.vulkan.libname=MoltenVK

# Run
./gradlew run
```

**Notes:**
- MoltenVK translates Vulkan → Metal
- Some Vulkan features not available
- Performance ~80-90% of native Metal

---

### Android

```bash
# Prerequisites: Android NDK, SDK
export ANDROID_NDK=/path/to/ndk
export ANDROID_SDK=/path/to/sdk

# Build APK
./gradlew assembleDebug

# Install on device
adb install build/outputs/apk/debug/astrum-debug.apk
```

**Android-specific:**
- Uses `ANativeWindow` for surface
- Touch input support
- Power management
- ARM64 optimization (Adreno, Mali, PowerVR)

---

### Web (WebGPU)

```bash
# Build with TeaVM (Java → WebAssembly)
./gradlew webBuild

# Output: build/web/astrum.js + astrum.wasm
```

**Requirements:**
- TeaVM compiler
- WebGPU-compatible browser (Chrome 113+, Firefox Nightly)

---

## Renderer Architecture

```
IRenderer (interface)
    │
    ├── VulkanRenderer (Windows, Linux, macOS, Android)
    │   ├── Swapchain (MAILBOX/FIFO present modes)
    │   ├── Depth Buffer (D32_SFLOAT)
    │   └── Dynamic Viewport/Scissor
    │
    └── WebGPURenderer (Web browsers)
        ├── GPUSwapChain
        ├── Depth Texture
        └── Command Encoders
```

### Automatic Platform Detection

```java
// Automatically selects correct renderer
IRenderer renderer = RendererFactory.createRenderer();
renderer.init();

// Check platform
PlatformSurface.Platform platform = PlatformSurface.getPlatform();
System.out.println("Running on: " + platform);

// Platform-specific optimizations
if (platform == PlatformSurface.Platform.ANDROID) {
    // Mobile optimizations
    setRenderDistance(2);
    setVSync(true);
} else if (platform == PlatformSurface.Platform.WEB) {
    // Web optimizations
    setRenderDistance(1);
    enableFrustumCulling(true);
}
```

---

## Platform-Specific Optimizations

### Android (Adreno 710 / Snapdragon)

```java
// In ChunkManager.java
public static final int RENDER_DISTANCE = 2; // Reduced for mobile
public static final int UNLOAD_DISTANCE = 4;

// Use HOST_VISIBLE memory (faster on mobile GPUs)
VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | 
VK_MEMORY_PROPERTY_HOST_COHERENT_BIT
```

### macOS (MoltenVK)

```java
// Use FIFO present mode (more stable)
VK_PRESENT_MODE_FIFO_KHR

// Limit to 2 frames in flight (MoltenVK preference)
MAX_FRAMES_IN_FLIGHT = 2
```

### Web (WebGPU)

```java
// Use smaller chunks for web
CHUNK_SIZE = 16; // Instead of 32

// Aggressive frustum culling
FrustumCuller.enable(true);
```

---

## Input Handling

### Desktop (Keyboard + Mouse)
```java
// GLFW handles this automatically
glfwSetKeyCallback(window, ...);
glfwSetCursorPosCallback(window, ...);
```

### Android (Touch)
```java
// Touch events via GLFW or native Android
glfwSetTouchCallback(window, ...);
```

### Web (Pointer Events)
```java
// WebGPU uses JavaScript pointer events
// TeaVM bridges to Java
```

---

## Known Limitations

| Platform | Limitation | Workaround |
|----------|-----------|------------|
| macOS | No Vulkan 1.3 features | Use Vulkan 1.2 subset |
| Android | MAILBOX mode unreliable | Use FIFO + triple buffer |
| Web | No compute shaders | Use fragment shaders |
| iOS | No Vulkan support | Use Metal backend (planned) |

---

## Testing on Each Platform

### Local Testing
```bash
# Windows/Linux
./gradlew run

# macOS
MVK_CONFIG_LOG_LEVEL=1 ./gradlew run

# Android (emulator)
./gradlew installDebug

# Web (local server)
cd build/web && python3 -m http.server 8080
```

### CI/CD Pipeline
```yaml
# GitHub Actions example
jobs:
  test-windows:
    runs-on: windows-latest
    steps:
      - uses: actions/checkout@v3
      - run: ./gradlew test

  test-linux:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - run: ./gradlew test

  test-macos:
    runs-on: macos-latest
    steps:
      - uses: actions/checkout@v3
      - run: brew install molten-vk
      - run: ./gradlew test
```

---

## Troubleshooting

### Vulkan not found (Linux)
```bash
sudo apt install libvulkan1 mesa-vulkan-drivers
```

### MoltenVK crash (macOS)
```bash
export VK_ICD_FILENAMES=/path/to/MoltenVK_icd.json
export VK_LAYER_PATH=/path/to/MoltenVK_layers
```

### Android black screen
```bash
# Check ANativeWindow creation
adb logcat | grep Astrum
```

### WebGPU not supported
```
Browser must support WebGPU:
- Chrome 113+
- Edge 113+
- Firefox Nightly (flag enabled)
```

---

## Future Platforms

### iOS (Metal)
- Native Metal backend planned
- No MoltenVK overhead
- Full iOS feature support

### Steam Deck (Proton)
- Linux build works via Proton
- Native Linux build preferred

### Consoles
- Not planned (closed platforms)
- Would require platform-specific SDKs

---

**Novusforge Studios** - Building the Future of the Sandbox
