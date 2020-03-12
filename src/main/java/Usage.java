import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class Usage extends Command
{
	public Usage()
	{
		this.name = "usage";
		this.category = BeepBoop.FUN;
		this.help = "Displays the emote and reaction usage of a custom emote from this server";
		this.arguments = "<custom server emote>";
	}
	
	@Override
	protected void execute(CommandEvent event) 
	{
//		Guild guild = event.getJDA().getGuildById(BeepBoop.GUILDID);
		String[] parts = event.getArgs().split("\\s+");
		String emoteIDString = "<:(\\S+?):(.+?)>";	//ID is group(2)
		Pattern emoteIDPattern = Pattern.compile(emoteIDString);
		Matcher match = emoteIDPattern.matcher(parts[0]);
		
		if(match.find())
		{
//			Emote emote = null;
//			if(guild.getEmoteById(match.group(2)) != null)
//			{
//				emote = guild.getEmoteById(match.group(2));
//			}
//			else
			{
				return;
			}
			
//			String name = emote.getName();
//			int emoteUse = 0;
//			int reactUse = 0;
//			List<String> reactList = new ArrayList<>();
//			List<String> emoteList = new ArrayList<>();
//			
//			try {
//				reactList = Files.readAllLines(UsefulMethods.findPath("reacts.txt"));
//				emoteList = Files.readAllLines(UsefulMethods.findPath("emotes.txt"));
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			
//			for(String line : emoteList)
//			{
//				String[] lineParts = line.split(": ");
//				if(lineParts[0].equals(name))
//					emoteUse = Integer.parseInt(lineParts[1]);
//			}
//			
//			for(String line : reactList)
//			{
//				String[] lineParts = line.split(": ");
//				if(lineParts[0].equals(name))
//					reactUse = Integer.parseInt(lineParts[1]);
//			}
//			
//			event.reply(emote.getAsMention() + " has been used as an emote " + emoteUse + " times."
//					+ "\n" + emote.getAsMention() + " has been used as a reaction " + reactUse + " times.");
		}
	}

}
