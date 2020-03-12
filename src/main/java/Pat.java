import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.User;

public class Pat extends Command 
{
	public Pat()
	{
		this.name = "pat";
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
			event.reply("You must specify who you want to pat");
			return;
		}
		
		User mentioned = UsefulMethods.hasMention(event.getArgs(), event.getJDA());
		if(mentioned != null)
		{
			if(mentioned.equals(auth))
			{
				event.reply("**You give yourself a pat on the back**");
				return;
			}
			User patted = event.getMessage().getMentionedUsers().get(0);
			String patEmote = event.getJDA().getGuildById(BeepBoop.MYSERVERID).getEmoteById("549801953903902753").getAsMention(); //stickfigure patting gif emote
			
			for(int i = 0; i < 4; i++)
			{
				event.getTextChannel().sendMessage(patEmote + patted.getAsMention()).queue(s -> s.delete().queueAfter(1, TimeUnit.SECONDS));
			}
			
			event.reply(patEmote + patted.getAsMention());
		}
	}

}
