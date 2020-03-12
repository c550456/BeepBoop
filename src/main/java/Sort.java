import java.util.Arrays;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class Sort extends Command 
{
	public Sort()
	{
		this.name = "sort";
		this.category = BeepBoop.FUN;
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		List<String> words = Arrays.asList(event.getArgs().split(",\\s+"));
		words.sort((a, b) ->
		{
			char aChar = a.charAt(0);
			char bChar = b.charAt(0);
			
			if(aChar < bChar)
				return -1;
			else if(aChar > bChar)
			{
				return 1;
			}
			else return 0;
		});
		
		String output = "";
		
		for(String word : words)
		{
			output += word + "\n";
		}
		event.reply(output);
	}

}
