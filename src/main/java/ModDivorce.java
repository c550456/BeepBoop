import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.User;

public class ModDivorce extends Command 
{
	public ModDivorce()
	{
		this.name = "moddivorce";
		this.category = BeepBoop.MOD;
		this.help = "Forces a user to divorce their spouse. Should not be used.";
		this.arguments = "<@user>";
	}
	
	@Override
	protected void execute(CommandEvent event) 
	{
		User user = UsefulMethods.hasMention(event.getArgs(), event.getJDA());
		if(user != null && CouplesMethods.hasSpouse(user, event.getJDA()))
		{
			event.reply(user.getAsMention() + " has been divorced from " + CouplesMethods.getSpouse(user, event.getJDA()).getAsMention());
			CouplesMethods.divorce(user);
		}
	}

}
