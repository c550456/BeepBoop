import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.Paginator;
//import com.jagrosh.jdautilities.menu.pagination.Paginator;
//import com.jagrosh.jdautilities.menu.pagination.PaginatorBuilder;
import com.vdurmont.emoji.EmojiManager;

import net.dv8tion.jda.api.entities.Member;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class Couples extends Command 
{
	public Couples()
	{
		this.name = "couples";
		this.help = "Displays a list of the current couples on the server";
		this.category = BeepBoop.FUN;
		this.aliases = new String[] {"couple", "spouses", "marriages"};
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		List<String> couples = new ArrayList<>();
		try {
			couples = Files.readAllLines(UsefulMethods.findPath("couples.txt"));
		} catch (IOException e) 
		{
			e.printStackTrace();
		}

		Collections.sort(couples, (a,b) -> sort(a, b));

		for(int i = 0; i < couples.size(); i++)
		{
			String line = couples.get(i);
			String[] parts = line.split(", ");

			try
			{
				Member a = event.getGuild().getMemberById(parts[0]);
				Member b = event.getGuild().getMemberById(parts[1]);
				couples.set(i, a.getEffectiveName() + " " + EmojiManager.getForAlias(":heart:").getUnicode() + " " + b.getEffectiveName()
				+ " *(" + parts[2] + " karma)*");
			} catch (NullPointerException e)
			{
				couples.remove(i);
				i--;
			}

		}

		if(couples.isEmpty())	//Allows me to use %couples on my server without throwing an exception
		{
			return;
		}

		String[] arr = new String[couples.size()];

	
		Paginator list = new Paginator.Builder()
				.addItems(couples.toArray(arr))
				.setEventWaiter(BeepBoop.waiter)
				.waitOnSinglePage(true)
				.setItemsPerPage(10)
				.setText("**List of Married Users**")
				.showPageNumbers(true)
				.addUsers(event.getAuthor())
				.build();

		list.display(event.getChannel());
	}

	public int sort(String a, String b)
	{
		int aKarma = Integer.parseInt(a.split(", ")[2]);
		int bKarma = Integer.parseInt(b.split(", ")[2]);

		if(aKarma == bKarma)
			return 0;
		else if(aKarma > bKarma)
			return -1;
		else 
			return 1;
	}

}
