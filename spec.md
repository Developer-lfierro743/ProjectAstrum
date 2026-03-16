code must be changed and must be added:
Phase 1: Foundation (The Skeleton)
   * astrum-common
       * [ ] Add Vector3i utility methods for chunk-local coordinate math.
       * [ ] Implement NativeMemory unit tests to verify off-heap safety.
   * astrum-api
       * [ ] Expand Registry to support Namespaced IDs (e.g., astrum:ferrous).
       * [ ] Create IWorld and IEntity interfaces for modder access.
   * astrum-core
       * [ ] Implement ECSSystemManager to run systems like Physics and AI.
       * [ ] Add MovementSystem to process VelocityComponent and PositionComponent.
   * astrum-engine
       * [ ] Vulkan: Complete createGraphicsPipeline with actual shader modules and fixed-function state.
       * [ ] Vulkan: Implement CommandPool and CommandBuffer allocation logic.
       * [ ] Windowing: Bridge WindowProvider into the main loop in AstrumClient.


  Phase 2: High-Performance Rendering (The Vision)
   * astrum-engine
       * [x] Meshing: Finish addFace logic in ChunkMesher (currently omitted).
       * [ ] Optimization: Implement Greedy Meshing to combine identical voxel faces.
       * [ ] Vulkan: Setup Vertex Buffers and Index Buffers using NativeMemory.
       * [ ] Vulkan: Implement vkCmdDrawIndexed to see the first triangle.
       * [ ] Scaling: Implement the off-screen scaling buffer for Adreno 710 optimization.
       * [ ] Bindless: Implement descriptor indexing for "The Formula's" 10k+ texture requirement.


  Phase 3: World & Generation (The Scale)
   * astrum-world
       * [ ] Implement InfiniteGenerator using the integrated FastNoiseLite.
       * [ ] Concurrency: Setup Java Virtual Threads to generate chunks in the background.
       * [ ] Streaming: Implement logic to load/unload chunks based on player distance (no world borders).
       * [ ] Ores: Add geological distribution logic for Scientific Ores (Ferrous, Aurum, etc.).


  Phase 4: Creative & Social (The Soul)
   * astrum-security
       * [ ] Implement SexualRule.java and AssetScanner.java as hardcoded rules.
       * [ ] Add mod verification logic to block legacy Minecraft modloaders (Forge/Fabric).
   * astrum-core
       * [ ] The Purge: Implement event triggers and temporary "no-rule" state logic.
   * astrum-client
       * [ ] UI: Build the Matte-Vibrant HUD (Vibrant HP bars, XP bars).
       * [ ] Inventory: Implement the 50-slot inventory and 11-slot quickbar (4x4 crafting grid).


  Phase 5: Universal Access (The Outreach)
   * astrum-web
       * [ ] Implement WebWindowProvider using TeaVM DOM APIs.
       * [ ] Create the WASM-GC glue code for the main entry point.
   * astrum-engine
       * [ ] WebGPU: Map Vulkan pipeline logic to WebGPU commands using TeaVM JSO.
   * astrum-server
       * [ ] Protocols: Implement wss:// and ws:// support for browser client connections.