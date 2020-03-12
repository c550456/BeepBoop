import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.Paginator;

public class Reactstats extends Command 
{
	
	public Reactstats()
	{
		this.name = "reactstats";
		this.aliases = new String[] {"reactions", "reactionstats", "reacts"};
		this.category = BeepBoop.FUN;
		this.help = "Displays a list of the usage of all the server's emotes as reactions";
	}

	@Override
	protected void execute(CommandEvent event) 
	{
//		Guild guild = event.getJDA().getGuildById(BeepBoop.GUILDID);
		
		Paginator paginator;
		try
		{
			List<String> reactList = new ArrayList<>();
			reactList = Files.readAllLines(UsefulMethods.findPath("reacts.txt"));
			
			Collections.sort(reactList, (a, b) -> sort(a,b));
			
			String[] reactArr = new String[reactList.size()];
			reactArr = reactList.toArray(reactArr);		//Creates an array as a copy of the ArrayList
														//We do this because Paginators cannot add Lists
			for(int i = 0; i < reactArr.length; i++)
			{
				String[] parts = reactArr[i].split(": ");
//				if(guild.getEmotesByName(parts[0], false).isEmpty())
//				{
//					reactArr = ArrayUtils.remove(reactArr, i);
//					i--;	//Because removing shifts all elements to the left, we need to go back one in order not to skip any
//				}
//				else
				{
//					parts[0] = parts[0].replaceAll(parts[0], guild.getEmotesByName(parts[0], false).get(0).getAsMention());
					reactArr[i] = parts[0].concat(": ").concat(parts[1]).concat(" results");
				}
			}
			
			paginator = new Paginator.Builder()
					.waitOnSinglePage(true)
					.showPageNumbers(true)
					.setItemsPerPage(10)
					.addItems(reactArr)
					.setText("**Server Reaction Statistics**")
					.setUsers(event.getAuthor())
					.setColor(Color.GREEN)
					.setTimeout(2, TimeUnit.MINUTES)
					.setEventWaiter(BeepBoop.waiter)
					.build();
					
			paginator.display(event.getTextChannel());
		} catch(IOException e)
		{
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
