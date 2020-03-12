import java.util.List;
import java.util.Random;

import com.google.common.primitives.Ints;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter
{
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		Message msg = event.getMessage();
		String lowerContent = msg.getContentDisplay().toLowerCase();
		if(lowerContent.startsWith("i'm ") || lowerContent.startsWith("i’m ")) //different types of apostrophes on certain phones
		{
			Random rand = new Random();
			int chance = rand.nextInt(10);
			List<Integer> lunaChances = Ints.asList(new int[] {1,2,3,4,5});
			List<Integer> otherChances = Ints.asList(new int[] {1,2});
			boolean work = 
					(event.getAuthor().getId().equals("253259131157348353") && (lunaChances.contains(chance)))	//is Luna and 50% chance
					|| otherChances.contains(chance); 															//anyone else 20%
			
			if (!work)
				return;
			
			String newName = event.getMessage().getContentDisplay().substring(3).trim();
			if(newName.length() > 32)
			{
				newName = newName.substring(0, 31);
			}

			Member me = event.getGuild().getMember(event.getJDA().getSelfUser());
			TextChannel testing = event.getJDA().getTextChannelById(BeepBoop.TESTCHANNELID);
			try
			{
			if(me.canInteract(event.getMember()))
			{
				final String finishedName = newName;
				event.getGuild().modifyNickname(event.getMember(), finishedName).queue(s -> testing.sendMessage("Changed nickname to: " + finishedName) ,
						f -> testing.sendMessage(f.getMessage()));
			}
			}catch(Exception e)
			{
				event.getJDA().getTextChannelById(BeepBoop.TESTCHANNELID).sendMessage(e.getMessage()).queue();
			}

		}

	}
}
