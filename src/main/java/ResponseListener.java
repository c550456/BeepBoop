import com.jagrosh.jdautilities.command.Command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
 * Used as an easy way to respond with yes or no to a number of different commands. To use:
 * 1. Check to see if there's already a response listening with the ResponseListener.isListening() method
 * 
 * 2. use JDA.addEventListener(new ResponseListener(this, userThatCalledCommand, userToListenFor, String 
 * to look for to accept, String to look for to deny, TextChannel to listen in, JDA object to use)
 * 
 * 3. Add an else if() statement to the (rightUser && yes) and (rightUser && no) blocks to use the cmd 
 * object you passed and call that class's methods for accept and deny
 */

public class ResponseListener extends ListenerAdapter
{
	public static Map<User, List<Command>> pending = new HashMap<>(); 

	private Command cmd = null;
	private User userA = null;
	private User userB = null;
	private JDA jda = null;
	private ScheduledExecutorService scheduler = null;
	private String accept = "";
	private String deny = "";
	private TextChannel channel = null;

	public ResponseListener(Command command, User caller, User listenTo, String accept, String deny, TextChannel channel, JDA jda)
	{
		cmd = command;
		userA = caller;
		userB = listenTo;
		this.jda = jda;
		scheduler = Executors.newSingleThreadScheduledExecutor();
		this.accept = accept;
		this.deny = deny;
		this.channel = channel;

		List<Command> commands = new ArrayList<>();

		if(pending.containsKey(caller))		//Correctly inputs pending
		{
			commands = pending.get(caller);	//Gets any existing commands
			commands.add(command);			//Adds this command
			pending.put(caller, commands);	//Updates pending
		}
		else
		{
			commands.add(command);
			pending.put(caller, commands);
		}

		commands.clear();

		if(pending.containsKey(listenTo))
		{
			commands = pending.get(listenTo);
			commands.add(command);
			pending.put(listenTo, commands);
		}
		else
		{
			commands.add(command);
			pending.put(listenTo, commands);
		}

		unListen(this);
	}

	public void unListen(ResponseListener listener) //is automatically executed after 1 minute if nothing happens
	{
		final Runnable checker = new Runnable()
		{
			public void run() 
			{
				if(jda.getRegisteredListeners().contains(listener))
				{
					channel.sendMessage(userB.getAsMention() + " has run out of time to respond!").queue();
					pending.put(userA, removeCommand(userA, cmd));
					pending.put(userB, removeCommand(userB, cmd));
					jda.removeEventListener(listener);
				}
			}
		};

		scheduler.schedule(checker, 1, TimeUnit.MINUTES);
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		Message message = event.getMessage();
		boolean rightUser = event.getAuthor() == userB;
		boolean yes = message.getContentDisplay().equals(BeepBoop.PREFIX + accept);
		boolean no = message.getContentDisplay().equals(BeepBoop.PREFIX + deny);
		//TODO: look into %protect and %attack to help the battlers

		boolean isSpouse = event.getAuthor() == CouplesMethods.getSpouse(userA, jda) && event.getAuthor() != userB;	//if message author is spouse and not userB

		if(isSpouse && cmd instanceof Kiss && event.getTextChannel() == channel)
		{
			Kiss.caughtCheating(userA, event);
			
			pending.put(userA, removeCommand(userA, cmd));
			pending.put(userB, removeCommand(userB, cmd));
			jda.removeEventListener(this);
			return;
		}
		

		if(rightUser && no)
		{
			//TODO: Change these static methods to use the cmd object. Not sure why I did it that way
			//userA is caller, userB is responder
			if(cmd instanceof Kiss)
			{
				Kiss.refuseKiss(userA, userB, event);
			}
			else if(cmd instanceof Propose)
			{
				Propose.refuseProposal(userA, userB, event);
			}
			else if(cmd instanceof Battle)
			{
				Battle.denyBattle(userB, event);
			}
			else if(cmd instanceof BanEveryone)
			{
				BanEveryone.dontBanEveryone(channel);
			}
			else if(cmd instanceof HighFive)
			{
				((HighFive) cmd).denyHighFive(userA, userB, event);
			}
			pending.put(userA, removeCommand(userA, cmd));
			pending.put(userB, removeCommand(userB, cmd));
			jda.removeEventListener(this);
			return;
		}
		else if(rightUser && yes)
		{
			//TODO: Change these static methods to use the cmd object. Not sure why I did it that way
			//userA is caller, userB is responder
			if(cmd instanceof Kiss)
			{
				Kiss.acceptKiss(userA, userB, event);
			}
			else if(cmd instanceof Propose)
			{
				Propose.acceptProposal(userA, userB, event);
			}
			else if(cmd instanceof Battle)
			{
				Battle.acceptBattle(userA, userB, event);
			}
			else if(cmd instanceof BanEveryone)
			{
				BanEveryone.banEveryone(channel);
			}
			else if(cmd instanceof HighFive)
			{
				((HighFive) cmd).acceptHighFive(userA, userB, event);
			}
			
			pending.put(userA, removeCommand(userA, cmd));
			pending.put(userB, removeCommand(userB, cmd));
			jda.removeEventListener(this);
			return;
		}
	}

	public static boolean isListening(User user, Command command)	//Used for checking whether or not to output the "already waiting on that" message
	{
		if(ResponseListener.pending.containsKey(user))
		{
			List<Command> commands = new ArrayList<>();
			commands = pending.get(user);

			for(Command c : commands)
			{
				if(c.getClass() == (command.getClass()))
				{
					return true;
				}
			}
		}
		return false;
	}

	public List<Command> removeCommand(User user, Command command)
	{
		List<Command> commands = new ArrayList<>();
		commands = pending.get(user);
		commands.remove(command);
		return commands;
	}
	
	public static Set<User> getUsers(Command command)
	{
		Set<User> userSet = new HashSet<>();
			for(User user : pending.keySet())
			{
				for(Command cmd : pending.get(user))
				{
					if(cmd.equals(command))
					{
						userSet.add(user);
					}
				}
			}
		
		return userSet;
	}
	
	public static List<Command> getCommands(User user)
	{
		return pending.get(user);
	}
	
	public Command getCmd()
	{
		return this.cmd;
	}
}
