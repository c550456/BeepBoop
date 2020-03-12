import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Kiss extends Command
{

	public Kiss()
	{
		this.name = "kiss";
		this.category = BeepBoop.COUPLES;
		this.help = "Offers a kiss to your spouse";
		this.cooldownScope = Command.CooldownScope.USER;
		this.cooldown = 60;
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		/*
		String anjukkID = "308389039956819969";
		if(event.getAuthor().getId().equals(anjukkID))
		{
			event.reply("lol no");
			return;
		}
		*/
		User spouse = CouplesMethods.getSpouse(event.getAuthor(), event.getJDA());
		User kissed = null;

		if(ResponseListener.isListening(event.getAuthor(), this))
		{
			event.reply("Your marriage already has a pending kiss!");
			return;
		}

		String[] args = event.getArgs().split("\\s");
		if(UsefulMethods.hasMention(args[0], event.getJDA()) != null)
		{
			kissed = UsefulMethods.hasMention(args[0], event.getJDA());
			if(kissed == event.getAuthor())
			{
				event.reply("You cannot kiss yourself, no matter how lonely you are.");
				return;
			}
		}

		if(kissed != null && kissed != spouse)
		{

			if(event.getGuild().getMember(spouse).getOnlineStatus() == OnlineStatus.ONLINE)
			{
				/*
				for(int i = 0; i < 4; i++)
				{
					CouplesMethods.adjustKarma(false, event.getAuthor(), event.getJDA());
				}
				event.reply("Uh oh! "  + spouse.getAsMention() + " caught " + event.getAuthor().getAsMention() + " cheating! Your marriage Karma has severely dropped!"
						+ "\n\n**New Marriage Karma:** " + CouplesMethods.getKarma(event.getAuthor(), event.getJDA()));
				 */
				caughtCheating(event.getAuthor(), event.getEvent());
				return;
			}
			else
			{
				offerKiss(event, kissed);
			}
		}
		else
		{
			offerKiss(event, spouse);
		}
	}

	public void offerKiss(CommandEvent event, User kissed)
	{
		try 
		{
			event.reply(kissed.getAsMention() + " you have been offered a kiss by " + event.getAuthor().getAsMention() + "!"
					+ "\n%agree or %refuse");
			event.getJDA().addEventListener(new ResponseListener(this, event.getAuthor(), kissed, "agree", "refuse", event.getTextChannel(), event.getJDA()));
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	public static void acceptKiss(User userA, User userB, MessageReceivedEvent event)
	{
		JDA jda = event.getJDA();

		if(CouplesMethods.getSpouse(userA, jda) == userB)
		{
			CouplesMethods.adjustKarma(50, userB, jda);
//			UsefulMethods.reply(event, getKiss(userA, userB) + "\n Marriage karma increased!"
//					+ "\n\n **New Marriage Karma:** " + CouplesMethods.getKarma(userB, jda));
//			UsefulMethods.reply(event, UsefulMethods.findFile("kissing starco.png", null));
		/*	File file = UsefulMethods.findFile("Kisses", null);
			Random rand = new Random();

			while(file.isDirectory())
			{
				int choose = rand.nextInt(file.listFiles().length);
//				System.out.println("Looking at: " + file.listFiles()[choose].getName());
				if(!file.listFiles()[choose].getName().equals("Thumbs.db"))
				{
					file = file.listFiles()[choose];
				}
//				System.out.println("Picking..." + file.getName());
			}
			event.getChannel().sendFile(file, new MessageBuilder().append(getKiss(userA, userB) + "\n Marriage karma increased!"
					+ "\n\n**New Marriage Karma:** " + CouplesMethods.getKarma(userB, jda)).build()).queue();*/
			event.getTextChannel().sendMessage(getKiss(userA, userB) + "\nMarriage Karma increased!"
					+ "\n\n**New Marriage Karma:** " + CouplesMethods.getKarma(userB, jda)).queue();
		}
		else if(CouplesMethods.hasSpouse(userB, jda) == false 	//If they're not married
				|| event.getGuild().getMember(CouplesMethods.getSpouse(userB, jda)).getOnlineStatus() != OnlineStatus.ONLINE)	//Or if their spouse is offline
		{
			UsefulMethods.reply(event, userA.getAsMention() + " and " + userB.getAsMention() + " share a forbidden kiss in secret.");
		}
		else	//if userA's spouse is not userB and userB is married and their spouse is online
		{
			caughtCheating(userB, event);
		}
	}

	public static void refuseKiss(User userA, User userB, MessageReceivedEvent event)
	{
		JDA jda = event.getJDA();
		if(CouplesMethods.getSpouse(userA, jda) == userB)
		{
			CouplesMethods.adjustKarma(-50, userB, jda);
			UsefulMethods.reply(event, "Kiss denied! Is there trouble in paradise? \nYour marriage karma just went down!"
					+ "\n\n **New Marriage Karma:** " + CouplesMethods.getKarma(userB, jda));
		}
		else
		{
			CouplesMethods.adjustKarma(50, userB, jda);
			if(CouplesMethods.hasSpouse(userB, jda))
			{
				UsefulMethods.reply(event, userB.getAsMention() + " stays faithful to their spouse and refuses the advances of " + userA.getAsMention()
				+". " + userB.getAsMention() + "'s Marriage Karma just went up!"
				+ "\n\n**New Marriage Karma:** " + CouplesMethods.getKarma(userB, jda));
			}
			else
			{
				UsefulMethods.reply(event, userB.getAsMention() + " shows no interest in " + userA.getAsMention() + " and refuses the kiss.");
			}
		}
	}
	
	public static String getKiss(User husband, User wife)
	{
		String kiss = "";
		
		try {
			List<String> kisses = Files.readAllLines(UsefulMethods.findPath("kisses.txt"));
			Random rand = new Random();
			int choose = rand.nextInt(kisses.size());
			kiss = kisses.get(choose).replaceAll("\\[husband]", husband.getAsMention())
					.replaceAll("\\[wife]", wife.getAsMention());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return kiss;
	}

	public static void caughtCheating(User userA, MessageReceivedEvent event)
	{
		JDA jda = event.getJDA();

		Member spouse = event.getGuild().getMember(CouplesMethods.getSpouse(userA, jda));
		
			CouplesMethods.adjustKarma(-200, userA, jda);
		
		UsefulMethods.reply(event, spouse.getAsMention() + " caught " + userA.getAsMention() + " cheating on them! Their "
				+ "Marriage Karma has severely dropped!"
				+ "\n\n**New Marriage Karma:** " + CouplesMethods.getKarma(userA, jda));
	}

}
