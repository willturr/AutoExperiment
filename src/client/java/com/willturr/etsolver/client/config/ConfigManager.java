package com.willturr.etsolver.client.config;

import com.willturr.etsolver.client.config.Config;

import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;


public class ConfigManager {

    private static final File CONFIG_FILE = new File(
            FabricLoader.getInstance().getConfigDir().toFile(),
            "etsolver.cfg"
    );

    public static void saveConfig() {
        try (PrintWriter writer = new PrintWriter(CONFIG_FILE)) {
            writer.println(Config.universalTickDelay);
        } catch (IOException e) {
            System.err.println("Could not save config: " + e.getMessage());
        }
    }

    public static void loadConfig() {
        if (!CONFIG_FILE.exists()) {
            saveConfig();
            return;
        }

        try {
            //this should be some kind of mapping, but one config value lol
            BufferedReader reader = new BufferedReader(new FileReader(String.valueOf(CONFIG_FILE.toPath())));
            Config.universalTickDelay = Double.parseDouble(reader.readLine());


        } catch (IOException e) {
            System.err.println("Could not load config: " + e.getMessage());
        }
    }


}
