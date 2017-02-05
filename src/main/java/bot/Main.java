package bot;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import model.InkademyDynamoModel;
import model.InkademyModel;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

public class Main {
    
    public static void main(String[] args) {
        
        String token = args[0];
        try {
            AmazonDynamoDBClient dynamoClient = new AmazonDynamoDBClient();
            dynamoClient.setRegion(Region.getRegion(Regions.US_WEST_2));
            InkademyModel model = new InkademyDynamoModel(dynamoClient);
            InkademyCoordinator bot = new InkademyCoordinator(model);

            IDiscordClient discordClient = new ClientBuilder().withToken(token).login();
            discordClient.getDispatcher().registerListener(bot);
        }
        catch (DiscordException e) {
            e.printStackTrace();
            System.exit(1);
        }
        catch (ResourceNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
