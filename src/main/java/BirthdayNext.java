import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class BirthdayNext extends Command 
{
	private String database = null;
	public BirthdayNext()
	{
		this.name = "next";
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		database = event.getGuild().getId();
		nextBirthday(event);
	}
	
	public void nextBirthday(CommandEvent event)
	{
		List<String> soonestUserList = null;
		
		ResultSet results = null;	
		try {
			results = UsefulMethods.runSQLQuery("SELECT dbo.GetUsersWithNearestBirthdays()", database);
			if(results.next() && results.getString(1) != null)
			{
				soonestUserList = Arrays.asList(results.getString(1).split(","));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(soonestUserList == null || soonestUserList.isEmpty())
		{
			event.reply("There are not yet any birthdays that have been entered on this server");
			return;
		}
		for(String userId : soonestUserList)
		{
			Birthday.displayBirthday(event, userId, database);
		}
	}

}
