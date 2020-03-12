import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class Battlestats extends Command 
{
	List<User> battlers = new ArrayList<>(); 
	Map<User, Integer> userRanks = new HashMap<>();
	public Battlestats()
	{
		this.name = "battlestats";
		this.category = BeepBoop.FUN;
		this.help = "Displays the battle statistics for a specific user";
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		event.getTextChannel().sendTyping().queue();
//		TODO: FIGURE THIS OUT
		/*
		try {
			Connection conn = DriverManager.getConnection("jdbc:derby:userProfilesDB;create=false");
			
			Statement s = conn.createStatement();
			
			ResultSet rs = s.executeQuery("SELECT points FROM Leaderboard WHERE id = " + event.getAuthor().getId());
			while(rs.next())
			{
				System.out.println(event.getJDA().getUserById(rs.getString(1)).getName() + " has " + 
						rs.getInt(2) + " points.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		*/
		try {
			Files.readAllLines(UsefulMethods.findPath("leaderboard.txt"))
			.forEach(s -> 
			{
				String[] line = s.split("\\s:\\s");
				User user = event.getJDA().getUserById(line[0]);
				Integer rank = Integer.parseInt(line[1]);
				battlers.add(user);
				userRanks.put(user, rank);
			});
		} catch (IOException e) {
			System.out.println("ERROR GETTING LEADERBOARD FOR BATTLESTATS");
			e.printStackTrace();
			System.exit(0);
		}
		
		
		displayStats(event);
		battlers.clear();
	}
	
	public int getRank(User user)
	{
		return userRanks.get(user);
	}
	
	public void displayStats(CommandEvent event)
	{
		Member member = null;
		List<User> users = event.getMessage().getMentionedUsers();
		int place = 0;
		member = users.isEmpty() ? event.getMember() : event.getGuild().getMember(users.get(0));	//If no mentions, defaults to user who called command
		BufferedImage image = UsefulMethods.urlToImage(member.getUser().getEffectiveAvatarUrl()+"?size=1024");
		
		
		Collections.sort(battlers, (a, b) -> sort(a, b));
		
		for(int i = 0; i < battlers.size(); i++)
		{
			if(battlers.get(i).equals(member.getUser()))
				place = i+1;
		}
		
		String statDescription = place != 0 ? " " + member.getEffectiveName() + ", \nyou have " + getRank(member.getUser()) +  " TELL Stormfather to fix this feature points"
				+ "\n Rank: " + place + "/" + battlers.size()  
								: "That user has not yet battled!";
		
		try {
			image = UsefulMethods.overlayImage(ImageIO.read(UsefulMethods.findFile("background.png", null)),
					UsefulMethods.createWandSizeCopy(image), statDescription, "Young Itch AOE", new Color(17, 5, 231, 255), -1, 670, 265, 0, 0);
		} catch (NullPointerException | IOException e1) {
			e1.printStackTrace();
			System.out.println("Could not find appropriate image!");
			return;
		}
		
		File outputfile = UsefulMethods.findFile("outputfile.png", null);
		try {
			ImageIO.write(image, "png", outputfile);
			event.reply(outputfile, "battlestats.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public int sort(User u1, User u2)
	{
		if(getRank(u1) == getRank(u2))
			return 0;
		else if (getRank(u1) > getRank(u2))
			return -1;
		else
			return 1;
	}

}
