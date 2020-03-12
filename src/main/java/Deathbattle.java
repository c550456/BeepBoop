import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

public class Deathbattle extends Command 
{
	public Deathbattle()
	{
		this.name = "deathbattle";
		this.category = BeepBoop.FUN;
		this.help = "Creates a Death Battle image of two users. @ only one user to use yourself as the other, or @ two to specify both";
		this.arguments = "<@user> <@user>";
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		JDA jda = event.getJDA();
		if(event.getArgs().isEmpty())
		{
			event.reply("Please enter a user or users for Death Battle");
			return;
		}
		String[] parts = event.getArgs().split("\\s+");
		
		User userA = UsefulMethods.hasMention(parts[0], jda);
		User userB = null;
		if(parts.length > 1)
		{
			userB = UsefulMethods.hasMention(parts[1], jda);
		}
		
		boolean both = (userA != null && userB != null);

		if(!both)
		{
			if(parts.length > 1)
			{
				if(userA == null)
				{
					event.reply("Could not find user with name: " + parts[0]);
					return;
				}
				else if(userB == null)
				{
					event.reply("Could not find user with name: " + parts[1]);
					return;
				}
			}
			else if(parts.length < 2) 
			{
				if(userA == null)
				{
					event.reply("Could not find user with name: " + parts[0]);
					return;
				}
				else
				{
					userB = event.getAuthor();
				}
			}
		}
		
		event.getChannel().sendTyping().queue();
		BufferedImage avatar = UsefulMethods.urlToImage(userA.getEffectiveAvatarUrl()+"?size=1024");
		avatar = UsefulMethods.resizeByPixel(avatar, 360, 375);
		File outputfile = UsefulMethods.findFile("outputfile.png", null);
		BufferedImage background = null;
		try {
			background = ImageIO.read(UsefulMethods.findFile("deathbattle.png", null));
		} catch (NullPointerException | IOException e) {
			e.printStackTrace();
		}
		
		BufferedImage image = UsefulMethods.overlayImage(background, avatar, null, null, null, 0, 21, 193, 0, 0);
		
		avatar = UsefulMethods.urlToImage(userB.getEffectiveAvatarUrl()+"?size=1024");
		avatar = UsefulMethods.resizeByPixel(avatar, 360, 375);
		image = UsefulMethods.overlayImage(image, avatar, null, null, null, 0, 520, 192, 0, 0);
		
		try {
			ImageIO.write(image, "png", outputfile);
			event.reply(outputfile, "DeathBattle.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
