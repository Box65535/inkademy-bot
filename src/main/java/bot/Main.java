package bot;

import command.CommandHandler;
import command.InkademyCommandHandler;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

/**
 * Created by Box on 1/16/2017.
 */
public class Main {
    
    public static void main(String[] args) {
        
        String token = args[0];
        try {
            IDiscordClient client = new ClientBuilder().withToken(token).login();
            CommandHandler inkademyHandler = InkademyCommandHandler.createHandler(client);
            CommandListener listener = CommandListener.createListener(inkademyHandler);
            client.getDispatcher().registerListener(listener);
        }
        catch (DiscordException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
