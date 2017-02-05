package command;

import box.discord.client.Messenger;
import box.discord.command.Command;
import box.discord.result.Option;
import data.ArchiveMessage;
import model.InkademyModel;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;

import java.util.List;
import java.util.Queue;
import java.util.SortedSet;

public class QueryCommand implements InkademyCommand {
    
    private InkademyModel model;
    private Messenger messenger;
    
    public QueryCommand(InkademyModel model, Messenger messenger) {
        this.model = model;
        this.messenger = messenger;
    }
    
    @Override
    public boolean matchesCommand(MessageReceivedEvent event) {
        return Command.isFirstToken(event, "!query");
    }

    @Override
    public void performCommand(MessageReceivedEvent event) {

        IChannel channel = event.getMessage().getChannel();
        List<String> tokens = Command.tokenize(event.getMessage().getContent());
        if (tokens.size() != 3) {
            messenger.sendMessage(channel, "Incorrect number of arguments. Try !help.");
            return;
        }
        
        String archiveName = tokens.get(1);
        String pattern = tokens.get(2);
        Option<Queue<ArchiveMessage>> archive;
//        if (archiveName.equals("*"))
//            archive = model.queryArchive(pattern);
//        else
            archive = model.queryArchive(archiveName, pattern);
        
        if (archive.isFailure()) {
            messenger.sendMessage(channel, "Could not find archive " + archiveName);
            messenger.sendMessage(channel, "If you're unsure what your archive is called, try using !list");
            return;
        }
        
        if (archive.get().isEmpty()) {
            messenger.sendMessage(channel, "Query returned no results.");
            messenger.sendMessage(channel, "If you're unsure what your archive is called, try using !list");
        }
        else {
            for (ArchiveMessage message : archive.get())
                messenger.sendEmbedMessage(channel, message.toEmbedMessage());
        }
    }
}
