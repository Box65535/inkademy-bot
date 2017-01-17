package command;

import archive.ArchiveReader;
import archive.ArchiveWriter;
import bot.ArchiveListener;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InkademyCommandHandler implements CommandHandler {

    private static final String HELP = "!help";
    private static final String CREATE = "!create";
    private static final String ARCHIVE = "!archive";
    private static final String LISTEN = "!listen";
    private static final String IGNORE = "!ignore";
    private static final String FINISH = "!finish";
    private static final String LIST = "!list";
    private static final String GREP = "!grep";
    private static final Set<String> COMMANDS =
            Stream.of(HELP, CREATE, ARCHIVE, LISTEN, IGNORE, FINISH, LIST).collect(Collectors.toSet());

    private IDiscordClient client;
    private Map<IChannel, ArchiveListener> managedChannels;
    
    private InkademyCommandHandler() {}
    
    public static InkademyCommandHandler createHandler(IDiscordClient client) {
        InkademyCommandHandler handler = new InkademyCommandHandler();
        handler.client = client;
        handler.managedChannels = new HashMap<>();
        return handler;
    }
    
    public void handle(MessageReceivedEvent event) {
            
        if (!isInkademy(event))
            return;
        if (!isVoiced(event))
            return;
        if (!isCommand(event))
            return;
        
        handleCommand(event);
    }

    private static boolean isInkademy(MessageReceivedEvent event) {
        return event.getMessage().getGuild().getName().equals("The Inkademy");
    }
    
    private static boolean isVoiced(MessageReceivedEvent event) {
        
        IGuild guild = event.getMessage().getGuild();
        for (IRole role : event.getMessage().getAuthor().getRolesForGuild(guild)) {
            if (role.getName().equals("voiced"))
                return true;
        }
        return false;
    }

    private static List<String> tokenize(String message) {
        return Arrays.asList(message.split("\\s+"));
    }

    private static boolean isCommand(MessageReceivedEvent event) {
        
        List<String> tokens = tokenize(event.getMessage().getContent());
        
        if (tokens.size() <= 0)
            return false;

        String command = tokens.get(0);
        return COMMANDS.contains(command);
    }

    private void handleCommand(MessageReceivedEvent event) {
        
        List<String> tokens = tokenize(event.getMessage().getContent());
        String command = tokens.get(0);

        switch (command) {
            case HELP:
                helpCommand(event);
                break;
            case CREATE:
                createCommand(event);
                break;
            case ARCHIVE:
                archiveCommand(event);
                break;
            case FINISH:
                finishCommand(event);
                break;
            case LISTEN:
                listenCommand(event);
                break;
            case IGNORE:
                ignoreCommand(event);
                break;
            case LIST:
                listCommand(event);
                break;
            case GREP:
                grepCommand(event);
                break;
            default:
                throw new IllegalStateException("Command not defined for " + command);
        }
    }

    private void helpCommand(MessageReceivedEvent event) {

        final String HELP_MESSAGE = new StringBuilder()
                .append("Inkademy Bot Usage:\n")
                .append("  !help                  Display usage\n")
                .append("  !create channel        Create a new archivable channel\n")
                .append("  !finish                Close and archive this channel\n")
                .append("  !archive channel       Retrieve the archive file for a channel\n")
                .append("  !listen                Start archiving this channel\n")
                .append("  !ignore                Stop archiving this channel\n")
                .append("  !list                  List files in the archive\n")
//                .append("  !grep pattern file     Query files in the archive\n")
                .toString();
        
        IChannel channel = event.getMessage().getChannel();
        MessageBuilder message = new MessageBuilder(client).withChannel(channel).withQuote(HELP_MESSAGE);
        sendMessage(message);
    }

    private void createCommand(MessageReceivedEvent event) {
        
        final EnumSet<Permissions> EMPTY_SET = EnumSet.noneOf(Permissions.class);
        final EnumSet<Permissions> VOICED_ADD_PERMISSIONS =
                EnumSet.of(Permissions.READ_MESSAGES, Permissions.SEND_MESSAGES);
        final EnumSet<Permissions> EVERYONE_ADD_PERMISSIONS =
                EnumSet.of(Permissions.READ_MESSAGE_HISTORY);
        final EnumSet<Permissions> EVERYONE_REMOVE_PERMISSIONS =
                EnumSet.of(Permissions.SEND_MESSAGES);
        
        List<String> tokens = tokenize(event.getMessage().getContent());
        if (tokens.size() != 2) {
            sendMessageToSameChannel(event, "Incorrect number of arguments. Try !help.");
            return;
        }
        
        String channelName = tokens.get(1);
        IGuild guild = event.getMessage().getGuild();
        List<IRole> roles = guild.getRoles();
        
        try {
            IChannel channel = guild.createChannel(channelName);
            channel.changeTopic("Channel created by Inkademy Bot");

            for (IRole role : roles) {
                if (role.isEveryoneRole())
                    channel.overrideRolePermissions(role, EVERYONE_ADD_PERMISSIONS, EVERYONE_REMOVE_PERMISSIONS);
                else if (role.getName().equals("voiced"))
                    channel.overrideRolePermissions(role, VOICED_ADD_PERMISSIONS, EMPTY_SET);
                else if (role.getName().equals("mod"))
                    channel.overrideRolePermissions(role, VOICED_ADD_PERMISSIONS, EMPTY_SET);
                else if (role.getName().equals("illuminati"))
                    channel.overrideRolePermissions(role, VOICED_ADD_PERMISSIONS, EMPTY_SET);
            }

            ArchiveWriter writer = ArchiveWriter.createWriter(convertToArchivePath(channelName));
            ArchiveListener listener = ArchiveListener.createListener(channel, writer);
            client.getDispatcher().registerListener(listener);
            
            channel.sendMessage("This channel is now under archive");
            channel.sendMessage("Messages will be saved, however deletions and edits will not be");
            channel.sendMessage("Use !finish to close this channel");
            
            managedChannels.put(channel, listener);
        }
        catch (RateLimitException e) {
            System.err.println("Sending messages too quickly!");
            e.printStackTrace();
        }
        catch (DiscordException e) {
            System.err.println(e.getErrorMessage());
            e.printStackTrace();
        }
        catch (MissingPermissionsException e) {
            System.err.println("Missing permissions for channel!");
            e.printStackTrace();
        }
    }

    private void archiveCommand(MessageReceivedEvent event) {
        
        List<String> tokens = tokenize(event.getMessage().getContent());
        if (tokens.size() != 2)
            sendMessageToSameChannel(event, "Incorrect number of arguments. Try !help.");

        String channelName = tokens.get(1);
        String archivePath = convertToArchivePath(channelName);
        
        try {
            if (!ArchiveReader.archiveExists(archivePath))
                sendMessageToSameChannel(event, "No archive can be found for " + channelName);
            else
                ArchiveReader.uploadArchive(event.getMessage().getChannel(), archivePath);
        }
        catch (DiscordException e) {
            System.err.println(e.getErrorMessage());
            e.printStackTrace();
        }
        catch (RateLimitException e) {
            System.err.println("Sending messages too quickly!");
            e.printStackTrace();
        }
        catch (MissingPermissionsException e) {
            System.err.println("Missing permissions for channel!");
            e.printStackTrace();
        }
        catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void finishCommand(MessageReceivedEvent event) {

        List<String> tokens = tokenize(event.getMessage().getContent());
        if (tokens.size() != 1) {
            sendMessageToSameChannel(event, "Incorrect number of arguments. Try !help.");
            return;
        }
        
        IChannel channel = event.getMessage().getChannel();
        
        if (!managedChannels.containsKey(channel)) {
            sendMessageToSameChannel(event, "This is not an archived channel");
            sendMessageToSameChannel(event, "Only use !finish in channels created by me");
            return;
        }
        
        ArchiveListener listener = managedChannels.remove(channel);
        client.getDispatcher().unregisterListener(listener);
        try {
            channel.delete();
        }
        catch (RateLimitException e) {
            System.err.println("Sending messages too quickly!");
            e.printStackTrace();
        }
        catch (DiscordException e) {
            System.err.println(e.getErrorMessage());
            e.printStackTrace();
        }
        catch (MissingPermissionsException e) {
            System.err.println("Missing permissions for channel!");
            e.printStackTrace();
        }
    }

    private void listenCommand(MessageReceivedEvent event) {

        List<String> tokens = tokenize(event.getMessage().getContent());
        if (tokens.size() != 1) {
            sendMessageToSameChannel(event, "Incorrect number of arguments. Try !help.");
            return;
        }

        IChannel channel = event.getMessage().getChannel();

        if (managedChannels.containsKey(channel)) {
            sendMessageToSameChannel(event, "I'm already listening to this channel");
            return;
        }
        
        sendMessageToSameChannel(event, "Beginning archive of " + channel.getName());

        ArchiveWriter writer = ArchiveWriter.createWriter(convertToArchivePath(channel.getName()));
        ArchiveListener listener = ArchiveListener.createListener(channel, writer);
        client.getDispatcher().registerListener(listener);
        
        managedChannels.put(channel, listener);
    }
    
    private void ignoreCommand(MessageReceivedEvent event) {

        List<String> tokens = tokenize(event.getMessage().getContent());
        if (tokens.size() != 1) {
            sendMessageToSameChannel(event, "Incorrect number of arguments. Try !help.");
            return;
        }

        IChannel channel = event.getMessage().getChannel();

        if (!managedChannels.containsKey(channel)) {
            sendMessageToSameChannel(event, "I'm already not listening to this channel");
            return;
        }

        sendMessageToSameChannel(event, "Stopping archive of " + channel.getName());
        client.getDispatcher().unregisterListener(managedChannels.remove(channel));
    }

    private void listCommand(MessageReceivedEvent event) {

        List<String> tokens = tokenize(event.getMessage().getContent());
        if (tokens.size() != 1)
            sendMessageToSameChannel(event, "Incorrect number of arguments. Try !help.");
        
        try {
            ArchiveReader.listArchives(event.getMessage().getChannel());
        }
        catch (DiscordException e) {
            System.err.println(e.getErrorMessage());
            e.printStackTrace();
        }
        catch (RateLimitException e) {
            System.err.println("Sending messages too quickly!");
            e.printStackTrace();
        }
        catch (MissingPermissionsException e) {
            System.err.println("Missing permissions for channel!");
            e.printStackTrace();
        }
        catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void grepCommand(MessageReceivedEvent event) {

        List<String> tokens = tokenize(event.getMessage().getContent());
        if (tokens.size() != 3)
            sendMessageToSameChannel(event, "Incorrect number of arguments. Try !help.");
        
        String pattern = tokens.get(1);
        String path = convertToArchivePath(tokens.get(2));
        
        try {
            ArchiveReader.grepArchives(event.getMessage().getChannel(), pattern, path);
        }
        catch (DiscordException e) {
            System.err.println(e.getErrorMessage());
            e.printStackTrace();
        }
        catch (RateLimitException e) {
            System.err.println("Sending messages too quickly!");
            e.printStackTrace();
        }
        catch (MissingPermissionsException e) {
            System.err.println("Missing permissions for channel!");
            e.printStackTrace();
        }
        catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void sendMessageToSameChannel(MessageReceivedEvent event, String content) {

        IChannel channel = event.getMessage().getChannel();
        MessageBuilder message = new MessageBuilder(client).withChannel(channel).withContent(content);
        sendMessage(message);
    }

    private static void sendMessage(MessageBuilder message) {
        
        try {
            message.send();
        }
        catch (RateLimitException e) {
            System.err.println("Sending messages too quickly!");
            e.printStackTrace();
        }
        catch (DiscordException e) {
            System.err.println(e.getErrorMessage());
            e.printStackTrace();
        }
        catch (MissingPermissionsException e) {
            System.err.println("Missing permissions for channel!");
            e.printStackTrace();
        }
    }
    
    public static String convertToArchivePath(String channelName) {
        channelName = channelName.replaceAll("[^A-Za-z0-9_\\-\\*]", "");
        return new StringBuilder().append("archives/").append(channelName).append(".txt").toString();
    }
}
