package com.novusforge.astrum.infiniminer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * DatafileLoader - Loads key/value pairs from a text file.
 * Format: "key = value", '#' for comments.
 */
public class DatafileLoader {
    private final Map<String, String> dataDict = new HashMap<>();

    public DatafileLoader(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] args = line.split("=", 2);
                if (args.length == 2) {
                    dataDict.put(args[0].trim(), args[1].trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading datafile: " + filename);
        }
    }

    public Map<String, String> getData() {
        return dataDict;
    }
}
