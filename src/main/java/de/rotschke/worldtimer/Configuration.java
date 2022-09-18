package de.rotschke.worldtimer;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Main.MOD_ID)
public class Configuration {
    @Config.Name("Client Settings")
    public static CLIENT client = new CLIENT();
    public static class CLIENT {
        @Config.Name("Enable")
        @Config.Comment({"If enabled, a timer and titles will be shown"})
        public static boolean enable = true;

        public enum Position {
            LEFT_TOP,
            RIGHT_TOP,
            LEFT_BOTTOM,
            RIGHT_BOTTOM
        }
        @Config.Name("Position")
        @Config.Comment({"Position presets for the timer display"})
        public static Position position = Position.RIGHT_TOP;

        public enum Format {
            DAY,
            DAY_HH,
            DAY_HH_MM,
            DAY_HH_MM_SS,
            DAY_TICKS,
            HH,
            HH_MM,
            HH_MM_SS,
            TICKS
        }
        @Config.Name("Format")
        @Config.Comment({"Format presets for the timer display"})
        public static Format format = Format.DAY_HH;

        @Config.Name("12 Hour Mode")
        @Config.Comment({"Shows a different type of day time", "e. g. '6 am', '2 pm'", "if set to 'false': e. g. '16 hours'"})
        public static boolean clockType = true;
    }

    @Config.Name("Server Settings")
    public static SERVER server = new SERVER();
    public static class SERVER {
        @Config.Name("Allow Display")
        @Config.Comment({"Allow clients to show display"})
        public static boolean allowShowingDisplay = true;

        @Config.Name("Prohibit HH, MM & SS")
        @Config.Comment({"Prohibit clients from showing day time", "e. g. 'HH_MM' is disabled, but day will still be shown"})
        public static boolean showOnlyDay = false;

        @Config.Name("Show daytime in Overworld only")
        @Config.Comment({"The day time will only be shown to players in the overworld"})
        public static boolean showOnlyInOverworld = true;

        @Config.Name("Show day in Overworld only")
        @Config.Comment({"The titles and the whole display will be hidden when you're not in the overworld"})
        public static boolean workOnlyInOverworld = true;

        @Config.Name("Clock required")
        @Config.Comment({"Players can only see the day time if they have a clock in their inventory", "Doesn't work if 'Prohibit HH, MM & SS' is 'true'"})
        public static boolean requireClock = false;

        @Config.Name("Day Announcement")
        @Config.Comment({"All players see a title, when a new day has begun", "e. g. \"Day 43\""})
        public static boolean announceDay = true;
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Main.MOD_ID)) {
            ConfigManager.sync(Main.MOD_ID, Config.Type.INSTANCE);
        }
    }
}
