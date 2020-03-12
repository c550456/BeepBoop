import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Message;

public class LiveCountdown extends Command 
{
	ScheduledExecutorService scheduler = null;
	Message countdown = null;
	
	public LiveCountdown()
	{
		this.name = "live";
		this.help = "displays a live update for the Battle for Mewni countdown";
		this.category = BeepBoop.MOD;
		this.requiredRole = "Moderator";
		
	}
	
	@Override
	protected void execute(CommandEvent event) 
	{
		final Runnable checker = new Runnable() 
		{
			public void run() { counter(event); }
		};
		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(checker, 0, 2, TimeUnit.SECONDS); //Using 1 second was too fast (was not actually 1 second 
																				//for some reason). Got rate limited.
	}
	
	public void counter(CommandEvent event)
	{
		DateTime now = DateTime.now(DateTimeZone.UTC);
		if(countdown == null)
		{
			event.getTextChannel().sendMessage(Countdown.getCountdown(now)).queue(m -> countdown = m, f -> f.printStackTrace());
		}
		else
		{
			countdown.editMessage(Countdown.getCountdown(now)).queue(s -> {}, f -> 
			{
				scheduler.shutdownNow();
				countdown = null;
			});
		}
	}

}
