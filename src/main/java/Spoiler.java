import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Message.Attachment;

public class Spoiler extends Command 
{
	public Spoiler()
	{
		this.name = "spoiler";
		this.category = BeepBoop.FUN;
		this.help = "Turns an attached image into a spoiler-tagged image";
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		Attachment atc = null;
		if(!event.getMessage().getAttachments().isEmpty()) //If there is at least one attachment
		{
			atc = event.getMessage().getAttachments().get(0);
		}
		else
		{
			event.reply("You must provide an attachment for this command!");
			return;
		}

		File file = new File(atc.getFileName());
		atc.downloadToFile(file);
		event.getMessage().delete().queue();
		event.getTextChannel().sendFile(file, "SPOILER_" + file.getName()).queue(s -> 
		{
			try {
				Files.delete(file.toPath());
			} catch (IOException e) {
				event.reply(e.getMessage());
			}
		});
	}

}



