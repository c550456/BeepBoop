import java.io.File;
import java.util.Random;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class EightBall extends Command 
{
	public EightBall()
	{
		this.name = "8ball";
		this.category = BeepBoop.FUN;
		this.help = "Summons the Magic 8 Ball to answer your questions";
		this.arguments = "<question>";
//		this.cooldown = 30;
	}
	@Override
	protected void execute(CommandEvent event) 
	{
		event.getMessage().delete().queue();


		File file = UsefulMethods.findFile("8ball", null);
		Random rand = new Random();

		while(file.isDirectory())
		{
			int choose = rand.nextInt(file.listFiles().length);
//			System.out.println("Looking at: " + file.listFiles()[choose].getName());
			if(!file.listFiles()[choose].getName().equals("Thumbs.db"))
			{
				file = file.listFiles()[choose];
			}
//			System.out.println("Picking..." + file.getName());
		}

//		System.out.println("Final file: " + file.getName());

		MessageEmbed embed = new EmbedBuilder()
				.setAuthor(event.getMember().getEffectiveName(), null, event.getAuthor().getEffectiveAvatarUrl())
				.addField(UsefulMethods.eightBall + " Question", event.getArgs(), false)
				.setColor(UsefulMethods.getMemberColor(event.getMember()))
				.setImage("attachment://" + file.getName())
				.build();


//		event.getTextChannel().sendFile(file, new MessageBuilder().setEmbed(embed).build()).queue();
		event.getTextChannel().sendMessage(embed).addFile(file).queue();
	}

}
