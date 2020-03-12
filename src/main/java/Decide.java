import java.util.*;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class Decide extends Command
{

	public Decide()
	{
		this.name = "decide";
		this.aliases = new String[] {"choose"};
		this.help = "Randomly chooses one of the user-given options. There is no limit to the number of options."
				+ "\nThis definitely isn't rigged.";
		this.arguments = "<Option 1> or <Option 2> or <Option 3>...";
		this.category = BeepBoop.FUN;
	}
	
	@Override
	protected void execute(CommandEvent event) 
	{
		String[] args = event.getArgs().split("\\s+or\\s+");
		
		for(String s : args)	//Check for Starco
		{
			if(s.trim().equalsIgnoreCase("Starco"))
			{
				event.reply("You should choose " + s);
				return;
			}
		}
		
		Random rand = new Random();
		int choose = rand.nextInt(args.length);
		event.reply("You should choose " + args[choose]);
	}
	
}
