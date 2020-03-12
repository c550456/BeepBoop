import net.dv8tion.jda.api.entities.User;

public class Couple 
{
	private User husband;
	private User wife;
	private int karma;
	private int fights;
	
	public Couple(User husband, User wife)
	{
		this.husband = husband;
		this.wife = wife;
		this.karma = 1000;
		this.fights = 0;
	}

	public User getHusband()
	{
		return this.husband;
	}
	
	public User getWife()
	{
		return this.wife;
	}
	
	public int getKarma() {
		return karma;
	}

	public void setKarma(int karma) {
		this.karma = karma;
	}

	public int getFights() {
		return fights;
	}

	public void setFights(int fights) {
		this.fights = fights;
	}

	
	
	
	
}
