

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class KaraokeLeaveListener extends ListenerAdapter 
{
	final String musicTextID = "307763370486923264";
	final String musicID = "307763283677544448";
	
	private ScheduledExecutorService scheduler = null;
	
	public KaraokeLeaveListener()
	{
		scheduler = Executors.newSingleThreadScheduledExecutor();
	}
	
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event)	//Triggers when someone disconnects from a VC
	{															//WILL NOT LISTEN UNLESS KARAOKE IS ON
		VoiceChannel musicVC = event.getJDA().getVoiceChannelById(musicID);
		if(event.getChannelLeft() == musicVC && !event.getMember().isOwner())
		{
			event.getGuild().mute(event.getMember(), false).queue();	//Unmutes people that leave
//			System.out.println(event.getChannelLeft());
		}
	}
	
	@Override
	public void onGuildVoiceMove(GuildVoiceMoveEvent event)
	{
		TextChannel musicText = event.getJDA().getTextChannelById(musicTextID);
		VoiceChannel musicVC = event.getJDA().getVoiceChannelById(musicID);
		
		if(event.getChannelLeft() == musicVC && event.getMember().getUser().isBot() == false && !event.getMember().isOwner())
		{
			event.getGuild().mute(event.getMember(), false).queue();	//Unmutes people that leave
		}
		else if(event.getChannelJoined() == musicVC && event.getMember().getUser().isBot() == false && !event.getMember().isOwner())
		{
			event.getGuild().mute(event.getMember(), true).queue();	//Mutes people that join during Karaoke
			scheduler.schedule(Karaoke.allowText(event.getMember(), musicText), 10, TimeUnit.SECONDS);
		}
		
	}
	
	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event)
	{
		TextChannel musicText = event.getJDA().getTextChannelById(musicTextID);
		VoiceChannel musicVC = event.getJDA().getVoiceChannelById(musicID);
		
		if(event.getChannelJoined() == musicVC && !event.getMember().getUser().isBot() && !event.getMember().isOwner())		//If a non-bot joins the VC
		{
			event.getGuild().mute(event.getMember(), true).queue();	//Mutes people that join during Karaoke
			scheduler.schedule(Karaoke.allowText(event.getMember(), musicText), 10, TimeUnit.SECONDS);
		}
		
		
	}
}
