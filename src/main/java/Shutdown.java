import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class Shutdown extends Command 
{
	public Shutdown()
	{
		this.name = "shutdown";
		this.aliases = new String[] {"end", "exit"};
		this.requiredRole = "Moderator"; 
		this.category = BeepBoop.MOD;
		this.help = "Shuts down BeepBoop. Only available to staff";
		this.arguments = "BeepBoop";
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		if(event.getArgs().equalsIgnoreCase("BeepBoop"))
		{
			event.getTextChannel().sendMessage("**" + event.getSelfMember().getEffectiveName() + " shutting down**").complete();
			System.exit(0);
		}
	}
}
