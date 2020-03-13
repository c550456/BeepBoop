import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
//import com.jagrosh.jdautilities.menu.pagination.Paginator;
//import com.jagrosh.jdautilities.menu.pagination.PaginatorBuilder;
//import com.jagrosh.jdautilities.waiter.EventWaiter;
import com.vdurmont.emoji.EmojiManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UsefulMethods 
{
	/*
	 * Storing the unicode of various emoji for later use everywhere
	 * TODO: store these in database instead?
	 */
	final static String gun = EmojiManager.getForAlias("gun").getUnicode();
	final static String boom = EmojiManager.getForAlias("boom").getUnicode();
	final static String wavyDash = EmojiManager.getForAlias("wavy_dash").getUnicode();
	final static String musicalNote = EmojiManager.getForAlias("musical_note").getUnicode();
	final static String runner = EmojiManager.getForAlias("runner").getUnicode();
	final static String door = EmojiManager.getForAlias("door").getUnicode();
	final static String check = EmojiManager.getForAlias("white_check_mark").getUnicode();
	final static String question = EmojiManager.getForAlias("question").getUnicode();
	final static String knife = EmojiManager.getForAlias("knife").getUnicode();
	final static String gameDie = EmojiManager.getForAlias("game_die").getUnicode();
	final static String eightBall = EmojiManager.getForAlias("8ball").getUnicode();

	public static void reply(MessageReceivedEvent event, String reply)
	{
		event.getTextChannel().sendMessage(reply).queue();
	}

	public static void reply(MessageReceivedEvent event, File file)
	{
		event.getTextChannel().sendFile(file).queue();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getSingleValueFromSQL(String query, String database) throws SQLException
	{
		T value = null;
		ResultSet result = runSQLQuery(query,database);
		if(result.next() && result.getObject(1) != null)
		{
			value = (T) result.getObject(1);
		}
		return value;
	}
	
	public static ResultSet runSQLQuery(String query, String database) throws SQLException
	{
		if(database == null)
		{
			database = BeepBoop.MYSERVERID; //My testing database
		}
		BeepBoop.logger.debug("\nDatabase: " + database + "\nSQL: " + query);
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		String connectionUrl = "";
		List<String> lines = getFileLines("BotSettings.txt");
		for(String line : lines)
		{
			if(line.contains("ConnectionString:"))
				connectionUrl = line.split("ConnectionString:\\s*")[1]+ database;
		}
		Connection con = DriverManager.getConnection(connectionUrl);
		
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		return rs;
	}
	
	public static Statement executeSQLProc(String query, String database) throws SQLException
	{
		if(database == null)
		{
			database = BeepBoop.MYSERVERID; //My testing database
		}
		BeepBoop.logger.debug("\nDatabase: " + database + "\nSQL: " + query);
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		String connectionUrl = "";
		List<String> lines = getFileLines("BotSettings.txt");
		for(String line : lines)
		{
			if(line.contains("ConnectionString:"))
				connectionUrl = line.split("ConnectionString:\\s*")[1]+ database;
		}
		Connection con = DriverManager.getConnection(connectionUrl);
		
		Statement stmt = con.createStatement();
		stmt.execute(query);
		return stmt;
	}

	public static File findFile(String filename, File dir) throws NullPointerException //to force myself to account for a null file
	{
		if(dir == null)		
			dir = new File(System.getProperty("user.dir"));	//Allows for calling of the method to be (String filename, null)

		File toReturn = null;
		for(File f : dir.listFiles())
		{
			if(toReturn != null)
				break;
			if(f.isDirectory() && !filename.equals(f.getName()))
			{
				toReturn = findFile(filename, f);
			}
			else if(filename.equalsIgnoreCase(f.getName()))
			{
				toReturn = f;
			}

		}
		return toReturn;
	}

	public static Path findPath(String filename)
	{
		return findFile(filename, null).toPath();
	}

	public static List<String> getFileLines(String filename)
	{
		List<String> lines = new ArrayList<>();

		try {
			lines = Files.readAllLines(findPath(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public static Color getMemberColor(Member member)
	{
		Color color = null;
		for(Role role : member.getRoles())
		{
			if(role.getColor() != null)
			{
				color = role.getColor();
				break;
			}
		}
		return color;
	}


	public static BufferedImage overlayImage(BufferedImage background, BufferedImage overlay, String text, String font, Color color, int fontSize, int x, int y, int textX, int textY)
	{

		BufferedImage image = background;

		int w = Math.max(image.getWidth(), overlay.getWidth());
		int h = Math.max(image.getHeight(), overlay.getHeight());
		BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		Graphics g = combined.getGraphics();
		g.drawImage(overlay, x, y, null);	//(670, 265) is the coordinate where the top left corner of the avatar needs to be
		g.drawImage(image, 0, 0, null);
		if(text != null && !text.equals("That user has not yet battled!"))
		{
			g.drawImage(textToImage(text, font, color, fontSize, background, textX, textY), 0, h-350, null);
		}
		else if(text != null && text.equals("That user has not yet battled!"))
		{
			g.drawImage(textToImage(text, font, color, fontSize, background, textX, textY), 0, h-150, null);
		}
		g.dispose();
		return combined;
	}

	public static BufferedImage urlToImage(String urlString)	//Takes a URL and returns the image represented by it
	{
		URL baseURL;
		BufferedImage image = null;
		try {
			baseURL = new URL(urlString);
			URLConnection url = baseURL.openConnection();
			url.setRequestProperty("User-Agent", "Java");
			image = ImageIO.read(url.getInputStream());

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("There was an error getting the image from the URL: " + urlString 
					+ "\nError: " + e.getMessage() + "\nCause: " + e.getCause());
		}
		return image;
	}

	public static File urlToGif(String urlString, File file)
	{
		byte[] b = new byte[1];
		FileOutputStream fo = null;
		try
		{
			URL url = new URL(urlString);
			URLConnection urlConnection = url.openConnection();
			urlConnection.setRequestProperty("User-Agent", "Java");
			urlConnection.connect();
			DataInputStream di = new DataInputStream(urlConnection.getInputStream());

			fo = new FileOutputStream(file);
			while (-1 != di.read(b, 0, 1))
				fo.write(b, 0, 1);
			di.close();
			fo.close();
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		return file;
	}

	public static BufferedImage resizeByPixel(BufferedImage originalImage, int xPixels, int yPixels)
	{
		BufferedImage scaledBI = new BufferedImage(xPixels, yPixels, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = scaledBI.createGraphics();

		g.drawImage(originalImage, 0, 0, xPixels, yPixels, null); 
		g.dispose();
		return scaledBI;
	}

	public static BufferedImage createResizedCopy(BufferedImage originalImage, int scale) throws IllegalArgumentException	//Where scale is the percentage to grow/shrink by
	{
		int newH = (int) (originalImage.getHeight() * (scale/100.0));
		int newW = (int) (originalImage.getWidth() * (scale/100.0));
		if(newH <= 0)
		{
			newH = 1;
		}
		if(newW <= 0)
		{
			newW = 1;
		}
		//		System.out.println("resizing...");
		BufferedImage scaledBI = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = scaledBI.createGraphics();

		g.drawImage(originalImage, 0, 0, newW, newH, null); 
		g.dispose();
		return scaledBI;
	}

	public static BufferedImage createWandSizeCopy(BufferedImage originalImage)	//Resizes an image to fit in the wand picture for battlestats
	{
		int newH = 559;
		int newW = 559;
		BufferedImage scaledBI = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = scaledBI.createGraphics();

		g.drawImage(originalImage, 0, 0, newW, newH, null); 
		g.dispose();
		return scaledBI;
	}

	public static BufferedImage textToImage(String text, String wantedFont, Color color, int size, BufferedImage background, int x, int y)
	{
		/*
	           Because font metrics is based on a graphics context, we need to create
	           a small, temporary image so we can ascertain the width and height
	           of the final image
		 */
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		String myfont = "Times New Roman";
		int fontsize = size < 0 ? 85 : size;
		Font font = new Font("Times New Roman", Font.PLAIN, 80);
		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for(String f : fonts)
		{
			if(wantedFont != null && f.equals(wantedFont))
			{
				myfont = f;
				fontsize += 20;
			}
		}
		font = new Font(myfont, Font.PLAIN, fontsize);
		g2d.setFont(font);
		//		System.out.println("USING FONT: " + g2d.getFontMetrics());
		FontMetrics fm = g2d.getFontMetrics();
		int width = 0;
		int height = 0;
		try
		{
			//			width = ImageIO.read(findFile("background.png", null)).getWidth();
			//			height =ImageIO.read(findFile("background.png", null)).getHeight();
			width = background.getWidth();
			height = background.getHeight();
		} catch (Exception e){}
		g2d.dispose();

		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		g2d = img.createGraphics();
		g2d.setFont(font);
		fm = g2d.getFontMetrics();
		if(color != null)
		{
			g2d.setColor(color);
		}
		else
		{
			g2d.setColor(Color.BLACK);
		}

		String[] lines = text.split("\n");
		//		int textHeight = 0;
		for(String line : lines)
		{
			g2d.drawString(line, x, y += fm.getAscent());
		}
		g2d.dispose();

		return img;
	}


	public static User hasMention(String word, JDA jda)
	{
		String mentionString = "<@!?(\\d{17,})>";
		Pattern mentionPattern = Pattern.compile(mentionString);
		Matcher match = mentionPattern.matcher(word);
		User user = null;

		if(match.find())
		{
			user = jda.getUserById(match.group(1));
		}
		return user;
	}

	public static Member findMember(Guild guild, String args)
	{
		Member member = null;

		if(!guild.getMembersByName(args, true).isEmpty())
		{
			member = guild.getMembersByName(args, true).get(0);
		}
		else if(args.length() > 5 && !guild.getMembersByName(args.substring(0, args.length()-5), true).isEmpty())	//In case the username is entered with the discrim
		{
			for(Member m : guild.getMembersByName(args.substring(0, args.length()-5), true))
			{
				if(m.getUser().getDiscriminator().equals(args.substring(args.length()-4)))
				{
					//					System.out.println(args.substring(args.length()-4));
					member = m;
				}
			}
			//			String name = member == null? "member not found" : member.getUser().getName().concat("#") + member.getUser().getDiscriminator();
			//			System.out.println("Member: " + name);
		}
		else if(!guild.getMembersByNickname(args, true).isEmpty())
		{
			member = guild.getMembersByNickname(args, true).get(0);
		}

		return member;
	}

	public static void displayList(List<String> list, String title, MessageChannel channel, User authorizedUser, EventWaiter waiter)
	{

		Paginator paginator = new Paginator.Builder()
				.addItems(list.toArray(new String[list.size()]))
				.setEventWaiter(waiter)
				.waitOnSinglePage(true)
				.setUsers(authorizedUser)
				.useNumberedItems(true)
				.setItemsPerPage(10)
				.showPageNumbers(true)
				.setText(title)
				.build();

		paginator.display(channel);
	}
	
	public static void displayListNonNumbered(List<String> list, String title, Color color, MessageChannel channel, User authorizedUser, EventWaiter waiter)
	{
		Paginator paginator = new Paginator.Builder()
				.addItems(list.toArray(new String[list.size()]))
				.setEventWaiter(waiter)
				.waitOnSinglePage(true)
				.setColor(color)
				.setUsers(authorizedUser)
				.useNumberedItems(false)
				.setItemsPerPage(1)
				.showPageNumbers(true)
				.setText(title)
				.build();

		paginator.display(channel);
	}
	
	public static void displayList(List<String> list, String title, int itemsPerPage, MessageChannel channel, User authorizedUser, EventWaiter waiter, Color color)
	{
		Paginator paginator = new Paginator.Builder()
				.addItems(list.toArray(new String[list.size()]))
				.setEventWaiter(waiter)
				.setColor(color)
				.waitOnSinglePage(true)
				.useNumberedItems(true)
				.setUsers(authorizedUser)
				.setItemsPerPage(itemsPerPage)
				.setTimeout(2, TimeUnit.MINUTES)
				.showPageNumbers(true)
				.setText(title)
				.build();

		paginator.display(channel);
	}
	
	public static String capitalize(String input)
	{
		return input.substring(0, 1).toUpperCase().concat(input.substring(1));
	}

}

