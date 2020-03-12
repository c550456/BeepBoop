import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class AddHug extends Command 
{
	public AddHug()
	{
		this.name = "hug";
		this.category = BeepBoop.MOD;
	}
	
	@Override
	protected void execute(CommandEvent event) 
	{
		String fixed = event.getArgs().replaceAll("\\[User]", "[user]");
		if(!fixed.contains("[user]"))
		{
			event.reply("You must include `[user]` in the hug!");
			return;
		}
		
		try {
			List<String> hugs = Files.readAllLines(UsefulMethods.findPath("hugs.txt"));
			hugs.add(fixed);
			Files.write(UsefulMethods.findPath("hugs.txt"), hugs, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		event.reply("**Alright, I've added that hug for you!**");
	}

}
