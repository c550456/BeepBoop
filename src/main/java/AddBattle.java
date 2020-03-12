import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class AddBattle extends Command 
{
	public AddBattle()
	{
		this.name = "battle";
		this.category = BeepBoop.MOD;
	}
	
	@Override
	protected void execute(CommandEvent event) 
	{
		String fixed = event.getArgs().replaceAll("\\[Winner]", "[winner]")
									.replaceAll("\\[Loser]", "[loser]");
		
		if(!fixed.contains("[winner]") && !fixed.contains("[loser]"))
		{
			event.reply("You must include `[winner]` or `[loser]` in the battle!");
			return;
		}
		
		try {
			List<String> battles = Files.readAllLines(UsefulMethods.findPath("battles.txt"));
			battles.add(fixed);
			Files.write(UsefulMethods.findPath("battles.txt"), battles, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		event.reply("**Alright, I've added that battle for you!**");
	}

}
