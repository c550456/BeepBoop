import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.awt.Color;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Logs extends Command 
{

	public Logs()
	{
		this.name = "log";
		this.aliases = new String[] {"logs", "audit", "auditlogs", "audits"};
		this.help = "Displays the logs for any linked entity (users, channels, emotes, roles), or all logs if no arguments are passed";
		this.category = BeepBoop.FUN;
	}


	@Override
	protected void execute(CommandEvent event) 
	{
		event.getTextChannel().sendTyping().complete();
		try
		{
		Guild guild = event.getGuild();
		String idSearch = "123";	//If searching all logs, null user is fine, but it won't check a null or empty string for the user ID
		
		String idString = "^<(?:@(?:!|&)?|#|a?:\\w+:)(\\d+)>$"; //Will match for any mentioned object. Group 1 will get the ID
		Pattern idPattern = Pattern.compile(idString);
		Matcher idMatcher = idPattern.matcher(event.getArgs());
		
		boolean allLogs = false;
		
		if(event.getArgs().isEmpty())
		{
			allLogs = true;
		}
		else if(idMatcher.find())
		{
			idSearch = idMatcher.group(1);
		}
		else
		{
			event.reply("You must provide something to search the logs for!");
			return;
		}
	
		

		List<String> logs = new ArrayList<>();
		for(AuditLogEntry log : guild.retrieveAuditLogs().cache(false))
		{
			boolean targetsObj =log.getTargetId().equals(idSearch);
			boolean objAuthor = log.getUser().getId() != null && log.getUser().getId().equals(idSearch);

			if(targetsObj || objAuthor || allLogs)
			{
				String oldValues = "";
				String newValues = "";
				for(AuditLogChange value : log.getChanges().values())
				{
					oldValues += value.getOldValue() == null ? "" : value.getKey().substring(0, 1).toUpperCase() + value.getKey().substring(1) + ": " + value.getOldValue() + "\n";
					newValues += value.getNewValue() == null ? "" : value.getKey().substring(0, 1).toUpperCase() + value.getKey().substring(1) + ": " + value.getNewValue() + "\n";
				}
				
				oldValues = checkValues(oldValues, guild);
				newValues = checkValues(newValues, guild);
			

				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' KK:mm a");
				String time = log.getTimeCreated().atZoneSimilarLocal(ZoneId.of("UTC")).format(dtf) + " UTC";

				String target = getTarget(log); 

				logs.add("**Action:** " + log.getType().toString().replaceAll("_", " ") + "\n**By:** " + log.getUser().getAsMention() + "\n**To: **" +  target /*" **with ID:** " + log.getTargetId()*/
						+ "\n**Old Values:**\n" + oldValues.trim() + "\n**New Values:**\n" + newValues.trim() + "\n**Time:** " + time 
						+ "\n**Log ID:** " + log.getId() + "\n");
			}
		}

		if(logs.isEmpty())
		{
			event.reply("There are no logs concerning that entity in the last 90 days!");
			return;
		}
		
		User user = event.getJDA().getUserById(idSearch); //possibly-null user
		Role role = guild.getRoleById(idSearch); //possibly-null role
		Color color = null;
		String title = idSearch.equals("123") ? "all events" : event.getArgs();
		if(user != null && guild.getMember(user) != null) //if it's a user
		{
			color = UsefulMethods.getMemberColor(guild.getMember(user));
			title = guild.getMember(user).getEffectiveName();
		}
		else if(role != null && !role.getId().equals(event.getGuild().getId())) //if it's a role and not the default role
		{
			color = guild.getRoleById(idSearch).getColor();
			title = role.getName();
		}
		
		UsefulMethods.displayList(logs, "Audit Logs Regarding: " + title, 3, event.getTextChannel(), event.getAuthor(), BeepBoop.waiter, color);
		
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	String getTarget(AuditLogEntry log)
	{
		String target = "";
		String targetId = log.getTargetId();
		Guild guild = log.getGuild();
		String defaultString = log.getTargetType() + " **with ID:** " + targetId;
		
		switch(log.getTargetType().toString())
		{
		case "MEMBER": target = " <@" + targetId + ">";  
		break;
		case "EMOTE": target = guild.getEmoteById(targetId) == null ? "(Now-deleted) " + defaultString : guild.getEmoteById(targetId).getAsMention();
		break;
		case "CHANNEL": target = guild.getTextChannelById(targetId) == null ? defaultString : guild.getTextChannelById(targetId).getAsMention();
		break;
		case "ROLE": target = guild.getRoleById(targetId) == null ? defaultString : guild.getRoleById(targetId).getAsMention();
		break;
		default: target = defaultString;
		}
		return target;
	}
	
	String checkValues(String values, Guild guild)
	{
		
		String roleString = "\\$(.*)\\[\\{name\\=(.*), id\\=(.*)\\}\\]";	//Group 1 is remove/add, Group 2 is the name, Group 3 is the ID
		Pattern rolePattern = Pattern.compile(roleString);
		Matcher m = rolePattern.matcher(values);
		
		String colorString = "Color: (\\d+)";		//Group 1 is the DECIMAL color code. Translate it to hex!
		Pattern colorPattern = Pattern.compile(colorString);
		Matcher m2 = colorPattern.matcher(values);
		
		String permsString = "((allow|deny|permissions)\\W\\s)(\\d+)";		//Group 1 is the allow/deny/permissions, Group 3 is the permissions value
		Pattern permsPattern = Pattern.compile(permsString, Pattern.CASE_INSENSITIVE);
		Matcher m3 = permsPattern.matcher(values);
		
		String overwritesString = "(allow|deny)=(\\d+)";	//Group 1 is the allow/deny, Group 2 is the permissions value
		Pattern overwritesPattern = Pattern.compile(overwritesString, Pattern.CASE_INSENSITIVE);
		Matcher m4 = overwritesPattern.matcher(values);
		
		String overwrites2String = "Permission_overwrites: \\[\\{"; //Group 1 is all we want. Remove the rest
		Pattern overwrites2Pattern = Pattern.compile(overwrites2String);
		Matcher m5 = overwrites2Pattern.matcher(values);
		
		if(m.find()) //if a role is added/removed
		{
			String firstWord = m.group(1).substring(0, 1).toUpperCase().concat(m.group(1).substring(1));
			String role = "";
			if(guild.getRoleById(m.group(3)) != null)
			{
				role = guild.getRoleById(m.group(3)).getAsMention();
			}
			else
			{
				role = m.group(2) + " **with ID:** " + m.group(3);
			}
			values = firstWord + role;
		}
		if(m2.find()) //if a color is mentioned
		{
			values = values.replaceAll(colorString, "Color: #" + Integer.toHexString(Integer.parseInt(m2.group(1))).toUpperCase());
		}
		while(m3.find()) //if "permissions" is mentioned
		{
			String perms = Permission.getPermissions(Long.parseLong(m3.group(3))).toString();
			values = values.replaceAll("(?i)"+m3.group(1)+"\\d+", m3.group(1) + perms.substring(1, perms.length()-1));
		}
		while(m4.find())	//if permission overrides are found
		{
			String perms = Permission.getPermissions(Long.parseLong(m4.group(2))).toString();
			values = values.replaceAll(m4.group(1)+"=\\d+", "**" + UsefulMethods.capitalize(m4.group(1)) + "**: " + perms.substring(1, perms.length()-1));
		}
		while(m5.find())
		{
			String whyDoINeedThisString = "\\[\\{(\\*\\*Allow\\*\\*:.*),\\sid.*";	//idk why, but this wouldn't work without doing it this way
			Pattern whyDoINeedThisPattern = Pattern.compile(whyDoINeedThisString);
			Matcher whyDoINeedThisMatcher = whyDoINeedThisPattern.matcher(values);
			while(whyDoINeedThisMatcher.find())
			{
				values = values.replaceAll(whyDoINeedThisString, whyDoINeedThisMatcher.group(1));
			}
		}
		if(values.isEmpty())
			values = "None";
		values = values.replaceAll("\\[\\]", "None");
		values = values.replaceAll("_", " ");
//		values = values;
		
		return values;
	}
}
