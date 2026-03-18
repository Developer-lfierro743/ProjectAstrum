package com.novusforge.astrum.infiniminer;

import com.novusforge.astrum.infiniminer.engines.*;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.*;

/**
 * PropertyBag - Central state container for Infiniminer.
 * Ported from C# XNA to Java 21 LWJGL3.
 */
public class PropertyBag {
    // Game engines
    public BlockEngine blockEngine = null;
    public InterfaceEngine interfaceEngine = null;
    public PlayerEngine playerEngine = null;
    public SkyplaneEngine skyplaneEngine = null;
    public ParticleEngine particleEngine = null;

    // Player variables
    public Camera playerCamera = null;
    public Vector3f playerPosition = new Vector3f(0, 30, 0); // Default spawn
    public Vector3f playerVelocity = new Vector3f(0, 0, 0);
    public PlayerClass playerClass = PlayerClass.Miner;
    public PlayerTools[] playerTools = new PlayerTools[]{PlayerTools.Pickaxe};
    public int playerToolSelected = 0;
    public BlockType[] playerBlocks = new BlockType[]{BlockType.None};
    public int playerBlockSelected = 0;
    public PlayerTeam playerTeam = PlayerTeam.Red;
    public boolean playerDead = false;
    public int playerOre = 0;
    public int playerCash = 0;
    public int playerWeight = 0;
    public int playerOreMax = 100;
    public int playerWeightMax = 100;
    public boolean playerRadarMute = false;
    public float playerToolCooldown = 0;
    public String playerHandle = "Player";
    public float volumeLevel = 1.0f;
    public long playerMyId = 0;
    public float radarCooldown = 0;
    public float radarDistance = 0;
    public float radarValue = 0;
    public float constructionGunAnimation = 0;

    public float mouseSensitivity = 0.005f;

    // Team variables
    public int teamOre = 0;
    public int teamRedCash = 0;
    public int teamBlueCash = 0;
    public PlayerTeam teamWinners = PlayerTeam.None;
    public Map<Vector3f, Defines.Beacon> beaconList = new HashMap<>();

    // Screen effect stuff
    private final Random randGen = new Random();
    public Defines.ScreenEffect screenEffect = Defines.ScreenEffect.None;
    public double screenEffectCounter = 0;

    // Team color stuff
    public boolean customColours = false;
    public Vector4f red = Defines.IM_RED;
    public Vector4f blue = Defines.IM_BLUE;
    public String redName = "Red";
    public String blueName = "Blue";

    // Chat stuff
    public Defines.ChatMessageType chatMode = Defines.ChatMessageType.None;
    public int chatMaxBuffer = 5;
    public List<Defines.ChatMessage> chatBuffer = new LinkedList<>(); 
    public List<Defines.ChatMessage> chatFullBuffer = new LinkedList<>(); 
    public String chatEntryBuffer = "";

    public PropertyBag(InfiniminerGame gameInstance) {
        // Initialize engines
        blockEngine = new BlockEngine();
        interfaceEngine = new InterfaceEngine(gameInstance);
        playerEngine = new PlayerEngine(blockEngine, this);
        skyplaneEngine = new SkyplaneEngine(gameInstance);
        particleEngine = new ParticleEngine(gameInstance);
        
        // Link property bag to engines
        blockEngine.setPropertyBag(this);
        interfaceEngine.setPropertyBag(this);
        skyplaneEngine.setPropertyBag(this);
        particleEngine.setPropertyBag(this);

        // Create a camera
        playerCamera = new Camera();
        updateCamera();
        
        // Initial equipment
        equipWeps();
    }

    public void equipWeps() {
        playerToolSelected = 0;
        playerBlockSelected = 0;
        
        switch (playerClass) {
            case Prospector:
                playerTools = new PlayerTools[]{PlayerTools.Pickaxe, PlayerTools.ConstructionGun, PlayerTools.ProspectingRadar};
                playerBlocks = new BlockType[]{
                    (playerTeam == PlayerTeam.Red) ? BlockType.SolidRed : BlockType.SolidBlue,
                    (playerTeam == PlayerTeam.Red) ? BlockType.TransRed : BlockType.TransBlue,
                    (playerTeam == PlayerTeam.Red) ? BlockType.BeaconRed : BlockType.BeaconBlue,
                    BlockType.Ladder
                };
                break;
            case Miner:
                playerTools = new PlayerTools[]{PlayerTools.Pickaxe, PlayerTools.ConstructionGun};
                playerBlocks = new BlockType[]{
                    (playerTeam == PlayerTeam.Red) ? BlockType.SolidRed : BlockType.SolidBlue,
                    (playerTeam == PlayerTeam.Red) ? BlockType.TransRed : BlockType.TransBlue,
                    BlockType.Ladder
                };
                break;
            case Engineer:
                playerTools = new PlayerTools[]{PlayerTools.Pickaxe, PlayerTools.ConstructionGun, PlayerTools.DeconstructionGun};
                playerBlocks = new BlockType[]{
                    (playerTeam == PlayerTeam.Red) ? BlockType.SolidRed : BlockType.SolidBlue,
                    BlockType.TransRed,
                    BlockType.TransBlue,
                    BlockType.Road,
                    BlockType.Ladder,
                    BlockType.Jump,
                    BlockType.Shock,
                    (playerTeam == PlayerTeam.Red) ? BlockType.BeaconRed : BlockType.BeaconBlue,
                    (playerTeam == PlayerTeam.Red) ? BlockType.BankRed : BlockType.BankBlue
                };
                break;
            case Sapper:
                playerTools = new PlayerTools[]{PlayerTools.Pickaxe, PlayerTools.ConstructionGun, PlayerTools.Detonator};
                playerBlocks = new BlockType[]{
                    (playerTeam == PlayerTeam.Red) ? BlockType.SolidRed : BlockType.SolidBlue,
                    (playerTeam == PlayerTeam.Red) ? BlockType.TransRed : BlockType.TransBlue,
                    BlockType.Ladder,
                    BlockType.Explosive
                };
                break;
        }
    }

    public PlayerTeam teamFromBlock(BlockType bt) {
        return switch (bt) {
            case TransBlue, SolidBlue, BeaconBlue, BankBlue -> PlayerTeam.Blue;
            case TransRed, SolidRed, BeaconRed, BankRed -> PlayerTeam.Red;
            default -> PlayerTeam.None;
        };
    }

    public void killPlayer(String deathMessage) {
        // PlaySound(InfiniminerSound.Death);
        playerPosition.set(randGen.nextInt(Defines.MAP_SIZE_X - 4) + 2, 66, randGen.nextInt(Defines.MAP_SIZE_Z - 4) + 2);
        playerVelocity.set(0, 0, 0);
        playerDead = true;
        screenEffect = Defines.ScreenEffect.Death;
        screenEffectCounter = 0;
        
        addChatMessage("You died: " + deathMessage, Defines.ChatMessageType.SayAll);
    }

    public void respawnPlayer() {
        playerDead = false;
        
        // Pick a random starting point
        playerPosition.set(randGen.nextInt(Defines.MAP_SIZE_X - 4) + 2, 66, randGen.nextInt(Defines.MAP_SIZE_Z - 4) + 2);
        playerVelocity.set(0, 0, 0);
        screenEffect = Defines.ScreenEffect.None;
        screenEffectCounter = 0;
        updateCamera();
        
        addChatMessage("You have respawned.", Defines.ChatMessageType.SayAll);
    }

    public void addChatMessage(String chatString, Defines.ChatMessageType chatType) {
        Defines.ChatMessage chatMsg = new Defines.ChatMessage(chatString, chatType, 10, 1);
        chatBuffer.add(0, chatMsg);
        chatFullBuffer.add(0, chatMsg);
        
        if (chatBuffer.size() > chatMaxBuffer) {
            chatBuffer.remove(chatBuffer.size() - 1);
        }
    }

    public void updateCamera() {
        if (playerCamera != null) {
            playerCamera.position.set(playerPosition);
        }
    }
}
