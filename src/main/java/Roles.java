import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.util.*;

public class Roles extends Command 
{
	public Roles()
	{
		this.name = "roles";
		this.category = BeepBoop.MOD;
		this.help = "Returns a list of all roles on the server with their IDs";
	}
	
	@Override
	protected void execute(CommandEvent event) 
	{
		Guild guild = event.getGuild();
		List<String> roles = new ArrayList<>();
		
		for(Role r : guild.getRoles())
		{
			roles.add(r.getName() + ": " + r.getId());
		}
		
		UsefulMethods.displayList(roles, "**" + guild.getName() + " Roles**", event.getChannel(), event.getAuthor(), BeepBoop.waiter);
	}

}
