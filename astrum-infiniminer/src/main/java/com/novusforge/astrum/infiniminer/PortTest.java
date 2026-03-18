package com.novusforge.astrum.infiniminer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PortTest {
    public static void main(String[] args) {
        System.out.println("Testing Infiniminer Port Components...");
        
        // Test BlockType
        BlockType dirt = BlockType.Dirt;
        System.out.println("Block: " + dirt + ", Solid: " + dirt.isSolid() + ", Cost: " + dirt.getCost());
        
        BlockType bank = BlockType.BankRed;
        System.out.println("Block: " + bank + ", Solid: " + bank.isSolid() + ", Cost: " + bank.getCost());

        // Test DatafileLoader
        File tempFile = new File("test.cfg");
        try (FileWriter fw = new FileWriter(tempFile)) {
            fw.write("# Test Comment\n");
            fw.write("serverName = Test Server\n");
            fw.write("maxPlayers = 16\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        DatafileLoader loader = new DatafileLoader("test.cfg");
        System.out.println("Data loaded: " + loader.getData());
        
        if ("Test Server".equals(loader.getData().get("serverName"))) {
            System.out.println("DatafileLoader test PASSED");
        } else {
            System.out.println("DatafileLoader test FAILED");
        }
        
        tempFile.delete();
        
        // Test Defines
        System.out.println("Version: " + Defines.INFINIMINER_VERSION);
        System.out.println("Sanitize: " + Defines.sanitize("Hello\nWorld!"));
        
        System.out.println("Port components test COMPLETED");
    }
}
