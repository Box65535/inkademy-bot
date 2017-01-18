package discord;

import java.io.File;
import java.util.EnumSet;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

public interface Messenger {

    public static final EnumSet<Permissions> EMPTY_SET = EnumSet.noneOf(Permissions.class);
    
    boolean sendMessage(IChannel channel, String message);

    boolean sendQuoteMessage(IChannel channel, String message);

    boolean uploadFile(IChannel channel, File file);

    IChannel createChannel(IGuild guild, String channelName);

    boolean addChannelPermissions(IChannel channel, IRole role, EnumSet<Permissions> permissions);

    boolean removeChannelPermissions(IChannel channel, IRole role, EnumSet<Permissions> permissions);

    public boolean removeChannel(IChannel channel);
}
