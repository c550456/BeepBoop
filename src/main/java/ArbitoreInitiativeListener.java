import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;

public class ArbitoreInitiativeListener extends ListenerAdapter
{
	private List<User> users = new ArrayList<>();
	
	public ArbitoreInitiativeListener()
	{
		this.users.clear();
	}
	public ArbitoreInitiativeListener(User user)
	{
		this.users.add(user);
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		if(users.contains(event.getAuthor()))
		{
			event.getMessage().delete().queue();
		}
	}
	
	public void addUser(User user)
	{
		this.users.add(user);
	}
	public void clearUsers()
	{
		this.users.clear();;
	}
}
