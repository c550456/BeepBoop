import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.TextChannel;

public class Name extends Command 
{
	public Name()
	{
		this.name = "name";
		this.cooldownScope = CooldownScope.USER;
		this.category = BeepBoop.MOD;
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		TextChannel channel = event.getTextChannel();

		
			try
			{
				channel.getManager().setName(event.getArgs().replaceAll("\\s", "-")).queue(null, fail -> event.reply("Invalid name!"));	
			} catch (Exception e)
			{
				event.reply("Invalid name!");
			}
		
	}

}
