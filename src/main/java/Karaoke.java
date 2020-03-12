import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
//import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class Karaoke extends Command
{
	JDA jda;
	final String guildID = "297550039125983233";	//Server ID
	final String memberID = "308992021664759809";	//Member role ID
	final String musicID = "307763283677544448";
	final String musicTextID = "307763370486923264";
	final String afkID = "308203575631151106";

	KaraokeLeaveListener leavers = new KaraokeLeaveListener();
	private ScheduledExecutorService scheduler = null;
	boolean karaokeOn = false;

	public Karaoke()
	{
		this.name = "karaoke"; //quote
		this.arguments = "<@user>";
		this.help = "unmutes a user in the Music VC while muting everyone else";
		this.category = BeepBoop.MOD;
		KaraokeJoin queue = new KaraokeJoin();
		this.children = new Command[] {queue, new KaraokeNext(this, queue), new KaraokeQueue(queue)};
		scheduler = Executors.newSingleThreadScheduledExecutor();
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		jda = event.getJDA();
		String[] args = event.getArgs().split("\\s+");
		Guild guild = event.getGuild();

		if(args.length == 1 || args.length == 0)
			args = new String[] {event.getArgs()};

		switch(args[0])
		{
		case "start":
			setKaraoke(true);
			return;
		case "end":
		case "off":
			setKaraoke(false);
			return;
		case "none":
			setSinger(event, event.getMember());
			return;
		}

		try //Need to allow for x number of users, unmute all of them
		{
			if (args[0].equals(guild.getMember(event.getMessage().getMentionedUsers().get(0)).getAsMention()))
			{
				Member m = guild.getMember(event.getMessage().getMentionedUsers().get(0));
				setSinger(event, m);
			}
		} catch (Exception e)
		{
			System.out.println("Incorrect karaoke command. " + e.toString() + e.getCause());
			event.reply("I'm sorry, the correct format is: `%karaoke @user`");
			return;
		}
	}

	public void setKaraoke(boolean on)
	{
		VoiceChannel musicVC = jda.getVoiceChannelById(musicID);
		VoiceChannel afkVC = jda.getVoiceChannelById(afkID);
		TextChannel musicText = jda.getTextChannelById(musicTextID);
		Guild guild = jda.getGuildById(guildID);
		Role members = guild.getRoleById(memberID);
		List<Member> currMembers = new ArrayList<>();
		currMembers = musicVC.getMembers();

		PermissionOverride perms = musicVC.getPermissionOverride(members);
		if(on)	//if we're turning on Karaoke
		{
			perms.getManager().grant(Permission.VOICE_SPEAK).queue();
		}
		else	//if we're turning off Karaoke
		{
			for(Member m : currMembers)
			{
				if(m.isOwner())
					continue;
				
				m.getGuild().mute(m, false).queue(null, f -> System.out.println(f.getMessage() + " ... Cause: " + f.getCause()));			//Unmuting everyone 
			}
			perms.getManager().deny(Permission.VOICE_SPEAK).queue();
			jda.removeEventListener(leavers);
		}

		for(Member m : currMembers)
		{
			if(m.isOwner())
				continue;
			
			m.getGuild().moveVoiceMember(m, afkVC).queue();		//Moving everyone, whether on or off, to update perms
			m.getGuild().moveVoiceMember(m, musicVC).queue();

			if(on && !m.getUser().isBot())
			{
				m.getGuild().mute(m, true).queue();			//Mutes everyone
			}


			scheduler.schedule(allowText(m, musicText), 10, TimeUnit.SECONDS); //Waits a few seconds to let Cobalt be slow, then fixes it
		}

		if(on)
			jda.addEventListener(leavers);

	}

	public static Runnable allowText(Member m, TextChannel musicText)
	{
		final Runnable allow = new Runnable()
		{
			public void run()
			{
				try {
					if(/*musicText.getPermissionOverride(m) == null && */!m.getUser().isBot())
					{
						musicText.createPermissionOverride(m).setAllow(Permission.MESSAGE_READ).queue();
						//				System.out.println("Allowed permissions for " + m.getEffectiveName() + ": " + musicText.getPermissionOverride(m).getAllowed());
					}
				} 
				catch (IllegalStateException ise)
				{
					musicText.getPermissionOverride(m).getManager().grant(Permission.MESSAGE_READ);
				}
				catch (Exception e)
				{
					System.out.println("Cobalt is slow and fucking up channel permissions again.");
					System.out.println(e.getMessage() + "... Cause: " + e.getCause());
				}
			}
		};
		return allow;
	}
	public void setSinger(CommandEvent event, Member member)
	{
		Member singer = null;
		Guild guild = event.getGuild();
		VoiceChannel musicVC = guild.getVoiceChannelById(musicID);
		singer = member;

		List<Member> singerList = musicVC.getMembers();

		for(Member m : singerList)
		{
			if(!m.getUser().isBot() && m != singer && !m.isOwner()) //if user member is not a bot
				guild.mute(m, true).queue();
		}

		event.getMessage().delete().queue();

		if(singer != null)
		{
			try
			{
				guild.mute(singer, false).queue();
			} catch (Exception e)
			{
				System.out.println("Error changing mute status");
			}
			event.reply("**" + singer.getAsMention() + ", it's your turn to sing!**");
		}
		else
			event.reply("**Dropped the mic**");
	}


}
