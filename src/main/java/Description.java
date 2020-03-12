import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.TextChannel;

public class Description extends Command 
{
	public Description()
	{
		this.name = "description";
		this.cooldownScope = CooldownScope.USER;
		this.category = BeepBoop.MOD;
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		TextChannel channel = event.getTextChannel();


		try
		{
			channel.getManager().setTopic(event.getArgs()).queue(null, fail -> event.reply("Invalid description!"));	
		} catch (Exception e)
		{
			event.reply("Invalid description!");
		}

	}

}
