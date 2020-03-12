import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import java.util.*;
import net.dv8tion.jda.api.entities.TextChannel;

public class Perms extends Command 
{
	public Perms()
	{
		this.ownerCommand = true;
		this.name = "perms";
	}
	@Override
	protected void execute(CommandEvent event) 
	{
		List<String> channels = new ArrayList<>();
		for(TextChannel tc : event.getGuild().getTextChannels())
		{
			if(tc.canTalk())
				channels.add(tc.getName());
		}
		event.reply("I have permission to post in these channels: " + channels);
	}

}
