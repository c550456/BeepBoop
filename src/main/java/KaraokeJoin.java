import java.util.ArrayDeque;
import java.util.Deque;
import java.util.*;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Member;

public class KaraokeJoin extends Command
{
	private Deque<Member> q = new ArrayDeque<Member>();
	public KaraokeJoin()
	{
		this.name = "join";
//		this.category = BeepBoop.FUN;
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		Member member = event.getMember();
		if(!q.contains(member))
		{
			q.add(member);
			event.reply("Okay " + event.getMember().getAsMention() + ", I've added you to the queue.");
		}
		else
		{
			event.reply("You are already in the queue! Wait your turn.");
		}
	}
	
	public boolean isEmpty()
	{
		return q.isEmpty();
	}

	public Member getNext()
	{
		return q.pop();
		
	}
	
	public String[] getQueue()
	{
		List<String> names = new ArrayList<>();
		for(Member m : q)
		{
			names.add(m.getEffectiveName());
		}
		return names.toArray(new String[names.size()]);
	}
}
