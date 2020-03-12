import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class AddKiss extends Command 
{
	public AddKiss()
	{
		this.name = "kiss";
		this.category = BeepBoop.MOD;
	}
	
	@Override
	protected void execute(CommandEvent event) 
	{
		String fixed = event.getArgs().replaceAll("\\[Husband]", "[husband]")
									.replaceAll("\\[Wife]", "[wife]");
		
		if(!fixed.contains("[wife]") || !fixed.contains("[husband]"))
		{
			event.reply("You must include both `[husband]` and `[wife]` in the kiss!");
			return;
		}
		
		try {
			List<String> kisses = Files.readAllLines(UsefulMethods.findPath("kisses.txt"));
			kisses.add(fixed);
			Files.write(UsefulMethods.findPath("kisses.txt"), kisses, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		event.reply("**Alright, I've added that kiss for you!**");
	}

}
