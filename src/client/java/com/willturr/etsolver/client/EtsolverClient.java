package com.willturr.etsolver.client;

import com.willturr.etsolver.client.modules.SolverManager;
import net.fabricmc.api.ClientModInitializer;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//mod functions
import com.willturr.etsolver.client.modules.ContainerManager;

public class EtsolverClient implements ClientModInitializer {

    public static final String MOD_ID = "etsolver";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Loading etsolver...");

        //ContainerManager.init();
        SolverManager.init();

    }
}
