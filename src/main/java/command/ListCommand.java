package command;

import discord.Messenger;
import model.InkademyModel;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;

import java.util.List;

public class ListCommand implements Command {
    
    private InkademyModel model;
    private Messenger messenger;
    
    public ListCommand(InkademyModel model, Messenger messenger) {
        this.messenger = messenger;
    }
    
    @Override
    public boolean isCommand(MessageReceivedEvent event) {
        return Command.isFirstToken(event, "!list");
    }

    @Override
    public void performCommand(MessageReceivedEvent event) {

        IChannel channel = event.getMessage().getChannel();
        List<String> tokens = Command.tokenize(event.getMessage().getContent());
        if (tokens.size() != 1) {
            messenger.sendMessage(channel, "Incorrect number of arguments. Try !help.");
            return;
        }

        List<String> archivesList = model.listArchives();
        if (archivesList == null) {
            messenger.sendMessage(channel, "Could not list archives");
            messenger.sendMessage(channel, "Contact the server admin");
            return;
        }
        
        StringBuilder archives = new StringBuilder();
        for (String archive : archivesList)
            archives.append(archive).append(" ");
        messenger.sendMessage(channel, archives.toString());
    }
}
