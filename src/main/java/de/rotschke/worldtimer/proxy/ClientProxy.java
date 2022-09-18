package de.rotschke.worldtimer.proxy;

import de.rotschke.worldtimer.ClientHandler;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        if (ClientHandler.INSTANCE == null) {
            new ClientHandler();
        }
        FMLCommonHandler.instance().bus().register(ClientHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(ClientHandler.INSTANCE);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {

    }
}
