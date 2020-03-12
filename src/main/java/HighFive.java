import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HighFive extends Command 
{
	public HighFive()
	{
		this.name = "highfive";
		this.aliases = new String[] {"high5"};
		this.category = BeepBoop.FUN;
		this.help = "Sends a high five request to any mentioned user";
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		if(ResponseListener.isListening(event.getAuthor(), this))
		{
			event.reply("You are already waiting for a return high five!");
			return;
		}

		JDA jda = event.getJDA();

		String[] args = event.getArgs().split("\\s+");
		User fivee = UsefulMethods.hasMention(args[0], jda);

		if(fivee == null)
		{
			event.reply("I'm sorry, I was not able to find a user with the name: " + args[0]);
			return;
		}
		else if(ResponseListener.isListening(fivee, this) && !ResponseListener.isListening(event.getAuthor(), this)) //third party high fives one already waiting
		{
			Set<String> cmdNames = new HashSet<>();
			for(Command cmd : ResponseListener.getCommands(fivee))
			{
				cmdNames.add(cmd.getName());
			}
			if(cmdNames.contains(this.name))
			{
				Set<User> users = ResponseListener.getUsers(this);
				List<Command> cmds = new ArrayList<>();
				for(User u : users)
				{
					cmds = ResponseListener.getCommands(u);
					Command toRemove = null;
					for(Command c : cmds)
					{
						if(c.getName().equals(this.name))
							toRemove = c;
					}
					cmds.remove(toRemove);
					ResponseListener.pending.put(u, cmds);
				}
				for(Object o : jda.getRegisteredListeners())
				{
					if(o instanceof ResponseListener)
					{
						if(((ResponseListener) o).getCmd().getName().equals(this.getName()))
						{
							jda.removeEventListener(o);
						}
					}
				}
				Set<User> origUsers = ResponseListener.getUsers(this);
				User tooSlow = null;
				for(User u : origUsers)
				{
					if(!u.getId().equals(fivee.getId()))
					{
						tooSlow = u;
					}
				}
				event.reply(event.getAuthor().getAsMention() + " intercepts and high fives " + fivee.getAsMention() + "! " 
				+ tooSlow.getAsMention() + " was too slow!");
				return;
			}
		}

		event.reply(fivee.getAsMention() + ", you have been offered a high five by " + event.getAuthor().getAsMention() + "! How do you respond?"
				+ "\n%return or %leave");
		event.getJDA().addEventListener(new ResponseListener(this, event.getAuthor(), fivee, "return", "leave", event.getTextChannel(), jda));
	}

	public void acceptHighFive(User userA, User userB, MessageReceivedEvent event)
	{
		UsefulMethods.reply(event, userB.getAsMention() + " gives " + userA.getAsMention() + " a crisp high five.");
	}

	public void denyHighFive(User userA, User userB, MessageReceivedEvent event)
	{
		UsefulMethods.reply(event, userB.getAsMention() + " leaves " + userA.getAsMention() + " hanging! Not cool...");
	}

	public void interceptHighFive(User userA, User userB, User interceptor)
	{

	}


}
