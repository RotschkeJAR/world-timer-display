package de.rotschke.worldtimer.proxy;

import de.rotschke.worldtimer.Network;
import de.rotschke.worldtimer.ServerHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent e) {
        if (ServerHandler.INSTANCE == null) {
            new ServerHandler();
        }
        MinecraftForge.EVENT_BUS.register(ServerHandler.INSTANCE);
        FMLCommonHandler.instance().bus().register(ServerHandler.INSTANCE);

        Network.init();
    }

    public void init(FMLInitializationEvent e) {

    }

    public void postInit(FMLPostInitializationEvent e) {

    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {

    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {

    }
}
