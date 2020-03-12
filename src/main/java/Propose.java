import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Propose extends Command 
{
	JDA jda = null;
	
	public Propose()
	{
		this.name = "propose";
		this.aliases = new String[] {"marry"};
		this.help = "Sends a marriage proposal to a user. If they accept, you will be married forever.";
		this.arguments = "@user";
		this.category = BeepBoop.FUN;
		this.cooldownScope = Command.CooldownScope.USER;
		this.cooldown = 60;
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		jda = event.getJDA();
		if(!event.getMessage().getMentionedUsers().isEmpty())
		{
			String[] parts = event.getArgs().split("\\s+");
			User bride = UsefulMethods.hasMention(parts[0], jda);
			User groom = event.getAuthor();
			if(bride != null)
			{
				if(bride == groom)
				{
					event.reply("You cannot marry yourself! Who are you, Narcissus?");
				}
				else if(bride.isBot())
				{
					event.reply("Bots can't marry, they have no feelings.");
				}
				else if(CouplesMethods.hasSpouse(bride, jda))
				{
					if(CouplesMethods.getSpouse(bride, jda) == groom)
					{
						event.reply("You two are already married! What are you thinking?");
					}
					else
						event.reply(bride.getAsMention() + " is already married! No polygamy in this Christian server!");
				}
				else if (CouplesMethods.hasSpouse(groom, jda))
				{
					event.reply(groom.getAsMention() + ", you are already married! No cheating!");
				}
				else if(ResponseListener.isListening(groom, this))
				{
					event.reply("You already have a pending marriage proposal!");
				}
				else if(ResponseListener.isListening(bride, this))
				{
					event.reply("That user already has a pending marriage proposal!");
				}
				else
				{
					event.reply(groom.getAsMention() + " has proposed to " + bride.getAsMention() + "!\n**%I do** or **%I don't**");
					jda.addEventListener(new ResponseListener(this, groom, bride, "I do", "I don't", event.getTextChannel(), jda));
				}
			}
			else
			{
				event.reply("Sorry, no user with the name " + event.getArgs() + " was found.");
			}
		}
	}

	
	public static void acceptProposal(User userA, User userB, MessageReceivedEvent event)
	{
		event.getTextChannel().sendMessage("Congratulations " + userA.getAsMention() + " and " + userB.getAsMention() + "! Your souls have been bound for eternity in the "
				+ "hypnotic ruby light of the Blood Moon!").queue();
		addCouple(userB, userA, event.getJDA());
	}
	
	public static void refuseProposal(User userA, User userB, MessageReceivedEvent event)
	{
		event.getTextChannel().sendMessage(userB.getAsMention() + " has rejected " + userA.getAsMention() +"! How unfortunate.").queue();
	}
	
	public static void addCouple(User wife, User husband, JDA jda)
	{
		List<String> couples = new ArrayList<>();
		try {
			couples = Files.readAllLines(UsefulMethods.findPath("couples.txt"));
			couples.add(wife.getId() + ", " + husband.getId() + ", 1000");
			Files.write(UsefulMethods.findPath("couples.txt"), couples, StandardCharsets.UTF_8);
		} catch (IOException e) {
			jda.getTextChannelById(BeepBoop.TESTCHANNELID).sendMessage("UNABLE TO FIND couples.txt!").queue();
			return;
		}


	}
	
	
	
}
