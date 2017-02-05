package command;

import box.discord.client.Messenger;
import box.discord.command.Command;
import box.discord.result.Option;
import model.InkademyModel;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;

import java.util.List;
import java.util.Set;

public class ListCommand implements InkademyCommand {
    
    private InkademyModel model;
    private Messenger messenger;
    
    public ListCommand(InkademyModel model, Messenger messenger) {
        this.model = model;
        this.messenger = messenger;
    }
    
    @Override
    public boolean matchesCommand(MessageReceivedEvent event) {
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

        Option<Set<String>> archivesList = model.getAllArchivedTopics();
        if (archivesList.isFailure()) {
            messenger.sendMessage(channel, "Could not list archives");
            messenger.sendMessage(channel, "Contact the server admin");
            return;
        }
        
        StringBuilder archives = new StringBuilder();
        for (String archive : archivesList.get())
            archives.append(archive).append(" ");
        messenger.sendMessage(channel, archives.toString());
    }
}
