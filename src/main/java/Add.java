import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class Add extends Command 
{
	public Add()
	{
		this.name = "add";
		this.children = new Command[] {new AddHug(), new AddBattle(), new AddKiss()};
		this.category = BeepBoop.MOD;
		this.arguments = "hug ` **or** ` battle ` **or** ` kiss";
		this.help =  "\n		•`hug` include `[user]`"
					+"\n		•`battle` include `[winner]` or `[loser]`"
					+"\n		•`kiss` include `[husband]` and `[wife]`";
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		
	}

}
