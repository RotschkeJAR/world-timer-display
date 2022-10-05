package de.rotschke.worldtimer;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Commands extends CommandBase {
    @Override
    @Nonnull
    public String getName() {
        return "worldtimer";
    }

    @Override
    @Nonnull
    public String getUsage(@Nullable ICommandSender sender) {
        return "/worldtimer reset_daycounter|reset_deathcounter|refresh \"playerName\"";
    }

    @Override
    public void execute(@Nullable MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        if (args.length < 1) {
            throw new WrongUsageException(this.getUsage(sender));
        } else {
            switch (args[0]) {
                case "reset_daycounter":
                    ServerHandler.INSTANCE.resetDays();
                    sender.sendMessage(new TextComponentString("Day counter has been reset!"));
                    break;
                case "reset_deathcounter":
                    if (args.length < 2) {
                        for (EntityPlayer player : Minecraft.getMinecraft().world.playerEntities) {
                            if (sender.getName().equals(player.getName())) {
                                if (!PlayerList.list.exist(player.getName(), player.getUniqueID())) {
                                    PlayerList.list.add(player.getName(), player.getUniqueID());
                                    sender.sendMessage(new TextComponentString("You haven't been registered yet! Please execute \"/worldtimer refresh\" in order to check for other unregistered players (offline players won't be found)."));
                                }
                                PlayerList.list.deaths[PlayerList.list.getFieldByPlayer(player.getName(), player.getUniqueID())] = 0;
                                sender.sendMessage(new TextComponentString("Your death counter has been reset!"));
                            }
                        }
                    } else {
                        for (EntityPlayer player : Minecraft.getMinecraft().world.playerEntities) {
                            if (player.getName().equals(args[1])) {
                                if (!PlayerList.list.exist(player.getName(), player.getUniqueID())) {
                                    PlayerList.list.add(player.getName(), player.getUniqueID());
                                    sender.sendMessage(new TextComponentString("This player hasn't been registered yet! Please execute \"/worldtimer refresh\" in order to check for other unregistered players (offline players won't be found)."));
                                }
                                PlayerList.list.deaths[PlayerList.list.getFieldByPlayer(player.getName(), player.getUniqueID())] = 0;
                                sender.sendMessage(new TextComponentString(player.getName() + "'s death counter has been reset!"));
                            }
                        }
                    }
                    break;
                case "refresh":
                    sender.sendMessage(new TextComponentString("Looking for unregistered players..."));
                    int entitiesFound = 0;
                    for (EntityPlayer player : Minecraft.getMinecraft().world.playerEntities) {
                        if (!PlayerList.list.exist(player.getName(), player.getUniqueID())) {
                            PlayerList.list.add(player.getName(), player.getUniqueID());
                            entitiesFound ++;
                            sender.sendMessage(new TextComponentString(player.getName() + " has been registered!"));
                        }
                    }
                    sender.sendMessage(new TextComponentString(entitiesFound > 0 ? ("CONCLUDED! " + entitiesFound + " unregistered players found!") : ("CONCLUDED! No unregistered players found.")));
                    break;
            }
        }
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(@Nullable MinecraftServer server, @Nullable ICommandSender sender, @Nonnull String[] args, @Nullable BlockPos position) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.add("reset_daycounter");
            suggestions.add("reset_deathcounter");
        } else if (Objects.equals(args[0], "reset_deathcounter")) {
            for (EntityPlayer player : Minecraft.getMinecraft().world.playerEntities) {
                suggestions.add(player.getName());
            }
        }
        return suggestions;
    }
}
