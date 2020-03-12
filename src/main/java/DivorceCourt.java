import java.util.Random;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.User;

public class DivorceCourt extends Command
{
	
	public DivorceCourt()
	{
		this.name = "court";
		this.aliases = new String[] {"divorcecourt", "911"};
		this.help = "Unable to divorce your spouse? Let the judges decide.";
		this.category = BeepBoop.COUPLES;
		this.cooldown = 30;

	}

	@Override
	protected void execute(CommandEvent event) 
	{
		User spouse = CouplesMethods.getSpouse(event.getAuthor(), event.getJDA());
		if(event.getGuild().getMember(spouse).getOnlineStatus() == OnlineStatus.OFFLINE)
		{
			event.reply("Your spouse is offline! You cannot take them to court.");
			return;
		}
		
		if(CouplesMethods.getSpouseBattles(event.getAuthor()) >= 5 && CouplesMethods.getKarma(event.getAuthor(), event.getJDA()) == 0)
		{
			Random rand = new Random();
			int choice = rand.nextInt(2);
			if(choice == 0)
			{
				event.reply("The court has found no evidence suggesting that your marriage is deserving of a forced divorce."
						+ " It has been decided that you shall stay married.");
			}
			else
			{
				event.reply("The court has determined that your marriage is broken and abusive. You have been forcibly divorced.");
				CouplesMethods.divorce(event.getAuthor());
			}
		}
		else
		{
			event.reply("Your marriage is too happy for the courts to worry about you.");
		}
	}
	
}
