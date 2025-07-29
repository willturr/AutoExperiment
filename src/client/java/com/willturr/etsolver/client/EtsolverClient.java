//not yet in use.

package com.willturr.etsolver.client;

import com.willturr.etsolver.client.config.ConfigManager;
import com.willturr.etsolver.client.modules.SolverManager;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//mod functions


public class EtsolverClient implements ClientModInitializer {

    public static final String MOD_ID = "AutoExperiment";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Loading AutoExperiment...");
        ConfigManager.loadConfig();

        //ContainerManager.init();
        SolverManager.init();

    }
}
