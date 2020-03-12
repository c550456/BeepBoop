import java.util.*;
import java.util.regex.*;

import org.apache.commons.lang3.math.NumberUtils;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;

public class Roll extends Command 
{
	public Roll()
	{
		this.name = "roll";
		this.aliases = new String[] {"dice", "die", "r"};
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		if(event.getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_MANAGE))
		{
			event.getMessage().delete().queue();
		}

		List<String> args = new ArrayList<>(Arrays.asList(event.getArgs().split("d", 2)));
		args.removeAll(Collections.singleton(""));
		int rolls = 1;
		int sides = 20;
		String title = "";


		if(!args.isEmpty() && args.size() < 2)
		{
			event.reply("You must enter valid inputs! For example: ```" + BeepBoop.PREFIX + "r 4 d20```");
			return;
		}
		else if(!args.isEmpty())
		{
			if(NumberUtils.isCreatable(args.get(0).trim())) //first arg isNumber
			{
				rolls = NumberUtils.toInt(args.get(0).trim(), 1);
			}

			if(args.size() > 1)
			{
				String sidesString = "^(\\d+)(.*)"; //Group 1 is the number of sides, Group 2 is the title
				Pattern sidesPattern = Pattern.compile(sidesString);
				Matcher m = sidesPattern.matcher(args.get(1).trim());

				if(m.find())
				{
					sides = NumberUtils.toInt(m.group(1), 20);
					if(m.groupCount() > 1 && !m.group(2).isEmpty())
					{
						title = m.group(2).trim();
					}
				}
				else
				{
					event.reply("You must enter valid inputs! For example: ```" + BeepBoop.PREFIX + "r 4 d20```");
					return;
				}
			}
		}


		event.reply(event.getAuthor().getAsMention() + " " + UsefulMethods.gameDie + "\n**" + roll(rolls, sides, title));

	}

	public String roll(int numRolls, int dieSides, String title)
	{
		int total = 0;

		if(title.isEmpty())
		{
			title = numRolls + "d" + dieSides;
		}

		Random rand = new Random();
		List<String> rolls = new ArrayList<>();

		for(int i = 0; i < numRolls; i++)
		{
			int thisRoll = rand.nextInt(dieSides) + 1;
			total += thisRoll;
			String toAdd = (thisRoll == dieSides || thisRoll == 1) ? "**" + thisRoll + "**" : thisRoll + "";
			rolls.add(toAdd);
		}

		title = title + ":** " + rolls.toString();
		String finalResult = numRolls > 1 ? title + "\n**Total:** " + total : title;

		return finalResult;
	}
}
