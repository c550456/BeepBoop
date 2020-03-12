import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;

//import com.jagrosh.jdautilities.menu.pagination.Paginator;
//import com.jagrosh.jdautilities.menu.pagination.PaginatorBuilder;
//import com.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
//TODO: make this work with everything!
public class Leaderboard extends Command 
{
	List<User> battlers = new ArrayList<>(); 
	static Map<User, Integer> userRanks = new HashMap<>();
	CommandEvent thisEvent = null;
	Guild guild = null;
	Message boardMessage = null;
	static List<String> info = new ArrayList<>();

	public Leaderboard()
	{
		this.name = "leaderboard";
		this.aliases = new String[] {"leader"};
		this.help = "Displays the %battle rankings of the server";
		this.category = BeepBoop.FUN;
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		
		
		thisEvent = event;
//		guild = thisEvent.getJDA().getGuildById(BeepBoop.GUILDID);

		try {
			info = Files.readAllLines(UsefulMethods.findPath("leaderboard.txt"));
			info
			.forEach(s -> 
			{
				String[] line = s.split("\\s:\\s");
				User user = event.getJDA().getUserById(line[0]);
				Integer rank = Integer.parseInt(line[1]);
				if(user != null)	
				{
					battlers.add(user);
					if(!userRanks.containsKey(user))
						userRanks.put(user, rank);
				}

			});
		} catch (IOException e) {
			System.out.println("ERROR GETTING LEADERBOARD");
			e.printStackTrace();
			System.exit(0);
		}
//		System.out.println(userRanks);
		//		Collections.sort(battlers, (a, b) -> sort(a, b));
		displayLeaderboard(event);

		battlers.clear();
		
	}

	public void displayLeaderboard(CommandEvent event)
	{

		EventWaiter waiter = new EventWaiter();
		event.getJDA().addEventListener(waiter);
		Paginator paginator;
		try {
			List<String> leaderList = new ArrayList<>();
			leaderList = Files.readAllLines(UsefulMethods.findPath("leaderboard.txt"));
			Collections.sort(leaderList, (a, b) -> sort(a, b));
			String[] leaderArr = new String[leaderList.size()];
			leaderArr = leaderList.toArray(leaderArr);

			for(int i = 0; i < leaderArr.length; i++)
			{
//				String[] parts = leaderArr[i].split(" : ");
//				if(event.getJDA().getGuildById(BeepBoop.GUILDID).getMemberById(parts[0]) != null)
//				{
//					leaderArr[i] = leaderArr[i].replaceAll(parts[0], event.getJDA().getGuildById(BeepBoop.GUILDID).getMemberById(parts[0]).getEffectiveName());
//				}
//				else
//				{
//					leaderArr = ArrayUtils.remove(leaderArr, i);
//					i--;
//				}
			}
			paginator = new Paginator.Builder()
					.waitOnSinglePage(true)
					.showPageNumbers(true)
					.addItems(leaderArr)
					.useNumberedItems(true)
					.setText("**Server Battle Rankings**")
					.setUsers(event.getAuthor())
					.setEventWaiter(waiter)
					.build();

			paginator.display(event.getTextChannel());
		} catch (IOException e) {
			System.out.println("CAUGHT EXCEPTION: " + e.getMessage());
			e.printStackTrace();
		}

	
	}

	public static void addPoints(User winner, User loser, int points)
	{
		int winnerRank = userRanks.get(winner);
		int loserRank = userRanks.get(loser);

		userRanks.put(winner,  winnerRank + points);
		if(loserRank - points < 0)
		{
			userRanks.put(loser, 0);
		}
		else
		{
			userRanks.put(loser, loserRank - points);
		}

		List<String> ranks = new ArrayList<>();
		userRanks.forEach((u,i) -> ranks.add(u.getId() + " : " + i.intValue())); //adds each user's ID and rank to ranks

		try {
			Files.write(UsefulMethods.findPath("leaderboard.txt"), ranks, StandardCharsets.UTF_8);
			
		} catch (IOException e) {
			System.out.println("UNABLE TO WRITE TO LEADERBOARD!");
		}

	}

	public static void addUser(User user)
	{
		List<String> rankings = new ArrayList<>();
		try {
			rankings = Files.readAllLines(UsefulMethods.findPath("leaderboard.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		rankings.add(user.getId() + " : " + 1000);
		userRanks.put(user, 1000);

		try {
			Files.write(UsefulMethods.findPath("leaderboard.txt"), rankings, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int sort(String a, String b)
	{
		int aRank = Integer.parseInt(a.split(" : ")[1]);
		int bRank = Integer.parseInt(b.split(" : ")[1]);

		if(aRank == bRank)
			return 0;
		else if (aRank > bRank)
			return -1;
		else
			return 1;
	}



}
