import java.io.File;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class Restart extends Command
{
	public Restart()
	{
		this.name = "restart";
		this.ownerCommand = true;
	}
	@Override
	protected void execute(CommandEvent event) 
	{
		if(event.getArgs().toLowerCase().equals("beepboop"))
		{
			String path = System.getProperty("user.dir");
			event.getJDA().getTextChannelById("307755754964647947").sendMessage(path).queue();
			
			File jar = new File(path + File.separator + "BeepBoopJDA.jar");
			if(jar.exists())
			{
				Runtime runtime = Runtime.getRuntime();
				try
				{
					event.getTextChannel().sendMessage("**Shutting down\nStarting " + jar + "**").complete();
					runtime.exec("java -jar" + jar);
					System.exit(0);
				}
				catch(Exception e)
				{
					event.getJDA().getTextChannelById(BeepBoop.TESTCHANNELID).sendMessage(e.getMessage()).queue();
				}
			}
			else
			{
				event.getJDA().getTextChannelById(BeepBoop.TESTCHANNELID).sendMessage(jar + "does not exist!").queue();
			}
		}
	}

}
