package command;

import bot.InkademyBot;
import discord.Messenger;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

import java.util.EnumSet;
import java.util.List;

public class CreateCommand implements Command {
    
    private static final EnumSet<Permissions> VOICED_ADD_PERMISSIONS =
            EnumSet.of(Permissions.READ_MESSAGES, Permissions.SEND_MESSAGES);
    private static final EnumSet<Permissions> EVERYONE_ADD_PERMISSIONS =
            EnumSet.of(Permissions.READ_MESSAGE_HISTORY);
    private static final EnumSet<Permissions> EVERYONE_REMOVE_PERMISSIONS =
            EnumSet.of(Permissions.SEND_MESSAGES);

    private InkademyBot bot;
    private Messenger messenger;
    
    public CreateCommand(InkademyBot bot, Messenger messenger) {
        this.bot = bot;
        this.messenger = messenger;
    }
    
    @Override
    public boolean isCommand(MessageReceivedEvent event) {
        return Command.isFirstToken(event, "!create");
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
        IChannel channel = messenger.createChannel(event.getMessage().getGuild(), channelName);
        
        if (channel == null) {
            messenger.sendMessage(receivedChannel, "Could not create channel " + channelName);
            messenger.sendMessage(receivedChannel, "Check my server permissions");
            return;
        }
        
        for (IRole role : event.getMessage().getGuild().getRoles()) {
            if (role.isEveryoneRole()) {
                messenger.addChannelPermissions(channel, role, EVERYONE_ADD_PERMISSIONS);
                messenger.removeChannelPermissions(channel, role, EVERYONE_REMOVE_PERMISSIONS);
            }
            else if (role.getName().equals("voiced"))
                messenger.addChannelPermissions(channel, role, VOICED_ADD_PERMISSIONS);
            else if (role.getName().equals("mod"))
                messenger.addChannelPermissions(channel, role, VOICED_ADD_PERMISSIONS);
            else if (role.getName().equals("illuminati"))
                messenger.addChannelPermissions(channel, role, VOICED_ADD_PERMISSIONS);
        }
        
        bot.listenToChannel(channel);
        
        messenger.sendMessage(channel, "This channel is now under archive");
        messenger.sendMessage(channel, "Messages will be saved, however deletions and edits will not be");
        messenger.sendMessage(channel, "Use !finish to close this channel");
    }
}