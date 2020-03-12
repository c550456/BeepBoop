import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.Paginator;
//import com.jagrosh.jdautilities.menu.pagination.Paginator;
//import com.jagrosh.jdautilities.menu.pagination.PaginatorBuilder;

public class KaraokeQueue extends Command 
{
	KaraokeJoin queue;
	public KaraokeQueue(KaraokeJoin queue)
	{
		this.name = "queue";
		this.aliases = new String[] {"line"};
		this.queue = queue;
	}
	
	@Override
	protected void execute(CommandEvent event) 
	{
		if(queue.getQueue().length == 0)
		{
			event.reply("There is nobody in the queue!");
			return;
		}
		
		Paginator paginator = new Paginator.Builder()
				.addItems(queue.getQueue())
				.setEventWaiter(BeepBoop.waiter)
				.setUsers(event.getAuthor())
				.waitOnSinglePage(true)
				.setItemsPerPage(10)
				.useNumberedItems(true)
				.setText("Karaoke Queue")
				.build();
		
		paginator.display(event.getChannel());
	}

}
