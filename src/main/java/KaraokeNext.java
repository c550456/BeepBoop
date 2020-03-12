import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class KaraokeNext extends Command 
{
	KaraokeJoin queue = null;
	Karaoke karaoke = null;
	public KaraokeNext(Karaoke karaoke, KaraokeJoin queue)
	{
		this.name = "next";
		this.category = BeepBoop.MOD;
		this.queue = queue;
		this.karaoke = karaoke;
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		if(!queue.isEmpty())
		{
			karaoke.setSinger(event, queue.getNext());
		}
		else
		{
			event.reply("There is nobody in the queue!");
			karaoke.setSinger(event, null);
		}

	}

}
