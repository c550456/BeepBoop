import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

public class CouplesMethods 
{

	public static void adjustKarma(int adjust, User user, JDA jda)
	{
		
		List<String> couples = new ArrayList<>();
		try {
			couples = Files.readAllLines(UsefulMethods.findPath("couples.txt"));
		} catch (IOException e) {
			System.out.println("UNABLE TO FIND couples.txt!");
		}

		for(int i = 0; i < couples.size(); i++)
		{
			String line = couples.get(i);
			if(line.contains(user.getId()))
			{
				String[] parts = line.split(", ");
				int newKarma = getKarma(user, jda) + adjust;
				if(newKarma < 0)
					newKarma = 0;
				couples.set(i, parts[0] + ", " + parts[1] + ", " + newKarma);
			}
		}

		try {
			Files.write(UsefulMethods.findPath("couples.txt"), couples, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static boolean hasSpouse(User user, JDA jda)
	{
		boolean married = false;
		List<String> couples = new ArrayList<>();
		try {
			couples = Files.readAllLines(UsefulMethods.findPath("couples.txt"));
		} catch (IOException e) {
			jda.getTextChannelById(BeepBoop.TESTCHANNELID).sendMessage("UNABLE TO FIND couples.txt!").queue();
		}
		for(String line : couples)
		{
			if(line.contains(user.getId()))
			{
//				System.out.println("line: " + line + " | ");
				married = true;
				break;
			}
		}
//		System.out.println(user.getName() + " Has spouse? " + married + "\nWith ID: " + user.getId());
		return married;
	}


	public static User getSpouse(User user, JDA jda)
	{
		User spouse = null;

		if(!hasSpouse(user, jda))
			return null;
		else
		{
			List<String> couples = new ArrayList<>();
			try {
				couples = Files.readAllLines(UsefulMethods.findPath("couples.txt"));
			} catch (IOException e) {
				jda.getTextChannelById(BeepBoop.TESTCHANNELID).sendMessage("UNABLE TO FIND couples.txt!").queue();
			}

			for(String line : couples)
			{
				if(line.contains(user.getId()))
				{
					String[] people = line.split(", ");
					if(user.getId().equals(people[0]))
						spouse = jda.getUserById(people[1]);
					else
						spouse = jda.getUserById(people[0]);
				}
			}
		}
//		System.out.println("null spouse? " + spouse == null);
		return spouse;
	}
	
	public static void divorce(User user)
	{
		List<String> couples = new ArrayList<>();
		couples = UsefulMethods.getFileLines("couples.txt");
		
		for(int i = 0; i < couples.size(); i++)
		{
			String line = couples.get(i);
			if(line.contains(user.getId()))
			{
				couples.remove(i);
				break;
			}
		}
		
		try {
			Files.write(UsefulMethods.findPath("couples.txt"), couples, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		couples = UsefulMethods.getFileLines("beatings.txt");
		for(int i = 0; i < couples.size(); i++)
		{
			String line = couples.get(i);
			if(line.contains(user.getId()))
			{
				couples.remove(i);
				break;
			}
		}
		
		try {
			Files.write(UsefulMethods.findPath("beatings.txt"), couples, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int getKarma(User user, JDA jda)
	{
		int karma = 0;
		if(!CouplesMethods.hasSpouse(user, jda))
			karma = -1;
		else
		{
			List<String> couples = new ArrayList<>();
			try {
				couples = Files.readAllLines(UsefulMethods.findPath("couples.txt"));
			} catch (IOException e) {
				jda.getTextChannelById(BeepBoop.TESTCHANNELID).sendMessage("UNABLE TO FIND couples.txt!").queue();
			}

			for(String line : couples)
			{
				String[] info = line.split(", ");
				if(line.contains(user.getId()))
					karma = Integer.parseInt(info[2]);
			}
		}
		return karma;
	}

	public static void addSpouseBattle(User user, JDA jda)
	{
		List<String> count = new ArrayList<>();
		count = UsefulMethods.getFileLines("beatings.txt");
		User spouse = CouplesMethods.getSpouse(user, jda);
		
		for(int i = 0; i < count.size(); i++)
		{
			String line = count.get(i);
			if(line.contains(user.getId()))
			{
				count.remove(i);
				break;
			}
		}
		
		count.add(user.getId() + ", " + spouse.getId() + ", " + (getSpouseBattles(user) + 1));
		
		try {
			Files.write(UsefulMethods.findPath("beatings.txt"), count, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int getSpouseBattles(User user)
	{
		int battles = 0;
		List<String> count = new ArrayList<>();
		count = UsefulMethods.getFileLines("beatings.txt");

		for(String line : count)
		{
			if(!line.contains(user.getId()))
				continue;

			String[] parts = line.split(", ");
			if(parts.length < 3)
				break;
			else
				battles = Integer.parseInt(parts[2]);
		}

		return battles;
	}

}
