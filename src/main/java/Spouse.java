import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;

public class Spouse extends Command
{

	public Spouse()
	{
		this.name = "spouse";
		this.help = "Shows who a user is married to, if anyone";
		this.arguments = "@user";
		this.category = BeepBoop.FUN;
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		JDA jda = event.getJDA();
		Member spouse = null;
		String[] parts = event.getArgs().split("\\s+");
		EmbedBuilder embed = new EmbedBuilder();
		String description = "That user is not yet married!";
		Member checker = null;

		if(event.getArgs().isEmpty())
		{
			checker = event.getMember();
		}
		else
		{
			try
			{
				checker = event.getGuild().getMember(UsefulMethods.hasMention(parts[0], jda));
			} catch(NullPointerException e)
			{
				event.reply("I'm sorry, I could not find a user with the name: " + parts[0]);
				return;
			}
		}

		try
		{
			spouse = event.getGuild().getMember(CouplesMethods.getSpouse(checker.getUser(), jda));
		} catch(NullPointerException e)
		{
			
//			return;
		}

		if(spouse != null)
		{
			
			description = checker.getAsMention() + " is married to " + spouse.getAsMention() + "\n"
					+ "*(" + checker.getEffectiveName() + " is married to " + spouse.getEffectiveName() + ")*"
					+ "\n\n**Marriage Karma: " + CouplesMethods.getKarma(checker.getUser(), event.getJDA()) + "**";
			try {
			embed.setThumbnail(spouse.getUser().getEffectiveAvatarUrl());
			} catch (Exception e)
			{
				description = description + "\n*There was an error getting that user's avatar*";
			}
		}

		embed.setDescription(description);
		event.reply(embed.build());
	}
	
	

}
