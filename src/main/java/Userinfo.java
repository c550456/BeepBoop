import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

import java.util.*;

public class Userinfo extends Command 
{
	public Userinfo()
	{
		this.name = "userinfo";
		this.aliases = new String[] {"whois", "who"};
		this.category = BeepBoop.FUN;
		this.help = "Displays information about a user";
		this.arguments = "@user";
	}
	
	@Override
	protected void execute(CommandEvent event) 
	{
		Member member;
		if(event.getArgs().isEmpty())
		{
			member = event.getMember();
		}
		else if(UsefulMethods.hasMention(event.getArgs(), event.getJDA()) != null)
		{
			member = event.getGuild().getMember(UsefulMethods.hasMention(event.getArgs(), event.getJDA()));
		}
		else
		{
			event.reply("I'm sorry, I couldn't find user with name: " + event.getArgs());
			return;
		}
		
		String id = member.getUser().getId();
		String nick = member.getNickname() == null ? "None" : member.getNickname();
		DateTimeFormatter dtf = DateTimeFormat.forPattern("EEEE, MMMM dd, yyyy");
		DateTime registerDate = new DateTime(member.getUser().getTimeCreated().toString());
		String register = registerDate.toString(dtf);
		DateTime joinDate = new DateTime(member.getTimeJoined().toString());
		String join = joinDate.toString(dtf);
		String roles = "";
		
		for(Role r : member.getRoles())
		{
			roles += r.getName() + ", ";
		}
		roles = roles.substring(0, roles.length()-2);
		
		String status = member.getOnlineStatus().toString().replaceAll("_", " ");
		status = status.charAt(0) + status.substring(1).toLowerCase();
		for(int i = 1; i < status.length(); i++)
		{
			if(status.charAt(i) == ' ')
			{
				status = status.substring(0, i) + " " + status.substring(i+1, i+2).toUpperCase() + status.substring(i+2);
			}
		}
		
		String game = member.getActivities().isEmpty() ? "None" : getActivities(member.getActivities());
		
		List<Member> memberList = new ArrayList<>();
		for(Member m : event.getGuild().getMembers())
		{
			if(!m.getUser().isBot())
			{
				memberList.add(m);
			}
		}
		Collections.sort(memberList, (a,b) -> a.getTimeJoined().compareTo(b.getTimeJoined()));
		
		int joinPos = memberList.indexOf(member) + 1;
		
		MessageEmbed embed = new EmbedBuilder()
				.setAuthor(member.getEffectiveName(), member.getUser().getAvatarUrl(), member.getUser().getAvatarUrl())
				.setTitle(member.getUser().getName() + "#" + member.getUser().getDiscriminator() + " Information")
				.setThumbnail(member.getUser().getEffectiveAvatarUrl())
				.addField("ID", id, true)
				.addField("Nickname", nick, true)
				.addField("Game", game, true)
				.addField("Join Position", joinPos + "", true)
				.addField("Online Status", status, true)
				.addField("Server Join Date", join, true)
				.addField("Discord Join Date", register, true)
				.addField("Roles", roles, true)
				.setColor(UsefulMethods.getMemberColor(member))
				.build();
				
		event.reply(embed);
	}
	
	public String getActivities(List<Activity> activities)
	{
		String act = "";
		for(Activity a : activities)
		{
			act += a.getName() + ", ";
		}
		return act.substring(0,act.lastIndexOf(", "));
	}

}
