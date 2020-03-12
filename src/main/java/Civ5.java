import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;


import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.Webhook;
//import net.dv8tion.jda.webhook.WebhookClient;
//import net.dv8tion.jda.webhook.WebhookClientBuilder;
//import net.dv8tion.jda.webhook.WebhookMessage;
//import net.dv8tion.jda.webhook.WebhookMessageBuilder;
import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;

public class Civ5 extends Command 
{
	public Civ5()
	{
		this.name = "civ";
		this.aliases = new String[] {"CIV", "Civ"};
		this.category = BeepBoop.FUN;
	}
	@Override
	protected void execute(CommandEvent event) 
	{
		boolean civV = event.getArgs().toLowerCase().equals("v");
		boolean civ5 = event.getArgs().equals("5");

		if(event.getArgs().isEmpty() || civV || civ5)
		{
			Guild brum = event.getJDA().getGuildById(BeepBoop.BRUMSERVERID);
			Role civRole = brum.getRoleById("469581419623219201");
			List<Member> civMembers = new CopyOnWriteArrayList<>();
			civMembers.addAll(brum.getMembersWithRoles(civRole)); //allows for mutation of civMembers
			List<Member> offline = new ArrayList<>();
			MessageEmbed embed = null;
			String onMembers = "All up to ";

			if(civMembers.isEmpty())
			{
				event.reply("Nobody has the correct role!");
				return;
			}

			for(Member member : civMembers)
			{
				final String HAMILID = "253276885654568960";
				boolean isHamil = member.getUser().getId().equals(HAMILID);
				boolean hamilOff = isHamil && member.getOnlineStatus().equals(OnlineStatus.OFFLINE);
				if(isHamil)
				{
					if(hamilOff)
					{
						offline.add(member);
						civMembers.remove(member);
					}
					continue;
				}
				if(!member.getOnlineStatus().equals(OnlineStatus.ONLINE)) //If there's a member with the role who isn't online, or if member == hamil and he's offline
				{
					offline.add(member);
					civMembers.remove(member);
				}
			}

			if(!offline.isEmpty()) //if anyone is offline
			{
				String offMembers = "";
				for(int i = 0; i < offline.size(); i++)
				{
					offMembers += (i+1) + ". " + offline.get(i).getAsMention() + "\n";
				}
				
				if(civMembers.size() == 1)
				{
					onMembers += civMembers.get(0).getEffectiveName() + " as usual.";
				}
				else if(civMembers.size() == 2)
				{
					onMembers += civMembers.get(0).getEffectiveName() + " and " + civMembers.get(1).getEffectiveName() + " as usual.";
				}
				else
				{
					if(civMembers.contains(event.getGuild().getMember(event.getAuthor())))
					{
						onMembers += "me, ";
						civMembers.remove(event.getGuild().getMember(event.getAuthor()));
					}
					for(int i = 0; i < civMembers.size()-1; i++)
					{
						onMembers += civMembers.get(i).getEffectiveName() + ", ";
					}
					onMembers += "and " + civMembers.get(civMembers.size()-1).getEffectiveName() + " as usual.";
				}
				embed = new EmbedBuilder()
						.setTitle("Unavailable Players:")
						.appendDescription(offMembers)
						.setThumbnail("https://vignette.wikia.nocookie.net/civilization/images/e/e5/Civ5_logo.png/revision/latest?cb=20130801031221") //Civ V logo
						.build();
			}
			else		//if everyone is online
			{
				embed = new EmbedBuilder()
						.appendDescription("Everyone is on! Quick, ping " + civRole.getAsMention())
						.build();
			}

			event.reply(embed);
			if(!onMembers.equals("All up to "))
			{
				Webhook user = null;
				user = event.getTextChannel().createWebhook(event.getMember().getEffectiveName()).complete();
				WebhookClient client = new WebhookClientBuilder(user.getUrl()).build(); //new WebhookClientBuilder(user).build();
				WebhookMessage message = new WebhookMessageBuilder()
						.setContent(onMembers)
						.setAvatarUrl(event.getAuthor().getAvatarUrl())
						.setUsername(event.getMember().getEffectiveName())
						.build();
				try {
					client.send(message).get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
				
				client.close();
				user.delete().queue(null, f -> System.out.println(f.getMessage()));
			}
		}
	}

}
