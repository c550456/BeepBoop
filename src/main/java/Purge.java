import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class Purge extends Command 
{
	public Purge()
	{
		this.name = "purge";
		this.category = BeepBoop.MOD;
		this.help = "Deletes between 1 and 100 messages";
		this.arguments = "<number of messages to delete>";
	}
	@Override
	protected void execute(CommandEvent event) 
	{
		String args = event.getArgs();
		TextChannel channel = event.getTextChannel();
		if(StringUtils.isNumeric(args) && Integer.parseInt(args) > 0)
		{
			int numMessages = Integer.parseInt(args);
			if(/*numMessages >= 2 && */numMessages <= 100)
			{
				//args + 1 to account for the message that was sent to execute the command
				List<Message> history = channel.getHistory().retrievePast(Integer.parseInt(args)+1).complete();
				channel.deleteMessages(history).queue();	
			}
			else
			{
				event.reply("Please use a number between 1 and 100");
			}
		}
		else
		{
			event.reply("You must enter a number greater than zero for this command!");
		}
	}

}
