package de.rotschke.worldtimer;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.UUID;

public class ServerHandler extends WorldSavedData {
    public static ServerHandler INSTANCE;

    private boolean updatingPermissions;
    private PlayerList list;
    private Style redText;
    private long dayCounter;

    public ServerHandler() {
        super("World Timer-Data");
        if (INSTANCE == null) {
            INSTANCE = this;
        }
        updatingPermissions = false;
        list = new PlayerList();
        redText = new Style().setColor(TextFormatting.RED);
        dayCounter = 1;
    }

    @SubscribeEvent
    public void loadWorld(WorldEvent.Load event) {
        updatingPermissions = true;
    }

    @SubscribeEvent
    public void update(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (event.world.getWorldTime() % 24000 == 1) {
                dayCounter++;
                updateClients();
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            updatingPermissions = true;
        }
    }

    @SubscribeEvent
    public void death(LivingDeathEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            list.reportDeath(event.getEntity().getName(), event.getEntity().getUniqueID());
            long deaths = list.getDeathsByPlayer(event.getEntity().getName(), event.getEntity().getUniqueID()) % 10;
            String a;
            switch ((int)deaths) {
                case 1:
                    a = list.getDeathsByPlayer(event.getEntity().getName(), event.getEntity().getUniqueID()) % 100 == 11 ? "th" : "st";
                    break;
                case 2:
                    a = list.getDeathsByPlayer(event.getEntity().getName(), event.getEntity().getUniqueID()) % 100 == 12 ? "th" : "nd";
                    break;
                case 3:
                    a = list.getDeathsByPlayer(event.getEntity().getName(), event.getEntity().getUniqueID()) % 100 == 13 ? "th" : "rd";
                    break;
                default:
                    a = "th";
                    break;
            }
            event.getEntity().sendMessage(new TextComponentString("This is your " + list.getDeathsByPlayer(event.getEntity().getName(), event.getEntity().getUniqueID()) + a + " Death on day " + dayCounter + "...").setStyle(redText));
        }
    }

    private void updateClients() {
        if (updatingPermissions && !Objects.requireNonNull(Minecraft.getMinecraft().world.playerEntities).isEmpty()) {
            Network.wrapper.sendToAll(new Network.Messages(dayCounter));
            updatingPermissions = false;
        }
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound nbt) {
        dayCounter = nbt.getLong("day");
        int length = nbt.getInteger("length");
        String[] name = new String[length];
        UUID[] id = new UUID[length];
        long[] death = new long[length];
        for (int i = 0; i < length; i++) {
            name[i] = nbt.getString("names" + i);
            id[i] = nbt.getUniqueId("ids" + i);
            death[i] = nbt.getLong("deaths" + i);
        }
        list.names = name;
        list.ids = id;
        list.deaths = death;
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound.setLong("day", dayCounter);
        String[] names = list.names;
        UUID[] ids = list.ids;
        long[] deaths = list.deaths;
        int length = 0;
        for (int i = 0; i < names.length; i++) {
            compound.setString("names" + i, names[i]);
            compound.setUniqueId("ids" + i, ids[i]);
            compound.setLong("deaths" + i, deaths[i]);
            length = i;
        }
        compound.setInteger("length", length);
        return compound;
    }
}
