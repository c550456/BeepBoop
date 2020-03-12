import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class Selfnickname extends Command 
{
	public Selfnickname()
	{
		this.name = "selfnick";
		this.ownerCommand = true;
		this.aliases = new String[] {"selfnickname"};
	}
	@Override
	protected void execute(CommandEvent event) 
	{
		try
		{
		event.getMessage().delete().queue();
		event.getGuild().modifyNickname(event.getGuild().getSelfMember(), event.getArgs()).queue(s -> {}, 
				f -> event.getJDA().getTextChannelById(BeepBoop.TESTCHANNELID).sendMessage(f.getMessage()).queue());
		}
		catch(InsufficientPermissionException ipe)
		{
			event.getJDA().getTextChannelById(BeepBoop.TESTCHANNELID).sendMessage(ipe.getMessage()).queue();
		}
	}

}
