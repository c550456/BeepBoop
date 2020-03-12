import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;

public class Say extends Command 
{
	public Say()
	{
		this.name = "say";
		this.category = BeepBoop.FUN;
		this.help = "Makes BeepBoop say something";
		this.arguments = "<whatever you want BeepBoop to say>";
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		event.getMessage().delete().queue();
		event.reply(event.getArgs());
		
		event.getJDA().getTextChannelById(BeepBoop.TESTCHANNELID).sendMessage(new EmbedBuilder()
				.appendDescription(event.getGuild().getMember(event.getAuthor()).getEffectiveName() + " made me say:\n**" + event.getArgs() + "**")
				.setAuthor(event.getGuild().getMember(event.getAuthor()).getEffectiveName(), null, event.getAuthor().getEffectiveAvatarUrl())
				.setFooter("Channel: " + event.getTextChannel().getName(), event.getAuthor().getEffectiveAvatarUrl())
				.setTimestamp(event.getMessage().getTimeCreated())
				.setColor(event.getGuild().getMember(event.getAuthor()).getColor())
				.build()).queue();
	}

}
