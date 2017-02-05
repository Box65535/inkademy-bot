package command;

import bot.InkademyCoordinator;
import box.discord.client.Messenger;
import box.discord.command.Command;
import box.discord.result.Option;
import data.ArchiveMessage;
import model.InkademyModel;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

import java.util.EnumSet;
import java.util.List;
import java.util.Queue;

public class ReplayCommand implements InkademyCommand {

    private static final EnumSet<Permissions> VOICED_ADD_PERMISSIONS =
            EnumSet.of(Permissions.READ_MESSAGES, Permissions.SEND_MESSAGES);
    private static final EnumSet<Permissions> EVERYONE_ADD_PERMISSIONS =
            EnumSet.of(Permissions.READ_MESSAGE_HISTORY);
    private static final EnumSet<Permissions> EVERYONE_REMOVE_PERMISSIONS =
            EnumSet.of(Permissions.SEND_MESSAGES);
    
    private InkademyModel model;
    private InkademyCoordinator bot;
    private Messenger messenger;
    
    public ReplayCommand(InkademyCoordinator bot, InkademyModel model, Messenger messenger) {
        this.model = model;
        this.bot = bot;
        this.messenger = messenger;
    }
    
    @Override
    public boolean matchesCommand(MessageReceivedEvent event) {
        return Command.isFirstToken(event, "!replay");
    }

    @Override
    public void performCommand(MessageReceivedEvent event) {

        IChannel receivedChannel = event.getMessage().getChannel();
        List<String> tokens = Command.tokenize(event.getMessage().getContent());
        if (tokens.size() != 2) {
            messenger.sendMessage(receivedChannel, "Incorrect number of arguments. Try !help.");
            return;
        }

        String channelName = tokens.get(1);
        Option<Queue<ArchiveMessage>> archive = model.getFullArchive(channelName);
        
        if (archive.isFailure() || archive.get().isEmpty()) {
            messenger.sendMessage(receivedChannel, "Could not find archive " + channelName);
            messenger.sendMessage(receivedChannel, "If you're unsure what your archive is called, try using !list");
            return;
        }
        
        Option<IChannel> channel = messenger.createChannel(event.getMessage().getGuild(), channelName);
        
        if (channel.isFailure()) {
            messenger.sendMessage(receivedChannel, "Could not create channel " + channelName);
            messenger.sendMessage(receivedChannel, "Check my server permissions");
            return;
        }
        
        for (IRole role : event.getMessage().getGuild().getRoles()) {
            if (role.isEveryoneRole()) {
                messenger.addChannelPermissions(channel.get(), role, EVERYONE_ADD_PERMISSIONS);
                messenger.removeChannelPermissions(channel.get(), role, EVERYONE_REMOVE_PERMISSIONS);
            }
            else if (role.getName().equals("voiced"))
                messenger.addChannelPermissions(channel.get(), role, VOICED_ADD_PERMISSIONS);
            else if (role.getName().equals("mod"))
                messenger.addChannelPermissions(channel.get(), role, VOICED_ADD_PERMISSIONS);
            else if (role.getName().equals("illuminati"))
                messenger.addChannelPermissions(channel.get(), role, VOICED_ADD_PERMISSIONS);
        }
        
        for (ArchiveMessage message : archive.get())
            messenger.sendEmbedMessage(channel.get(), message.toEmbedMessage());
        
        bot.listenToChannel(channel.get());
        
        messenger.sendMessage(channel.get(), "This channel is now under archive");
        messenger.sendMessage(channel.get(), "Use !finish to close this channel");
    }
}
