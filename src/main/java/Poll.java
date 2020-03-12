import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.vdurmont.emoji.EmojiManager;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
//import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import java.util.*;

public class Poll extends Command implements EventListener
{
	private Message listener = null;
	private boolean everyone = false;
	private boolean here = false;
//	private boolean mChoice = false;
	private boolean eListener = false;
	private boolean mcListener = false;
//	private boolean ready = false;
	private Member member = null;
	private String question = "";
	private List<IMentionable> emoteList = new ArrayList<>();
	List<User> users = new ArrayList<>();

	public Poll()
	{
		this.name = "poll";
		this.aliases = new String[] {"createpoll", "polls"};
		this.category = BeepBoop.MOD;
		this.help = "Creates a poll for easy tracking of votes";
		this.arguments = "<poll question (with options described)> BeepBoop will guide you through the rest.";
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		users.clear();
		emoteList.clear();
//		String emojiString = "<:(\\S+?):(.+?)>";	//ID is group(2)
//		Pattern emojiPattern = Pattern.compile(emojiString);

		question = event.getArgs();

//		Matcher match = emojiPattern.matcher(event.getArgs());
/*
		while(match.find())
		{
			emoteList.add(event.getGuild().getEmoteById(match.group(2)));
		}
	*/	
		emoteList = event.getMessage().getMentions(MentionType.EMOTE);

		member = event.getMember();
		event.getMessage().delete().queue();

		eListener = false;
		mcListener = false;
		everyone = false;
		here = false;
//		ready = false;
//		mChoice = false;

		String here = "(" + EmojiManager.getForAlias(":arrow_down:").getUnicode() + ")";
		String all = "(" + EmojiManager.getForAlias(":white_check_mark:").getUnicode() + ")";
		event.getChannel().sendMessage("Would you like to `@everyone?` " + all + " or `@here`? " + here).queue(s -> 
		{
			eListener = true;
			listener = s;
			s.addReaction(EmojiManager.getForAlias(":white_check_mark:").getUnicode()).queue();
			s.addReaction(EmojiManager.getForAlias(":arrow_down:").getUnicode()).queue();
			s.addReaction(EmojiManager.getForAlias(":x:").getUnicode()).queue();
		});
	}

	@Override 
	public void onEvent(GenericEvent event) 
	{
		if(event instanceof MessageReactionRemoveEvent)
		{
			MessageReactionRemoveEvent e = ((MessageReactionRemoveEvent) event);
			if(e.getUser() != null && users.contains(e.getUser()) && e.getMessageId().equals(listener.getId()))
				users.remove(e.getUser());
		}
		if(event instanceof MessageReactionAddEvent 
				//				&& ((MessageReactionAddEvent) event).getMember() == member
				&& listener != null
				&& ((MessageReactionAddEvent) event).getMessageId().equals(listener.getId()))
		{
			MessageReactionAddEvent evt = ((MessageReactionAddEvent) event);
			if(((MessageReactionAddEvent) event).getMember() == member)
			{
				if(evt.getReactionEmote().getName().equals(EmojiManager.getForAlias(":white_check_mark:").getUnicode()))
				{
					if(eListener)
					{
						everyone = true;
						here = false;
						eListener = false;
//						checkMultipleChoice(evt.getChannel());
						createPoll(evt.getChannel());
					}
					else if(mcListener)
					{
//						mChoice = true;
						mcListener = false;
//						createPoll(evt.getChannel());
					}
					listener.delete().queue();
				}
				else if(evt.getReactionEmote().getName().equals(EmojiManager.getForAlias(":arrow_down:").getUnicode()))
				{
					everyone = false;
					here = true;
					eListener = false;
					listener.delete().queue();
					createPoll(evt.getChannel());
//					checkMultipleChoice(evt.getChannel());
				}
				else if(evt.getReactionEmote().getName().equals(EmojiManager.getForAlias(":x:").getUnicode()))
				{
					if(eListener)
					{
						everyone = false;
						here = false;
						eListener = false;
//						checkMultipleChoice(evt.getChannel());
						createPoll(evt.getChannel());
					}
					else if(mcListener)
					{
//						mChoice = false;
						mcListener = false;
//						createPoll(evt.getChannel());
					}
					listener.delete().queue();
				}
			}
			/*
			if(!mChoice && ready)
			{
				listener = evt.getChannel().getMessageById(evt.getMessageId()).complete();
				List<MessageReaction> reacts = listener.getReactions();
				User check = null;
				
				if(!evt.getUser().isBot())
					check = evt.getUser();

				for(MessageReaction mr : reacts)
				{
					for(User u : mr.getUsers().complete())
					{
						if(!users.contains(u) && !u.isBot() && u != check)
						{
							users.add(u);
						}
					}
				}

				if(check != null && users.contains(check))
				{
					evt.getReaction().removeReaction(check).queue();
				}
				users.add(check);
			}
			*/
		}
	}
/*
	public void checkMultipleChoice(MessageChannel channel)
	{
		channel.sendMessage("Would you like to allow more than one option to be selected?").queue(s -> 
		{
			mcListener = true;
			listener = s;
			s.addReaction(EmojiManager.getForAlias(":white_check_mark:").getUnicode()).queue();
			s.addReaction(EmojiManager.getForAlias(":x:").getUnicode()).queue();
		});
	}
*/
	public void createPoll(MessageChannel channel)
	{
		if(everyone)
		{
			question = question + "\n@everyone";
		}
		else if(here)
		{
			question = question + "\n@here";
		}

		channel.sendMessage(question).queue(s -> 
		{
			listener = s;
			for(IMentionable emote : emoteList)
			{
				if(emote != null)
				s.addReaction((Emote) emote).queue();
			}
//			ready = true;
			users.clear();
			emoteList.clear();
		});
	}


}
