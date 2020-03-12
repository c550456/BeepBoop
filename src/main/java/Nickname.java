import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class Nickname extends Command 
{

	public Nickname() 
	{
		this.name = "nick";
		this.aliases = new String[] {"nickname"};
		this.category = BeepBoop.FUN;
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		if (event.getArgs().isEmpty())
		{
			event.reply("Must include a user and a new name!");
			return;
		}

		String[] parts = event.getArgs().split("\\s+");
		if(parts.length < 2)
		{
			event.reply("Must include a user and a new name!");
			return;
		}

		User user = UsefulMethods.hasMention(parts[0], event.getJDA());
		if(user != null)
		{
			try
			{
				Guild guild = event.getGuild();
				String newName = "";
				for(int i = 1; i < parts.length; i++)
				{
					newName += parts[i] + " ";
				}
				guild.modifyNickname(guild.getMember(user), newName).queue(null, 
						f -> event.getJDA().getTextChannelById(BeepBoop.TESTCHANNELID).sendMessage(f.getMessage()).queue());
			}
			catch(InsufficientPermissionException ipe)
			{
				event.getJDA().getTextChannelById(BeepBoop.TESTCHANNELID).sendMessage(ipe.getMessage()).queue();
			}
			catch(Exception e)
			{
				event.reply(e.getMessage());
				return;
			}
		}
		else
		{
			event.reply("I couldn't find that user!");
		}
	}

}
