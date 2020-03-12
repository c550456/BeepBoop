import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.events.emote.update.EmoteUpdateNameEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.entities.*;

public class EmoteTrackListener extends ListenerAdapter 
{
	@Override
	public void onMessageReceived(MessageReceivedEvent event)	//Tracks emote usage
	{
		//TODO: make this work with SQL and all guilds
		boolean dm = event.getChannelType() == ChannelType.PRIVATE;
		boolean isBot = event.getAuthor().isBot();
		boolean command = event.getMessage().getContentDisplay().startsWith("%");
		//		if(!event.getGuild().getId().equals(BeepBoop.GUILDID) || event.getAuthor().isBot() || event.getMessage().getContent().startsWith("%"))
		if(dm || isBot || command)
		{
			return;
		}
		String emoteIDString = "<:(\\S+?):(.+?)>";	//ID is group(2)
		Pattern emoteIDPattern = Pattern.compile(emoteIDString);
		Matcher m = emoteIDPattern.matcher(event.getMessage().getContentRaw());
		if(m.find())
		{
			Emote emote = event.getGuild().getEmoteById(m.group(2));
			if(emote != null)	//If the found emote is an emote of this server
			{
				try {
					boolean found = false;
					List <String> emotesList = Files.readAllLines(UsefulMethods.findPath("emotes.txt"));
					for(int i = 0; i < emotesList.size(); i++)
					{
						String line = emotesList.get(i);
						String[] parts = line.split(":\\s");
						if(emote.getName().equals(parts[0]))
						{
							int results = Integer.parseInt(parts[1]);
							emotesList.set(i, parts[0] + ": " + (results + 1)); 
							found = true;
							break;
						}

					}
					if(found == false)
					{
						emotesList.add(emote.getName() + ": " + 1);
					}
					Files.write(UsefulMethods.findPath("emotes.txt"), emotesList, StandardCharsets.UTF_8);
				} catch (IOException e) {
					event.getJDA().getTextChannelById(BeepBoop.TESTCHANNELID).sendMessage("UNABLE TO READ emotes.txt!").queue();
					return;
				} 
			}
		}
	}


	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event)		//Tracks reaction usage
	{
		Guild guild = event.getGuild();
		boolean isEmote = event.getReactionEmote().isEmote();	//Ensures that the reaction used is not a unicode emoji

		if(isEmote && event.getReactionEmote().getEmote().getGuild() == guild && event.getGuild() == guild)
		{
			Emote emote = event.getReactionEmote().getEmote();
			try {
				boolean found = false;
				List <String> reactList = Files.readAllLines(UsefulMethods.findPath("reacts.txt"));
				for(int i = 0; i < reactList.size(); i++)
				{
					String line = reactList.get(i);
					String[] parts = line.split(":\\s");
					if(emote.getName().equals(parts[0]))
					{
						int results = Integer.parseInt(parts[1]);
						reactList.set(i, parts[0] + ": " + (results + 1)); 
						found = true;
						break;
					}
				}
				if(found == false)
				{
					reactList.add(emote.getName() + ": " + 1);
				}
				Files.write(UsefulMethods.findPath("reacts.txt"), reactList, StandardCharsets.UTF_8);
			} catch (IOException e) {
				event.getJDA().getTextChannelById(BeepBoop.TESTCHANNELID).sendMessage("UNABLE TO READ emotes.txt!").queue();
				return;
			} 
		}
	}

	@Override
	public void onEmoteUpdateName(EmoteUpdateNameEvent event)	//Keeps emotes list updated with name changes
	{
		String oldName = event.getOldName();
		String newName = event.getNewName();

		List<String> emotes = UsefulMethods.getFileLines("emotes.txt");
		List<String> reactions = UsefulMethods.getFileLines("reacts.txt");

		for(int i = 0; i < emotes.size(); i++)
		{
			if(emotes.get(i).contains(oldName))
			{
				emotes.set(i, emotes.get(i).replaceAll(oldName, newName));
			}
		}

		for(int i = 0; i < reactions.size(); i++)
		{
			if(reactions.get(i).contains(oldName))
			{
				reactions.set(i, reactions.get(i).replaceAll(oldName, newName));
			}
		}

		try {
			Files.write(UsefulMethods.findPath("emotes.txt"), emotes, StandardCharsets.UTF_8);
			Files.write(UsefulMethods.findPath("reacts.txt"), reactions, StandardCharsets.UTF_8);
			event.getJDA().getTextChannelById(BeepBoop.TESTCHANNELID).sendMessage("**Changed " + oldName + " to " + newName + "**\nGuild: " + event.getGuild().getName()).queue();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
