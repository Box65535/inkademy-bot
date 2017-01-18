package bot;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

public class Main {
    
    public static void main(String[] args) {
        
        String token = args[0];
        try {
            IDiscordClient client = new ClientBuilder().withToken(token).login();
            InkademyBot bot = new InkademyBot(client);
        }
        catch (DiscordException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
