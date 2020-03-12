import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class Serverinfo extends Command 
{

	public Serverinfo()
	{
		this.name = "serverinfo";
		this.category = BeepBoop.FUN;
		this.help = "Displays the info for this server.";
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		Guild server = null;
		if(event.getArgs().isEmpty())
		{
			server = event.getGuild();
		}
		else
		{
			server = event.getJDA().getGuildById(event.getArgs());
		}

		//		System.out.println("server gotten: " + server.getName());
		EmbedBuilder info = new EmbedBuilder();
		DateTimeFormatter dtf = DateTimeFormat.forPattern("EEEE, MMMM dd, yyyy");

		try
		{
			info.setTitle(server.getName(), null);
			info.setThumbnail(server.getIconUrl());
			info.addField("Owner", server.getOwner().getEffectiveName(), true);
			info.addField("ID", server.getId(), true);

			DateTime creationTime = new DateTime(server.getTimeCreated().toString());
			info.addField("Date Created", creationTime.toString(dtf), true);


			info.addField("Channels", 
					server.getTextChannels().size() + " text, " + server.getVoiceChannels().size() + " voice",
					true);
			info.addField("Region", server.getRegion().getName(), true);
			List<Member> members = server.getMembers();
			int onlineMembers = 0;
			for(Member m : members)
			{
				if(m.getOnlineStatus().equals(OnlineStatus.ONLINE))
				{
					onlineMembers++;
				}
			}
			info.addField("Members", "" + server.getMembers().size() + " (" + onlineMembers + " online)", true);
			info.addField("Emotes", server.getEmotes().size()+"", true);
			info.addField("Roles", server.getRoles().size()+"", true);
			event.reply(info.build());
		} catch (Exception e)
		{
			event.getJDA().getTextChannelById("307755754964647947").sendMessage("ERROR GETTING SERVER INFO\n**" + e.getMessage() + "**").queue();
			e.printStackTrace();
		}
	}

}
