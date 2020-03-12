import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Guild;

public class Launch extends Command 
{
	final static String launchEmoteID = "395355172139827201";
	final static String thinkingDakotaID = "338885290192011274";
	
	public Launch()
	{
		this.name = "launch";
		this.category = BeepBoop.EMOTES;
		this.help = "Posts the \"launch the missiles now!\" animated emote";
	}
	
	@Override
	protected void execute(CommandEvent event) 
	{
		event.getMessage().delete().queue();
		Guild server = event.getJDA().getGuildById(BeepBoop.MYSERVERID);
		
		event.reply("<a:launchthemissilesnow:" + server.getEmoteById(launchEmoteID).getId() + ">");
	}

}
