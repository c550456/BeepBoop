import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class Ping extends Command 
{
	public Ping()
	{
		this.name = "ping";
		this.category = BeepBoop.MOD;
		this.help = "Displays the bot's ping.";
	}
	
	@Override
	protected void execute(CommandEvent event) 
	{
		DateTime ping = new DateTime(event.getMessage().getTimeCreated().toString()).withZone(DateTimeZone.UTC);
		DateTime pong = DateTime.now().withZone(DateTimeZone.UTC);
		
		/*
		PeriodFormatter pingFormat = new PeriodFormatterBuilder()
				.appendMillis()
				.appendSuffix("` ms")
				.toFormatter();
		
		Period p = new Period(ping, pong, PeriodType.millis());
		
		event.reply("**Beep Boop! Normalized Ping is: `" + pingFormat.print(p) + "**");
		*/
		
		long ms = Math.abs(ping.getMillis() - pong.getMillis());
		
		event.reply("**Beep Boop! Ping is: `" + ms + "` ms**");
	}

}
