import java.io.File;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class Luna extends Command 
{

	public Luna()
	{
		this.name = "luna";
		this.category = BeepBoop.FUN;
	}
	@Override
	protected void execute(CommandEvent event) 
	{
		File file = UsefulMethods.findFile("luna.gif", null);
		event.reply(file, "luna_irl.gif");
	}

}
