import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.User;

public class Stab extends Command 
{
	public Stab()
	{
		this.name = "stab";
		this.category = BeepBoop.FUN;
		this.cooldown = 5;
		this.cooldownScope = CooldownScope.GUILD;
	}
	@Override
	protected void execute(CommandEvent event) 
	{
		User auth = event.getAuthor();
		if(event.getArgs().isEmpty())
		{
			event.reply("You must specify who you want to stab");
			return;
		}
		
		User mentioned = UsefulMethods.hasMention(event.getArgs(), event.getJDA());
		if(mentioned != null)
		{
			if(mentioned.equals(auth))
			{
				event.reply("**Sudoku denied**");
				return;
			}
			User stab = event.getMessage().getMentionedUsers().get(0);
			String knife = UsefulMethods.knife;
			
			for(int i = 0; i < 4; i++)
			{
				event.getTextChannel().sendMessage(knife + stab.getAsMention()).queue(s -> s.delete().queueAfter(1, TimeUnit.SECONDS));
			}
			
			event.reply(knife + stab.getAsMention());
		}
	}

}
