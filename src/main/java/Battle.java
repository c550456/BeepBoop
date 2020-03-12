import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;

import org.joda.time.DateTime;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Battle extends Command 
{
	static Map<User, DateTime> cooldown = new HashMap<>();
	public static Map<User, User> battles = new HashMap<>();

	public Battle()
	{
		this.name = "battle";
		this.aliases = new String[] {"fite", "fight", "challenge"};
		this.help = "Challenges a user to a battle";
		this.arguments = "@user";
		this.category = BeepBoop.FUN;
	}

	@Override
	protected void execute(CommandEvent event) 
	{

		if(Leaderboard.userRanks.isEmpty())
			try {
				Leaderboard.info = Files.readAllLines(UsefulMethods.findPath("leaderboard.txt"));
				Leaderboard.info
				.forEach(s -> 
				{
					String[] line = s.split("\\s:\\s");
					User user = event.getJDA().getUserById(line[0]);
					Integer rank = Integer.parseInt(line[1]);
					if(user != null)
					{
						Leaderboard.userRanks.put(user, rank);
					}
				});
			} catch (IOException e) {
				System.out.println("ERROR GETTING LEADERBOARD");
				e.printStackTrace();
				System.exit(0);
			}

		String[] args = event.getArgs().split("\\s+");
		List<User> mentions = event.getMessage().getMentionedUsers();
		boolean hasMentions = (!mentions.isEmpty());


		if(hasMentions)
		{
			String userMention = mentions.get(0).getAsMention();
			String memberMention = event.getGuild().getMember(mentions.get(0)).getAsMention();
			boolean suicide = mentions.get(0) == event.getAuthor();
			boolean isBeepBoop = mentions.get(0) == event.getSelfUser();
			boolean battleBot = mentions.get(0).isBot() && !isBeepBoop;
			
//			Guild guild = event.getJDA().getGuildById(BeepBoop.BRUMSERVERID);
//			Role mod = guild.getRoleById("302641262240989186");
//			boolean isMod = guild.getMember(event.getAuthor()) != null 
//					&& guild.getMember(event.getAuthor()).getRoles().contains(mod);
			
			boolean areMarried = (CouplesMethods.hasSpouse(event.getAuthor(), event.getJDA()) && CouplesMethods.getSpouse(event.getAuthor(), event.getJDA()) == mentions.get(0));

			if(args[0].equals(userMention) || args[0].equals(memberMention))
			{
				if(areMarried)
				{
					if(cooldown.containsKey(event.getAuthor()))
					{
						int seconds = (int) ((DateTime.now().getMillis() - cooldown.get(event.getAuthor()).getMillis()) / 1000);
						if(seconds < 60)
						{
							event.reply("That command is on cooldown for " + (60 - seconds) + " seconds!");
							return;
						}
						else
						{
							cooldown.remove(event.getAuthor());
						}
					}
					cooldown.put(event.getAuthor(), DateTime.now());
					event.reply("Trying to fight your spouse? Shame on you! Your marriage karma has just dropped!");
					CouplesMethods.adjustKarma(-50, event.getAuthor(), event.getJDA());
					CouplesMethods.addSpouseBattle(event.getAuthor(), event.getJDA());
					return;
				}

				if(suicide)
				{
					event.reply("Don't Levitato yourself! It isn't the answer!");
					return;
				}

				if(isBeepBoop)
				{
					event.reply("I run these battles. You think I can't win them all?");
					return;
				}

				if(battleBot)
				{
					event.reply("You're a monster! Trying to fight a defenseless bot...");
					return;
				}
				
				if(ResponseListener.isListening(event.getAuthor(), this))
				{
					event.reply("You are already battling somebody!");
				}
				else if(ResponseListener.isListening(mentions.get(0), this))
				{
					event.reply("That user is already in a battle!");
				}
				else
				{
					event.reply(event.getAuthor().getAsMention() + " has challenged you to a battle " + args[0] + "!\n%accept or %decline");
					event.getJDA().addEventListener(new ResponseListener(this, event.getAuthor(), mentions.get(0), "accept", "decline", event.getTextChannel(), event.getJDA()));
				}
			}
		}
	}
	
	public static void acceptBattle(User userA, User userB, MessageReceivedEvent event)
	{
		UsefulMethods.reply(event, getBattleOutcome(userA, userB, event.getGuild()));
	}
	
	public static void denyBattle(User userB, MessageReceivedEvent event)
	{
		UsefulMethods.reply(event, userB.getAsMention() + " used their dimensional scissors to wimp out and escape!");
	}
	
	public static String getBattleOutcome(User attacker, User defender, Guild guild)
	{
		User winner = null;
		User loser = null;
		Random rand = new Random();
		int choose = 0;
		choose = rand.nextInt(2);
		if(choose == 1)
		{
			winner = attacker;
			loser = defender;
		}
		else
		{
			winner = defender;
			loser = attacker;
		}
		
		adjustRank(winner, loser);
		List<String> scenarios = new ArrayList<>();
		try {
			scenarios = Files.readAllLines(UsefulMethods.findPath("battles.txt"));
		} catch (IOException e) {
			System.out.println("CANNOT FIND BATTLES");
			e.printStackTrace();
		}
		
		choose = rand.nextInt(scenarios.size());
		
/*
		ObjectMapper mapper = new ObjectMapper(); 
		JsonNode rootNode = null;
		try {
			rootNode = mapper.readTree(mapper.getFactory().createParser
					(UsefulMethods.findFile("profiles.json", null)));
		} catch (NullPointerException | IOException e) {
			e.printStackTrace();
		}
	*/	
//		MyUser win = MyUser.getProfile(winner.getId());
//		MyUser lose = MyUser.getProfile(loser.getId());
		String output = scenarios.get(choose).trim().replaceAll("\\[winner]", guild.getMember(winner).getEffectiveName())
				.replaceAll("\\[loser]", Matcher.quoteReplacement(guild.getMember(loser).getEffectiveName()));
		
		
		
		return output;
	}
	
	public static void adjustRank(User winner, User loser)
	{
		if(!Leaderboard.userRanks.containsKey(winner))
		{
			Leaderboard.addUser(winner);
		}

		if(!Leaderboard.userRanks.containsKey(loser))
		{
			Leaderboard.addUser(loser);
		}
		boolean highWins = false;
		int newPoints = 0;
		int winnerRank = Leaderboard.userRanks.get(winner);
		int loserRank = Leaderboard.userRanks.get(loser);
		int diff = Math.abs(winnerRank - loserRank);
		if(winnerRank > loserRank)
		{
			highWins = true;
		}
		
		if(highWins)
		{
			int compare = 300;
			newPoints = 4;
			while(true)
			{
				if(diff >= compare)
				{
					break;
				}
				else
				{
					compare -= 25;
					newPoints++;
				}
			}
		}
		else	//if !highWins
		{
			int compare = 300;
			newPoints = 28;
			while(true)
			{
				if(diff >= compare)
				{
					break;
				}
				else
				{
					compare -= 25;
					newPoints--;
				}
			}
		}

		Leaderboard.addPoints(winner, loser, newPoints);

	}

}
