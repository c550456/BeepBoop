import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class Countdown extends Command 
{

	public Countdown()
	{
		this.name = "countdown";
		this.help = "displays a countdown to November 15";
		this.children = new Command[] {new LiveCountdown()};
		this.category = BeepBoop.FUN;
	}
	
	@Override
	protected void execute(CommandEvent event) 
	{
//		OffsetDateTime time = event.getAuthor().getCreationTime();
//		System.out.println(time.toString());
		DateTime now = DateTime.now(DateTimeZone.UTC);
		event.reply(getCountdown(now));
	}
	
	public static String getCountdown(DateTime now)
	{
		String print = "";
		
		DateTime bfm = new DateTime()
				.withMonthOfYear(12)
				.withDayOfMonth(16)
				.withYear(2017)
				.withHourOfDay(5)
				.withMinuteOfHour(0)
				.withSecondOfMinute(0)
				.withMillisOfSecond(0)
				.withZoneRetainFields(DateTimeZone.UTC);
		
		 PeriodFormatter dhms = new PeriodFormatterBuilder()
				    .appendDays()
				    .appendSuffix(" day", " days")
				    .appendSeparator(", ")
				    .appendHours()
				    .appendSuffix(" hour", " hours")
				    .appendSeparator(", ")
				    .appendMinutes()
				    .appendSuffix(" minute", " minutes")
				    .appendSeparator(", and ")
				    .appendSeconds()
				    .appendSuffix(" second", " seconds")
				    .toFormatter();

		
		if(now.isBefore(bfm))
		{
			Period p = new Period(now, bfm, PeriodType.dayTime());
			print = dhms.print(p);
		}
		return "**Time until " + bfm.monthOfYear().getAsText() + " " + bfm.getDayOfMonth() + ":** " + print;
	}

}
