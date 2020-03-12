import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class BanEveryone extends Command 
{
	public BanEveryone()
	{
		this.name = "baneveryone";
		this.ownerCommand = true;
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		if(event.getAuthor() != event.getGuild().getOwner().getUser())
		{
			event.reply("Only the server owner can use this command!");
			return;
		}
		
		event.reply("Are you sure you want to ban everyone? **__This is not a joke__** \n%yes or %no");
		new ResponseListener(this, event.getAuthor(), event.getAuthor(), "yes", "no", event.getTextChannel(), event.getJDA());
	}
	
	public static void dontBanEveryone(TextChannel channel)
	{
		channel.sendMessage("Okay, I won't ban everyone.").queue();
	}
	
	public static void banEveryone(TextChannel channel)
	{
		boolean isStaff = false;
		for(Member m : channel.getGuild().getMembers())
		{
			isStaff = false;
			for(Role r : m.getRoles())	//Check every role each member has to see if they have Moderator
			{
				if(r.getId().equals(BeepBoop.MODROLEID))
				{
					isStaff = true;
					break;
				}
			}
			//			Add a line to check a list of people
			if(!isStaff)
			{
//				channel.getGuild().getController().ban(m, 0).queue();
				channel.getGuild().ban(m, 0).queue();
			}
		}
	}

}
