import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.User;

import java.awt.Color;

import org.apache.commons.lang3.StringUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class ArbitoreInitiative extends Command 
{
	private User user = null;
	private static ArbitoreInitiativeListener listener = new ArbitoreInitiativeListener();

	public ArbitoreInitiative()
	{
		this.name = "initiative";
		this.category = BeepBoop.FUN;
		this.aliases = new String[] {"censor", "arbitore"};
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		String brumID = "160557716287258624";
		String stormID = "165534549135196160";

		if(!event.getAuthor().getId().equals(brumID) && 
				!event.getAuthor().getId().equals(stormID))
		{
			return;
		}

		if(!StringUtils.isNumeric(event.getArgs()))
		{
			//Non-number input
			boolean off = event.getArgs().equals("off");
			if(off)
			{
				user = null;
				listener.clearUsers();
				event.getJDA().removeEventListener(listener);
			}
			else
			{
				event.reply("That is not a valid user ID!");
			}
		}
		else
		{
			try
			{
				user = event.getJDA().getUserById(event.getArgs());
				if(user == null || event.getGuild().getMember(user) == null)
				{
					throw new NumberFormatException();
				}
				else
				{
					MessageEmbed embed = new EmbedBuilder()
							.appendDescription("**Censoring " + event.getGuild().getMember(user).getAsMention() + "**")
							.setColor(Color.decode("#1ed760")) //Spotify green
							.build();
					listener.addUser(user);
					event.reply(embed);
					if(!event.getJDA().getRegisteredListeners().contains(listener))
						event.getJDA().addEventListener(listener);
				}

			} 
			catch(NumberFormatException nfe)
			{
				nfe.printStackTrace();
				event.reply("That is not a valid ID!");
			}
			catch(NullPointerException npe)
			{
				event.reply("There was an error getting that user!");
			}
		}


	}



}
