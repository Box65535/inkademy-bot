package command;

import box.discord.client.Messenger;
import box.discord.command.Command;
import box.discord.result.Option;
import data.ArchiveMessage;
import model.InkademyModel;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;

import java.io.*;
import java.util.List;
import java.util.Queue;
import java.util.SortedSet;

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
        
        String archiveName = tokens.get(1);
        Option<Queue<ArchiveMessage>> archive = model.getFullArchive(archiveName);
        
        if (archive.isFailure()) {
            messenger.sendMessage(channel, "Could not find archive " + archiveName);
            messenger.sendMessage(channel, "If you're unsure what your archive is called, try using !list");
            return;
        }
        
        File file = new File(archiveName + ".txt");
        try (Writer writer = new BufferedWriter(new FileWriter(file))) {
            for (ArchiveMessage message : archive.get())
                writer.write(message.toString());
            messenger.uploadFile(channel, file);
        }
        catch (Exception e) {
            messenger.sendMessage(channel, "Could not upload file");
        }
    }
}
