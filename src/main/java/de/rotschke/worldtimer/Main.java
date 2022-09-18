package de.rotschke.worldtimer;

import de.rotschke.worldtimer.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(
        modid = Main.MOD_ID,
        name = Main.MOD_NAME,
        version = Main.VERSION
)
public class Main {

    public static final String MOD_ID = "worldtimer";
    public static final String MOD_NAME = "World Timer Display";
    public static final String VERSION = "1.0";

    @SidedProxy(clientSide = "de.rotschke.worldtimer.proxy.ClientProxy", serverSide = "de.rotschke.worldtimer.proxy.ServerProxy", modId = MOD_ID)
    public static CommonProxy proxy;
    public static Configuration config;

    @Mod.Instance(MOD_ID)
    public static Main INSTANCE;

    //This is the first initialization event. Register tile entities here. The registry events below will have fired prior to entry to this method.
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    //This is the second initialization event. Register custom recipes
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    //This is the final initialization event. Register actions from other mods here.
    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    //Forge will automatically look up and bind blocks to the fields in this class based on their registry name.
    @GameRegistry.ObjectHolder(MOD_ID)
    public static class Blocks {

    }

    //Forge will automatically look up and bind items to the fields in this class based on their registry name.
    @GameRegistry.ObjectHolder(MOD_ID)
    public static class Items {

    }

    //This is a special class that listens to registry events, to allow creation of mod blocks and items at the proper time.
    @Mod.EventBusSubscriber
    public static class ObjectRegistryHandler {
        //Listen for the register event for creating custom items.
        @SubscribeEvent
        public static void addItems(RegistryEvent.Register<Item> event) {

        }

        //Listen for the register event for creating custom blocks.
        @SubscribeEvent
        public static void addBlocks(RegistryEvent.Register<Block> event) {

        }
    }
}
