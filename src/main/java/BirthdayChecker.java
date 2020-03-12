import java.util.*;
import java.util.concurrent.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class BirthdayChecker 
{
	final String birthdayRoleID = "316477719183491073";
	final String newsChannelID = "307726630061735936";

	JDA jda = null;


	public BirthdayChecker(JDA jda)
	{
		this.jda = jda;
		final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

		final Runnable checker = new Runnable() 
		{
			public void run() 
			{ 
				try
				{
					checkBirthdays(jda); 
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};

		try
		{
			scheduler.scheduleAtFixedRate(checker, 0, 1, TimeUnit.HOURS);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}


	public void checkBirthdays(JDA jda)	
	{
		//TODO: remove birthday role from people when it's no longer their birthday
		try {
			//			Map<String,List<String>> birthdays = new HashMap<>();
			List<String> users = new ArrayList<>();
			String currentDb = null;
			String birthdayRoleId = null;
			boolean assignBirthdayRole = false;
			String birthdayChannelId = null;
			for(Guild guild : jda.getGuilds())
			{
				currentDb = guild.getId();
				//Get birthday-related settings
				ResultSet results = UsefulMethods.runSQLQuery("SELECT * FROM dbo.GetBirthdaySettings()", currentDb);
				if(results.next())
				{
					birthdayChannelId = results.getString("BirthdayChannelId");
					birthdayRoleId = results.getString("BirthdayRoleId");
				}
				results = UsefulMethods.runSQLQuery("SELECT dbo.GetTodayBirthdays() as 'Users'", currentDb);
				if(results.next() && results.getString("Users") != null) 
				{
					users = Arrays.asList(results.getString("Users").split(","));
				}

				//Assign birthday role, if possible
				if(birthdayRoleId == null || guild.getRoleById(birthdayRoleId) == null)
				{
					for(Role r : guild.getRoles())
					{
						if(r.getName().toLowerCase().contains("birthday"))
						{
							birthdayRoleId = r.getId();
							break;
						}
					}
				}
				if(birthdayRoleId != null && guild.getRoleById(birthdayRoleId) != null)
				{
					//Once we have a birthday role, make sure one of the bot's roles can assign it
					for(Role botRole : guild.getMember(jda.getSelfUser()).getRoles())
					{
						if(botRole.canInteract(guild.getRoleById(birthdayRoleId)))
						{
							assignBirthdayRole = true;
							break;
						}
					}
					if(assignBirthdayRole)
					{
						for(String userId : users)
						{
							guild.addRoleToMember(userId, guild.getRoleById(birthdayRoleId)).queue();
						}
					}
				}

				//Announce birthday, when applicable
				//TODO: include option for posting a guild-defined birthday message with optional image or gif
				if(birthdayChannelId != null)
				{
					TextChannel bdayChannel = guild.getTextChannelById(birthdayChannelId);
					if(bdayChannel != null && bdayChannel.canTalk())
					{
						for(String userId : users)
						{
							bdayChannel.sendMessage("Happy birthday " + guild.getMemberById(userId).getAsMention() + "!").queue();
						}
					}
				}

				for(String userId : users)
				{
					updateBirthday(userId, currentDb, jda);
				}
				checkToRemoveBirthdayRole(users,birthdayRoleId, guild);

			}
		} catch (NullPointerException |  SQLException e) {
			System.out.println("ERROR WITH BIRTHDAYS");
			jda.getTextChannelById(BeepBoop.TESTCHANNELID).sendMessage("SOMETHING WRONG WITH BIRTHDAYS!").queue();
			e.printStackTrace();
			System.exit(0);
		}

	}

	public void updateBirthday(String userId,String database, JDA jda)
	{
		String sql = "EXEC UpdateBirthdayToNextYear\n"
				+ "@UserId = " + userId;
		try 
		{
			UsefulMethods.executeSQLProc(sql, database);
		} catch (SQLException e) 
		{
			jda.getTextChannelById(BeepBoop.TESTCHANNELID).sendMessage("Could not update birthday for userId: " + userId).queue();
			e.printStackTrace();
		}
	}

	public void checkToRemoveBirthdayRole(List<String> userIds, String birthdayRoleId, Guild guild)
	{
		List<String> ids = new ArrayList<>(userIds);
		if(birthdayRoleId == null || guild.getRoleById(birthdayRoleId) == null || userIds == null)
		{
			return;
		}
		Role bdayRole = guild.getRoleById(birthdayRoleId);
		List<Member> members = guild.getMembersWithRoles(bdayRole);
		try
		{
			for(Member m : members)
			{
				ResultSet results = UsefulMethods.runSQLQuery("SELECT dbo.IsUserBirthdayAYearAway('"+ m.getUser().getId() +"')", guild.getId());
				if(results != null && results.next() && results.getBoolean(1))
				{
					ids.add(m.getUser().getId());
				}
				//If the user is NOT in the list of users who should be getting the role OR who already SHOULD have it
				if(!ids.contains(m.getUser().getId())) 
				{
					guild.removeRoleFromMember(m, bdayRole).queue(s -> {}, 
							f -> BeepBoop.logger.error("Could not remove birthday role from userId:" + m.getUser().getId()
									+ " in database: " + guild.getId()));
				}
			}
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
}