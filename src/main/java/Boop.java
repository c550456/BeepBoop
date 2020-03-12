import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class Boop extends Command
{
	//TODO: make this work for EVERYONE, use database to store boops
	final String lunaID = "253259131157348353";
	final String buzzID = "284366103709351937";

	final String suggestionsChannelID = "307624726879272963";

	public Boop()
	{
		this.name = "boop";
		this.help = "Recreation of Bonfire's boops, without a cooldown.";
		this.arguments = "@user <additional boop description>";
		

	}

	@Override
	protected void execute(CommandEvent event) 
	{
		int numBoops = 0;
		String[] args = event.getArgs().split("\\s+", 2);
		String response = "";

		List<String> boops = new ArrayList<>();
		try {
			for(String line : Files.readAllLines(UsefulMethods.findPath("boops.txt")))
			{
				boops.add(line);
			}
		} catch (NullPointerException | IOException e) 
		{
			event.getJDA().getTextChannelById(BeepBoop.TESTCHANNELID).sendMessage("You done fucked up with Boops").complete();
			System.exit(0);
		}

		if(event.getAuthor().getId().equals(lunaID))
		{
			numBoops = Integer.parseInt(boops.get(0));
			boops.set(0, ""+(numBoops +1));
		}
		else
		{
			numBoops = Integer.parseInt(boops.get(1));
			boops.set(1, ""+(numBoops + 1));
		}

		response = getResponse(event, numBoops, args);
		event.reply(response);

		updateBoops(event, boops);
	}


	public void updateBoops(CommandEvent event, List<String> boops)
	{
		try 
		{
			Files.write(UsefulMethods.findPath("boops.txt"), boops, StandardCharsets.UTF_8);
		} catch (NullPointerException | IOException e) {
			event.getJDA().getTextChannelById(BeepBoop.TESTCHANNELID).sendMessage("Error writing to boops.txt!").complete();
			System.exit(0);
		}
	}

	public String getResponse(CommandEvent event, int numBoops, String[] args)
	{
		String response = "";

		if(args.length > 1)
		{
			response = event.getAuthor().getAsMention() + " has booped " + event.getMessage().getMentionedUsers().get(0).getAsMention() + " " + args[1] + "!"
					+ " That's " + numBoops + " times now!";
		}
		else
		{
			response = event.getAuthor().getAsMention() + " has booped " + event.getMessage().getMentionedUsers().get(0).getAsMention() + "!"
					+ " That's " + numBoops + " times!";
		}

		return response;
	}
}
