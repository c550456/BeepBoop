import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class Hug extends Command
{
	public Hug()
	{
		this.name = "hug";
		this.category = BeepBoop.FUN;
		this.help = "Allows one user to hug another";
		this.arguments = "@user";
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		String[] args = event.getArgs().split("\\s+");
		List<Member> mentions = new ArrayList<>();
		
		for(String s : args)
		{
			User user = UsefulMethods.hasMention(s, event.getJDA());
			if(user != null && !mentions.contains(event.getGuild().getMember(user)))
			{
				mentions.add(event.getGuild().getMember(user));
			}
			else if(user == null && args.length == 1)
			{
				event.reply("Sorry, could not find a user with the name: " + s);
				return;
			}
		}
		event.reply(getResponse(mentions));
		
	}
	
	public String getResponse(List<Member> toHug)
	{
		String response = "";
		List<String> hugs = new ArrayList<>();
		List<String> toHugNames = new ArrayList<>();
		
		try {
			hugs.addAll(Files.readAllLines(UsefulMethods.findPath("hugs.txt")));
		} catch (NullPointerException | IOException e1) 
		{
			System.exit(0);
		}
		
		for(Member m : toHug)
		{
			toHugNames.add(m.getEffectiveName());
		}
		
		
		if(toHugNames.size() > 1)
		{
			String lastHug = toHugNames.get(toHugNames.size()-1);
			toHugNames.remove(toHugNames.size()-1);
			toHugNames.add("and " + lastHug);
		}
		
		
		Random rand = new Random();
		int choose = rand.nextInt(hugs.size());
		response = "*" + hugs.get(choose).replaceAll("\\[user]", Matcher.quoteReplacement(toHugNames.toString().substring(1, toHugNames.toString().length()-1))) + "*";
		return response;
	}
}
