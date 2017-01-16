package bot;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.api.events.EventSubscriber;

public class First {

    private IDiscordClient client;

    public static void main(String[] args) {

        String tkn = "MjcwMzM0NjQyNzkwNDY1NTM4.C14nVQ.Yn-VTcY7iQf7oEaGYX2vtQCpKII";
        new First().init(tkn);
    }

    public void init(String tkn){
        try {
            client = new ClientBuilder().withToken(tkn).login();
            EventDispatcher dispatcher = client.getDispatcher();
            dispatcher.registerListener(this);
//          System.out.println();
//          System.out.println(client.isLoggedIn());
//          System.out.println(client.isReady());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventSubscriber
    public void handle(ReadyEvent event) {

        System.out.println("Ready method!");
        System.out.println("Ready method!");
        System.out.println("Ready method!");
        System.out.println("Ready method!");
    }

    @EventSubscriber
    public void handle(MessageReceivedEvent event) {

        IMessage message = event.getMessage(); // Gets the message from the event object NOTE: This is not the content of the message, but the object itself
        IChannel channel = message.getChannel(); // Gets the channel in which this message was sent

        try {
            // Builds (sends) and new message in the channel that the original message was sent with the content of the original message.
            new MessageBuilder(this.client).withChannel(channel).withContent(message.getContent()).build();
        }
        catch (RateLimitException e) { // RateLimitException thrown. The bot is sending messages too quickly!
            System.err.print("Sending messages too quickly!");
            e.printStackTrace();
        }
        catch (DiscordException e) { // DiscordException thrown. Many possibilities. Use getErrorMessage() to see what went wrong.
            System.err.print(e.getErrorMessage()); // Print the error message sent by Discord
            e.printStackTrace();
        }
        catch (MissingPermissionsException e) { // MissingPermissionsException thrown. The bot doesn't have permission to send the message!
            System.err.print("Missing permissions for channel!");
            e.printStackTrace();
        }
    }

    @EventSubscriber
    public void createChannel(String channelName) {

    }
}

