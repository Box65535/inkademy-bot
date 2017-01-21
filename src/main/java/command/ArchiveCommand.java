package command;

import box.discord.client.Messenger;
import box.discord.command.Command;
import box.discord.result.Option;
import model.InkademyModel;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;

import java.io.File;
import java.util.List;

public class ArchiveCommand implements InkademyCommand {
    
    private InkademyModel model;
    private Messenger messenger;
    
    public ArchiveCommand(InkademyModel model, Messenger messenger) {
        this.model = model;
        this.messenger = messenger;
    }

    @Override
    public boolean matchesCommand(MessageReceivedEvent event) {
        return Command.isFirstToken(event, "!archive");
    }

    @Override
    public void performCommand(MessageReceivedEvent event) {

        IChannel channel = event.getMessage().getChannel();
        List<String> tokens = Command.tokenize(event.getMessage().getContent());
        if (tokens.size() != 2) {
            messenger.sendMessage(channel, "Incorrect number of arguments. Try !help.");
            return;
        }
        
        String archiveName = tokens.get(0);
        Option<File> file = model.getArchive(archiveName);
        
        if (!file.isSuccess()) {
            messenger.sendMessage(channel, "Could not find archive " + archiveName);
            messenger.sendMessage(channel, "If you're unsure what your archive is called, try using !list");
            return;
        }
        
        messenger.uploadFile(channel, file.get());
    }
}
