package de.rotschke.worldtimer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Objects;

@SideOnly(Side.CLIENT)
public class ClientHandler extends Gui {
    public static ClientHandler INSTANCE;
    private final Minecraft mc;
    private boolean fullscreenChanged;
    private boolean rendering;
    private long day;
    private int time;

    private int width;
    private int height;

    public ClientHandler() {
        if (INSTANCE == null) {
            INSTANCE = this;
        }
        mc = Minecraft.getMinecraft();
        fullscreenChanged = Minecraft.getMinecraft().isFullScreen();
        day = 1;
        time = 0;
    }

    public void update(long newDay) {
        day = newDay;
        if (rendering && Configuration.client.enable && day > 0 && Configuration.server.announceDay && (!Configuration.server.workOnlyInOverworld || mc.player.dimension == 0)) {
            switch (Configuration.server.announcementType) {
                case TITLE:
                    Minecraft.getMinecraft().ingameGUI.displayTitle("Day " + day, "", 20, 140, 60);
                    break;
                case ACTIONBAR:
                    Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Day " + day));
                    break;
                case CHAT_MESSAGE:
                    Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentString("Day " + day), true);
                    break;
            }
            updateScreenResolution();
        }
    }

    public void updateScreenResolution() {
        ScaledResolution scaled = new ScaledResolution(Minecraft.getMinecraft());
        width = scaled.getScaledWidth();
        height = scaled.getScaledHeight();
        fullscreenChanged = Minecraft.getMinecraft().isFullScreen();
    }

    @SubscribeEvent
    public void tick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            time = (int)event.world.getWorldTime() % 24000;
        }
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if ((width == 0 && height == 0) || (Minecraft.getMinecraft().isFullScreen() != fullscreenChanged) || (width != Minecraft.getMinecraft().displayWidth) || (height != Minecraft.getMinecraft().displayHeight)) {
            updateScreenResolution();
        }
        if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            rendering = true;
            if (Configuration.server.allowShowingDisplay && Configuration.client.enable && day > 0 && (!Configuration.server.workOnlyInOverworld || mc.player.dimension == 0)) {
                draw(!Configuration.server.showOnlyDay && (Configuration.server.showOnlyInOverworld ? (mc.player.dimension == 0 && (!Configuration.server.requireClock || hasItem(mc.player, Objects.requireNonNull(Item.getByNameOrId("minecraft:clock"))))) : (!Configuration.server.requireClock || hasItem(mc.player, Objects.requireNonNull(Item.getByNameOrId("minecraft:clock"))))));
            }
        }
    }

    private void draw(boolean showTime) {
        String line1 = timerTextDisplay(true);
        String line2 = timerTextDisplay(false);
        int x = 0, y = 0;
        switch (Configuration.client.position) {
            case LEFT_TOP:
                x = 5;
                y = 3;
                break;
            case RIGHT_TOP:
                if (mc.fontRenderer.getStringWidth(line1) < mc.fontRenderer.getStringWidth(line2)) {
                    while (mc.fontRenderer.getStringWidth(line1) < mc.fontRenderer.getStringWidth(line2)) {
                        line1 = " " + line1;
                    }
                } else if (mc.fontRenderer.getStringWidth(line1) > mc.fontRenderer.getStringWidth(line2)) {
                    while (mc.fontRenderer.getStringWidth(line1) > mc.fontRenderer.getStringWidth(line2)) {
                        line2 = " " + line2;
                    }
                }
                x = width - mc.fontRenderer.getStringWidth(line1) - 5;
                y = 3;
                break;
            case LEFT_BOTTOM:
                x = 5;
                y = height - 24;
                break;
            case RIGHT_BOTTOM:
                if (mc.fontRenderer.getStringWidth(line1) < mc.fontRenderer.getStringWidth(line2)) {
                    while (mc.fontRenderer.getStringWidth(line1) < mc.fontRenderer.getStringWidth(line2)) {
                        line1 = " " + line1;
                    }
                } else if (mc.fontRenderer.getStringWidth(line1) > mc.fontRenderer.getStringWidth(line2)) {
                    while (mc.fontRenderer.getStringWidth(line1) > mc.fontRenderer.getStringWidth(line2)) {
                        line2 = " " + line2;
                    }
                }
                x = width - mc.fontRenderer.getStringWidth(line1) - 5;
                y = height - 24;
                break;
        }
        drawString(mc.fontRenderer, line1, x, y, 16777215);
        if (showTime) drawString(mc.fontRenderer, line2, x, y + 11, 16777215);
    }

    private String timerTextDisplay(boolean firstLine) {
        String result = "error";
        if (firstLine && (Configuration.client.format == Configuration.client.format.DAY || Configuration.client.format == Configuration.client.format.DAY_HH || Configuration.client.format == Configuration.client.format.DAY_HH_MM || Configuration.client.format == Configuration.client.format.DAY_HH_MM_SS)) {
            result = "Day " + day;
        } else {
            int time = (this.time + 6000) % 24000;
            //hours
            byte hh = (byte)Math.floor(time / 1000.0);

            //minutes
            double m = time % 1000.0;
            m = m / 1000.0;
            m = m * 60.0;
            byte mm = (byte)Math.floor(m);

            //seconds
            byte ss = (byte)Math.floor((m % 1.0) * 60.0);

            switch (Configuration.client.format) {
                case DAY_HH:
                case HH:
                    if (Configuration.client.clockType) {
                        String a = hh > 11 ? "PM" : "AM";
                        hh = (byte)(a == "PM" ? (hh - 12) : hh);
                        hh = hh == 0 ? 12 : hh;
                        result = hh + " " + a;
                    } else {
                        result = hh + "hours";
                    }
                    break;
                case DAY_HH_MM:
                case HH_MM:
                    if (Configuration.client.clockType) {
                        String a = hh > 11 ? "PM" : "AM";
                        hh = (byte)(a == "PM" ? (hh - 12) : hh);
                        hh = hh == 0 ? 12 : hh;
                        result = hh + ":" + (mm > 9 ? mm : ("0" + mm)) + " " + a;
                    } else {
                        result = hh + ":" + (mm > 9 ? mm : ("0" + mm));
                    }
                    break;
                case DAY_HH_MM_SS:
                case HH_MM_SS:
                    if (Configuration.client.clockType) {
                        String a = hh > 11 ? "PM" : "AM";
                        hh = (byte)(a == "PM" ? (hh - 12) : hh);
                        hh = hh == 0 ? 12 : hh;
                        result = hh + ":" + (mm > 9 ? mm : ("0" + mm)) + ":" + (ss > 9 ? ss : ("0" + ss)) + " " + a;
                    } else {
                        result = hh + ":" + (mm > 9 ? mm : ("0" + mm)) + ":" + (ss > 9 ? ss : ("0" + ss));
                    }
                    break;
                case DAY_TICKS:
                case TICKS:
                    result = this.time + " Ticks";
                    break;
            }
        }
        return result;
    }

    public boolean hasItem(@Nonnull EntityPlayer player, @Nonnull Item item) {
        for (ItemStack s : player.inventory.mainInventory) {
            if (s.getItem() == item) {
                return true;
            }
        }
        return false;
    }
}
