import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class Waiting extends Command
{
	public Waiting()
	{
		this.name = "waiting";
		this.category = BeepBoop.FUN;
	}

	@Override
	protected void execute(CommandEvent event)
	{
		try
		{
			event.getMessage().delete().queue();
			event.reply(UsefulMethods.findFile("we're waiting.gif", null), "waiting.gif");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
