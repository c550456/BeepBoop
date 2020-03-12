import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;

public class DivorceListener extends ListenerAdapter 
{
	ScheduledExecutorService scheduler = null;
	JDA jda = null;
	Member b = null;
	Member a = null;
	public static List<Member> pending = new ArrayList<>();
	
	public DivorceListener(Member a, Member b, JDA jda)
	{
		scheduler = Executors.newSingleThreadScheduledExecutor();
		this.jda = jda;
		this.b = b;
		this.a = a;
		pending.add(a);
		pending.add(b);
		unListen(this);
	}

	public void unListen(DivorceListener divorce)
	{
		final Runnable checker = new Runnable()
		{
			public void run() {jda.removeEventListener(divorce); pending.remove(a); pending.remove(b);}
		};

		scheduler.schedule(checker, 1, TimeUnit.MINUTES);
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		Message message = event.getMessage();
		boolean rightUser = message.getAuthor() == b.getUser();
		boolean yes = message.getContentDisplay().equalsIgnoreCase("%yes");
		boolean no = message.getContentDisplay().equalsIgnoreCase("%no");
		
		
		if(rightUser && no)
		{
			UsefulMethods.reply(event, "It seems " + b.getAsMention() + " is happily married. "
					+ "I guess you two really are perfect for each other.");
			jda.removeEventListener(this);
			pending.remove(a);
			pending.remove(b);
			return;
		}
		
		if(rightUser && yes)
		{
			CouplesMethods.divorce(a.getUser());
			
			jda.removeEventListener(this);
			
				pending.remove(a);
				pending.remove(b);
				UsefulMethods.reply(event, "Alright! Your marriage has been cleaved! Feel free to find love elsewhere.");
			
		}
	}

}
