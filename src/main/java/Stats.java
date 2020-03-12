import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.Paginator;

public class Stats extends Command 
{
	/*//TODO: 
	 * rework this to:
	 *  be more efficient at getting emotes, 
	 *  store counts in database, 
	 *  and work for every guid
	 */
	public Stats()
	{
		this.name = "stats";
		this.aliases = new String[] {"statistics"};
		this.category = BeepBoop.FUN;
		this.help = "Displays a list of the usage of all the server's emotes";
	}

	@Override
	protected void execute(CommandEvent event)
	{
//		DateTimeFormatter mdy = DateTimeFormat.forPattern("MM/dd/yyyy").withLocale(Locale.US).withZoneUTC();
//		Guild guild = event.getJDA().getGuildById(BeepBoop.GUILDID);

		Paginator paginator;
		try {

			List<String> emotesList = new ArrayList<>();
			emotesList = Files.readAllLines(UsefulMethods.findPath("emotes.txt"));

			Collections.sort(emotesList, (a, b) -> sort(a,b));

			String[] emotesArr = new String[emotesList.size()];
			emotesArr = emotesList.toArray(emotesArr);		//Creates an array as a copy of the ArrayList
															//Do this because Paginators cannot add Lists
			for(int i = 0; i < emotesArr.length; i++)
			{
//				String[] parts = emotesArr[i].split(": ");
//				if(guild.getEmotesByName(parts[0], false).isEmpty())
//				{
//					emotesArr = ArrayUtils.remove(emotesArr, i);
//					i--;	//Because removing shifts all elements to the left, we need to go back one in order to not skip any
//				}
//				else
				{
//					StringBuilder name = new StringBuilder(parts[0]);
//					DateTime date = new DateTime(guild.getEmotesByName(name.toString(), false).get(0).getTimeCreated().toString());
//					String added = date.toString(mdy);
//					parts[0] = parts[0].replaceAll(parts[0], guild.getEmotesByName(parts[0], false).get(0).getAsMention());
//					emotesArr[i] = parts[0].concat(": ").concat(parts[1]).concat(" results (" + added + ")");
				}
			}
			
			paginator = new Paginator.Builder()
					.waitOnSinglePage(true)
					.showPageNumbers(true)
					.setItemsPerPage(10)
					.addItems(emotesArr)
					.setText("**Server Emote Statistics**")
					.setUsers(event.getAuthor())
					.setColor(Color.GREEN)
					.setTimeout(2, TimeUnit.MINUTES)
					.setEventWaiter(BeepBoop.waiter)
					.build();
			paginator.display(event.getTextChannel());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public int sort(String a, String b)
	{
		int aUse = Integer.parseInt(a.split(": ")[1]); 
		int bUse = Integer.parseInt(b.split(": ")[1]);

		if(aUse == bUse)
			return 0;
		else if(aUse > bUse)
			return -1;
		else 
			return 1;
	}

}
