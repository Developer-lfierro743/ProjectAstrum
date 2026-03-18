package com.novusforge.astrum.infiniminer;

import org.joml.Vector4f;

/**
 * Infiniminer Defines - Core constants and definitions.
 * Ported from C# XNA to Java 21 LWJGL3.
 */
public class Defines {
    
    // Version
    public static final String INFINIMINER_VERSION = "1.5";
    
    // Colors (using ARGB or separate components)
    public static final Vector4f IM_RED = new Vector4f(222/255f, 24/255f, 24/255f, 1.0f);
    public static final Vector4f IM_BLUE = new Vector4f(80/255f, 150/255f, 255/255f, 1.0f);
    
    // Map dimensions
    public static final int MAP_SIZE_X = 64;
    public static final int MAP_SIZE_Y = 64;
    public static final int MAP_SIZE_Z = 64;
    public static final int GROUND_LEVEL = 8;
    
    // Network
    public static final int DEFAULT_PORT = 5565;
    
    // Physics
    public static final float MOVESPEED = 3.5f;
    public static final float GRAVITY = -8.0f;
    public static final float JUMPVELOCITY = 4.0f;
    public static final float CLIMBVELOCITY = 2.5f;
    public static final float DIEVELOCITY = 15.0f;
    
    // Input
    public enum Buttons {
        None, Fire, AltFire, Forward, Backward, Left, Right, Sprint, Jump, Crouch,
        Ping, Deposit, Withdraw, SayAll, SayTeam, ChangeClass, ChangeTeam,
        Tool1, Tool2, Tool3, Tool4, Tool5, ToolUp, ToolDown, BlockUp, BlockDown
    }
    
    public enum MouseButton {
        LeftButton, MiddleButton, RightButton, WheelUp, WheelDown
    }
    
    public enum ScreenEffect {
        None, Death, Teleport, Fall, Explosion
    }
    
    public enum InfiniminerSound {
        DigDirt, DigMetal, Ping, ConstructionGun, Death, CashDeposit,
        ClickHigh, ClickLow, GroundHit, Teleporter, Jumpblock, Explosion,
        RadarLow, RadarHigh, RadarSwitch
    }
    
    public enum InfiniminerMessage {
        BlockBulkTransfer, BlockSet, UseTool, SelectClass, ResourceUpdate,
        DepositOre, DepositCash, WithdrawOre, TriggerExplosion,
        PlayerUpdate, PlayerJoined, PlayerLeft, PlayerSetTeam,
        PlayerDead, PlayerAlive, PlayerPing, ChatMessage, GameOver,
        PlaySound, TriggerConstructionGunAnimation, SetBeacon
    }
    
    public enum ChatMessageType {
        None, SayAll, SayRedTeam, SayBlueTeam
    }
    
    public static class ChatMessage {
        public String message;
        public ChatMessageType type;
        public float timestamp;
        public int newlines;

        public ChatMessage(String message, ChatMessageType type, float timestamp, int newlines) {
            this.message = message;
            this.type = type;
            this.timestamp = timestamp;
            this.newlines = newlines;
        }
    }

    public static class Beacon {
        public String ID;
        public PlayerTeam Team;
    }
    
    // Death messages
    public static final String deathByLava = "WAS INCINERATED BY LAVA!";
    public static final String deathByElec = "WAS ELECTROCUTED!";
    public static final String deathByExpl = "WAS KILLED IN AN EXPLOSION!";
    public static final String deathByFall = "WAS KILLED BY GRAVITY!";
    public static final String deathByMiss = "WAS KILLED BY MISADVENTURE!";
    public static final String deathBySuic = "HAS COMMITED PIXELCIDE!";
    
    // String Sanitization
    public static String sanitize(String input) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c >= 32 && c <= 126)
                output.append(c);
        }
        return output.toString();
    }
}
