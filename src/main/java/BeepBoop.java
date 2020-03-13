import java.util.List;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.Command.Category;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
//import com.jagrosh.jdautilities.waiter.EventWaiter;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;


/*TODO:
 * MAKE EVERYTHING WORK WITH SQL
 * make new battle points and ranks show up after a battle
 */
public class BeepBoop
{
	
	static private String MYTOKEN = "";
	final static String PREFIX = "%";

	final static String MYSERVERID = "292494629872467969";
	final static String BRUMSERVERID = "339219357878190093";

	final static String TESTCHANNELID = "307755754964647947";
	final static String SUGGESTIONSCHANNELID = "307624726879272963";
	final static String GENERALCHANNELID = "297550039125983233";
	final static String BOTCOMMANDSCHANNELID = "423661429703573504";
	final static String MODROLEID = "302641262240989186";
	final static String HUGQUEENROLEID = "358852293925404672";
	final static String DJROLEID = "368480471375544331";
	
	final static String STORMFATHERID = "165534549135196160";


	public final static Category FUN = new Category("For Fun", "It seems you've mistaken this for the <#307726225458331649> channel. Please read before you type, you spong.",
			e ->
	{ 
//		Guild guild = e.getJDA().getGuildById(GUILDID);
//		if(!e.getGuild().equals(guild))
		{
			return true;
		}
//		boolean mod = guild.getMember(e.getAuthor()) != null && guild.getMember(e.getAuthor()).getRoles().contains(guild.getRoleById(MODROLEID));
//		boolean botCommands = e.getTextChannel().getId().equals(BOTCOMMANDSCHANNELID);
//		boolean testing = e.getTextChannel().getId().equals(TESTCHANNELID);
		//		boolean isHug = e.getEvent().getMessage().getContent().startsWith("%hug");
		//		boolean hugQueen = guild.getMember(e.getAuthor()) != null && e.getMember().getRoles().contains(guild.getRoleById(HUGQUEENROLEID));
		//		boolean kittyHug = isHug && hugQueen;
//		return mod || botCommands || testing;
	});

	public final static Category COUPLES = new Category("For Couples", "You are not married!", 
			e -> 
	{
//		Guild guild = e.getJDA().getGuildById(GUILDID);
		boolean married = CouplesMethods.hasSpouse(e.getAuthor(), e.getJDA());
//		boolean mod = guild.getMember(e.getAuthor()) != null && guild.getMember(e.getAuthor()).getRoles().contains(guild.getRoleById(MODROLEID));
//		boolean botCommands = e.getTextChannel().getId().equals(BOTCOMMANDSCHANNELID);
		boolean testing = e.getTextChannel().getId().equals(TESTCHANNELID);

		return married || testing;
	});

	public final static Category EMOTES = new Category("Free Nitro!", "Sorry, that is not a supported emote", 	//TODO: add these in
			e ->
	{
		//		List<String> emotes = new ArrayList<>(Arrays.asList("launch"));
		//		return emotes.contains(e.getArgs());
		return true;
	});

	public final static Category MOD = new Category("For Staff", "Sorry, that is a staff-only command!",
			e ->
	{
//		Guild guild = e.getJDA().getGuildById(GUILDID);
//		if(!e.getGuild().equals(guild))
		{
			return true;
		}
//		boolean mod = guild.getMember(e.getAuthor()) != null && guild.getMember(e.getAuthor()).getRoles().contains(guild.getRoleById(MODROLEID));
//		boolean dj = e.getMember().getRoles().contains(guild.getRoleById(DJROLEID));
//		boolean djKaraoke = dj && e.getMessage().getContentDisplay().startsWith("%karaoke");
//		return mod || djKaraoke;
	});


	public static EventWaiter waiter = null;
	
	public static Logger logger = LoggerFactory.getLogger(BeepBoop.class);

	public static void main(String[] args) throws InterruptedException 
	{
		logger.info("STARTING UP");
		List<String> lines = UsefulMethods.getFileLines("BotSettings.txt");
		for(String line : lines)
		{
			if(line.contains("BotToken:"))
			{
				MYTOKEN = line.split("BotToken:\\s*")[1];
				break;
			}
		}
		CommandClientBuilder client = new CommandClientBuilder();
		waiter = new EventWaiter();
		client.setOwnerId("165534549135196160");
		client.setPrefix(PREFIX);
		client.setLinkedCacheSize(0);

		Poll poll = new Poll();
		Command[] commands = 
			{
					//FUN
					new Avatar(),
					new ArbitoreInitiative(),
					new Decide(),//
					new Hug(),//
					new BigEmote(),//
					new Countdown(),//
					new Birthday(),//
					new Deathbattle(),
					new Battle(),
					new Leaderboard(),
					new Battlestats(),//
					new HighFive(),
					new Propose(),
					new Couples(),
					new Spouse(),
					new Launch(),
					new Waiting(),
					new Stab(),
					new Pat(),
					new Nickname(),
					new EightBall(),
					new Stats(),
					new Say(),
					new Spoiler(),
					new Reactstats(),
					new Roll(),
					new Usage(),
					new UrbanDictionary(),
					new Sort(),
					new Userinfo(),
					new Serverinfo(),
					new Logs(),
					new Servericon(),
					//COUPLES
					new Kiss(),
					new Divorce(),
					new DivorceCourt(),
					//MOD
					poll,
					new Karaoke(),
					new Purge(),
					new Shutdown(),//
					new Ping(),
					new Add(),
					new Name(),
					new Description(),
					new Roles(),
					new Boop(),
					//OTHER
					//					new BanEveryone(),
					new Perms(),
					new Selfnickname(),
					new Restart(),
					new ModDivorce()
			};

		client.addCommands(commands);
		
		JDA jda = null;
		try {
			jda = new JDABuilder(AccountType.BOT).setToken(MYTOKEN)
					.setStatus(OnlineStatus.ONLINE)
					.setActivity(Activity.playing("Type %help"))
					.addEventListeners(waiter,client.build())
					.build();
		} catch (LoginException e) {
			System.out.println("LOGIN EXCEPTION: " + e.toString());
			System.exit(0);
		} catch (IllegalArgumentException e) {
			System.out.println("ILLEGALARGUMENT EXCEPTION: " + e.toString());
			System.exit(0);
		}

		Object[] listeners = new Object[] 
				{
						new MessageListener(),
						new EmoteTrackListener(),
						poll,
						new SpellCheck()
				};
		jda.addEventListener(listeners);
		
		
		jda.awaitReady();
		new BirthdayChecker(jda);	//Not a listener, checks on a schedule
		jda.getTextChannelById(TESTCHANNELID).sendMessage("**BeepBoop Online**").queue();
		logger.info("Finished starting");

	}


}
