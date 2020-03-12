import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class Birthday extends Command
{
	private String database = null;
	public Birthday()
	{
		this.name = "birthday"; 
		this.children = new Command[] {new BirthdayNext()};
		this.arguments = "<MM/DD>  ` **or** ` @user ` **or** ` next";
		this.help =  "\n		•`<MM/DD>` adds your birthday to the list. "
				+"\n		•`@user` displays the birthday of that user, if they are in the list. "
				+"\n		•`next` displays the closest birthday";
		this.category = BeepBoop.FUN;
	}

	@Override
	protected void execute(CommandEvent event) 
	{	
		database = event.getGuild().getId();

		if(event.getArgs().isEmpty())
		{
			displayBirthday(event,event.getMember().getId(), database);
			return;
		}
		String[] parts = event.getArgs().split("\\s+");
		if(parts.length > 1)
		{
			event.reply("You must use only one value with this command");
			return;
		}
		Pattern pattern = Message.MentionType.USER.getPattern();
		Matcher match = pattern.matcher(event.getArgs());
		User user = null;
		while(match.find())
		{
			user = event.getJDA().getUserById(match.group(1));
			if(user == null)
			{
				event.reply("I was unable to determine the user for this!");
				return;
			}
		}

		
		if(user == null)
		{
			user = event.getAuthor();
			addBirthday(event, user);
		}
		else //if(users.size() == 1 && (event.getArgs().equals(userMention) || event.getArgs().equals(memberMention))) 
			displayBirthday(event, user.getId(), database);

	}

	public void addBirthday(CommandEvent event, User user)
	{
		DateTimeFormatter bdayFormat = DateTimeFormat.forPattern("MM/dd").withLocale(Locale.US).withZoneUTC();
		try {
			if(event.getArgs().length() != 5)
				throw new ParseException("Must use the format of `MM/DD`", 0);
			DateTime bdt = bdayFormat.parseDateTime(event.getArgs())
					.withYear(DateTime.now().getYear())
					.withZone(DateTimeZone.UTC)
					.withMillisOfDay(0);
			String thisEntry = String.format("%02d/%02d", bdt.getMonthOfYear(), bdt.getDayOfMonth());
			
			DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd").withLocale(Locale.US).withZoneUTC();
			DateTime dt = dtf.parseDateTime(thisEntry).withYear(DateTime.now().getYear()).withZone(DateTimeZone.UTC);
			if(dt.withZone(DateTimeZone.UTC).withMillisOfDay(0).isBefore(DateTime.now().withZone(DateTimeZone.UTC).withMillisOfDay(0)))	//Ensures correct day of week
			{
				dt = dt.plusYears(1);
			}

			String sql = "EXEC AddBirthday\n"
						+ "@Birthday = '" + dt + "',\n" 
						+ "@UserId = '" + user.getId() + "'";
			Statement stmt = UsefulMethods.executeSQLProc(sql,database);

			SQLWarning warn = stmt.getWarnings();
			if(warn != null)
			{
				BeepBoop.logger.error(warn.getMessage());
				if(warn.getMessage().contains("AK_UserID"))
					event.reply("You already have a birthday stored!");
				else
				{
					String fullName = event.getJDA().getUserById(BeepBoop.STORMFATHERID).getName()
							+ "#" + event.getJDA().getUserById(BeepBoop.STORMFATHERID).getDiscriminator();
					event.reply("Oops, there was an error. Please contact " + fullName);
				}
			}
			else
				event.reply("Okay " + event.getMember().getAsMention() + ", I've added your birthday as " + getDay(bdt.withZone(DateTimeZone.UTC)));

		} 
		catch (Exception e) {
			event.reply(getErrorMessage(e));
		}

	}

	public static void displayBirthday(CommandEvent event, String userId, String database)
	{
		Member member = event.getGuild().getMemberById(userId);
		String birthdayRoleId = null;
		List<Role> roles = event.getGuild().getRoles();
		for(Role r : roles)
		{
			if(r.getName().toLowerCase().contains("birthday"))
			{
				birthdayRoleId = r.getId();
				break;
			}
		}
		EmbedBuilder bdayEmbed = new EmbedBuilder();
		bdayEmbed.setDescription(getBirthday(member, database));
		if(birthdayRoleId != null)
		{
			Role bdayRole = event.getGuild().getRoleById(birthdayRoleId);
			bdayEmbed.setColor(bdayRole.getColor());
		}
		try
		{
			bdayEmbed.setThumbnail(member.getUser().getEffectiveAvatarUrl());
		} catch(Exception e)
		{
			bdayEmbed.appendDescription("\n*There was an error getting the avatar for this user*");
		}
		MessageEmbed embed = bdayEmbed.build();
		event.reply(embed);
	}


	public static String getBirthday(Member member, String database)
	{
		String birthday = "That user has not registered a birthday";
		DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd").withLocale(Locale.US).withZoneUTC();
		DateTime dt = new DateTime().withZone(DateTimeZone.UTC);

		ResultSet results = null;
		try {
			results = UsefulMethods.runSQLQuery("SELECT dbo.GetBirthdayByUserId('"+member.getUser().getId()+"')",database);
			if(results.next() && results.getString(1) != null)
			{
				dt = dtf.parseDateTime(results.getString(1)).withYear(DateTime.now().getYear()).withZone(DateTimeZone.UTC);
				birthday = member.getAsMention() + "'s birthday is " + getDay(dt.withZone(DateTimeZone.UTC));
			}
		} catch (SQLException e) {
			BeepBoop.logger.error(e.getMessage());
		}

		return birthday;
	}

	public static String getDay(DateTime dt)
	{
		String day = "";
		if(dt.withZone(DateTimeZone.UTC).withMillisOfDay(0).isBefore(DateTime.now().withZone(DateTimeZone.UTC).withMillisOfDay(0)))	//Ensures correct day of week
		{
			dt = dt.plusYears(1);
		}
		dt = dt.withMillisOfDay(0);
		day = dt.withZone(DateTimeZone.UTC).dayOfWeek().getAsText() + ", " 
				+ dt.withZone(DateTimeZone.UTC).monthOfYear().getAsText() + " " 
				+ dt.withZone(DateTimeZone.UTC).dayOfMonth().getAsText();
		return day;
	}

	public String getErrorMessage(Exception e)
	{
		String error = "";
		if(e instanceof ParseException)
			error = "I'm sorry, that is not a valid input. Please enter your birthday as `MM/DD`";
		else if (e instanceof IllegalArgumentException)
		{
			error = "That is not a valid input!";
		}
		else if(e instanceof IllegalStateException)
		{
			error = "Your birthday is already in the list!";
		}
		else if(e instanceof SQLException)
		{
			error = "There was a problem with the database!";
			e.printStackTrace();
		}
		else 
		{
			error = "There was an issue adding you to the birthday list. Contact Stormfather for help.";
			e.printStackTrace();
		}
		return error;
	}
}
