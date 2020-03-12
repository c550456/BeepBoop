import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Member;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class Divorce extends Command 
{
	public Divorce()
	{
		this.name = "divorce";
		this.help = "Divorces you from your spouse. Must be agreed upon by both parties.";
		this.category = BeepBoop.COUPLES;
		this.cooldownScope = Command.CooldownScope.USER;
		this.cooldown = 60;
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		List<String> couples = new ArrayList<>();
		Member a = event.getMember();
		Member b = null;
		int karma = 0;
		try {
			couples = Files.readAllLines(UsefulMethods.findPath("couples.txt"));
		} catch (IOException e) 
		{
			e.printStackTrace();
		}

		for(String line : couples)
		{
			String[] parts = line.split(", ");
			if(parts[0].contains(a.getUser().getId()))
			{
				b = event.getGuild().getMemberById(parts[1]);
				karma = Integer.parseInt(parts[2]);
			}
			else if(parts[1].contains(a.getUser().getId()))
			{
				b = event.getGuild().getMemberById(parts[0]);
				karma = Integer.parseInt(parts[2]);
			}
		}

		if(DivorceListener.pending.contains(a) || DivorceListener.pending.contains(b))
		{
			event.reply("Your marriage already has a divorce request!");
			return;
		}
		else if(karma < 100)
		{
			event.reply(b.getAsMention() + ", " + a.getAsMention() + " wants a divorce! Do you accept?\n%yes or %no");
			event.getJDA().addEventListener(new DivorceListener(a, b, event.getJDA()));
		}
		else
		{
			event.reply("Your marriage karma is too high! Clearly you are happy together.");
		}
	}

}
