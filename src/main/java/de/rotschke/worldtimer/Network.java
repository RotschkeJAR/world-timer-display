package de.rotschke.worldtimer;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;

public class Network {
    public static final SimpleNetworkWrapper wrapper = new SimpleNetworkWrapper(Main.MOD_ID);

    public static void init() {
        wrapper.registerMessage(HandleStatus.class, Messages.class, 0, Side.CLIENT);
    }

    public static class HandleStatus implements IMessageHandler<Messages, IMessage> {
        @Override
        public IMessage onMessage(Messages msg, MessageContext ctx) {
            if (msg.valid && ctx.side != Side.SERVER) {
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    ClientHandler.INSTANCE.update(msg.day);
                });
            }
            return null;
        }
    }

    public static class Messages implements IMessage {
        private boolean valid = false;
        public long day;

        public Messages(long day) {
            this.day = day;
            valid = true;
        }

        public Messages() {}

        @Override
        public void fromBytes(@Nonnull ByteBuf buf) {
            try {
                day = buf.readLong();
            } catch (IndexOutOfBoundsException ioe) {
                ioe.printStackTrace();
            }
            valid = true;
        }

        @Override
        public void toBytes(@Nonnull ByteBuf buf) {
            if (valid) {
                buf.writeLong(day);
            }
        }
    }
}
