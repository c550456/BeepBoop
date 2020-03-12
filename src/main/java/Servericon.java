import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class Servericon extends Command 
{
	public Servericon() 
	{
		this.name = "servericon";
		this.category = BeepBoop.FUN;
		this.help = "Displays the server's icon";
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		String iconURL = event.getGuild().getIconUrl();
		String bigURL = iconURL + "?size=1024";
		BufferedImage image = UsefulMethods.urlToImage(bigURL);
		//		event.reply(iconURL);
		File outputFile = new File(UsefulMethods.findFile("avatar.png", null).getAbsolutePath());
		try 
		{
			ImageIO.write(image, "png", outputFile);
			event.reply(outputFile, "icon.png");
		} catch (IOException e) 
		{
			event.reply("There was a problem getting the image! Have a link instead.");
			e.printStackTrace();
		}

	}

}
