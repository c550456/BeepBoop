import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class Avatar extends Command 
{
	public Avatar()
	{
		this.name = "avatar";
		this.aliases = new String[] {"pfp"};
		this.arguments = "<@user> or <nickname> or <username>";
		this.help = "Displays a user's avatar";
		this.category = BeepBoop.FUN;
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		event.getChannel().sendTyping().queue();
		Member member = null;
		String[] parts = event.getArgs().split("\\s+");

		if(event.getArgs().isEmpty())
		{
			member = event.getMember();
		}
		else
		{
			try
			{
				member = event.getGuild().getMember(UsefulMethods.hasMention(parts[0], event.getJDA()));
			} catch(NullPointerException e)
			{
				//				System.out.println("Member is not mention tagged...");
				try
				{
					member = UsefulMethods.findMember(event.getGuild(), event.getArgs());
				} catch(NullPointerException e1)
				{
					System.out.println("null member returned");
					event.reply("I'm sorry, I could not find a user with the name " + event.getArgs());
					return;
				}
			}
		}

		if(member == null)
		{
			event.reply("I'm sorry, I could not find a user with the name " + event.getArgs());
			return;
		}

		File outputfile = null;
		String bigURL = (member.getUser().getEffectiveAvatarUrl() + "?size=1024");
		BufferedImage image = null;
		try
		{
			image = UsefulMethods.urlToImage(bigURL);
		} catch (Exception e)
		{
			event.reply("There was an error getting that avatar! Have a link instead.\n" + bigURL);
			System.out.println("Error: " + e.getMessage() + "\nCause: " + e.getCause());
//			e.printStackTrace();
			return;
		}
		TextChannel channel = event.getTextChannel();

		try{
			if(member.getUser().getEffectiveAvatarUrl().endsWith("gif"))
			{
				channel.sendTyping().queue();
				String url = member.getUser().getEffectiveAvatarUrl()+ "?size=1024";
				String smallURL = member.getUser().getEffectiveAvatarUrl();

				outputfile = UsefulMethods.urlToGif(url, UsefulMethods.findFile("avatar.gif", null));
				if(outputfile.length() > 8388608)
				{
					channel.sendTyping().queue();
					outputfile = UsefulMethods.urlToGif(smallURL, UsefulMethods.findFile("avatar.gif", null));
				}
				if(outputfile.length() < 8388608)
					channel.sendFile(outputfile).queue();
				else
					event.reply("That file is too big for me to send! Have a link instead.\n" + smallURL);
			}
			else
			{
				outputfile = new File(UsefulMethods.findFile("avatar.png", null).getAbsolutePath());
				ImageIO.write(image, "png", outputfile);
				event.reply(outputfile, "avatar.png");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException iae)
		{
			channel.sendMessage("File is too big to send!").queue();
		}

	}

}
