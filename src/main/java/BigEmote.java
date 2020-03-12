import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import org.apache.commons.lang3.StringUtils;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.vdurmont.emoji.EmojiParser;

import net.dv8tion.jda.api.entities.Message.MentionType;

public class BigEmote extends Command 
{

	public BigEmote()
	{
		this.name = "emote";
		this.aliases = new String[] {"emoji", "bigemote", "bigemoji"};
		this.arguments = "<custom server emoji> <size to scale to>";
		this.category = BeepBoop.FUN;
		this.help = "Displays a larger version of a custom server emoji. Size is optional, "
				+ "and should be the percentage you want to scale to. For example, `%emote [emote] 150` "
				+ "produces a picture 150% the size of the default";
	}

	@Override
	protected void execute(CommandEvent event) 
	{
		event.getChannel().sendTyping().queue();
		String[] parts = event.getArgs().split("\\s+");
		String emoteSize = "100";	//The size of the emote picture to display, in terms of % of default
		if(parts.length > 1)
		{
			emoteSize = parts[1];
		}

		String args = event.getArgs();
		//String emoteIDString = "<:(\\S+?):(.+?)>";	//Emote ID is group(2)
		Pattern emoteIDPattern = MentionType.EMOTE.getPattern();//Pattern.compile(emoteIDString);
		Matcher customMatch = emoteIDPattern.matcher(args);

		try {
			if(customMatch.find()) //if Emote (not emoji)
			{
				boolean isAnimated = customMatch.group().substring(1).startsWith("a");
				URL emoteURL;
				String extension = isAnimated ? "gif" : "png";

				File outputfile = new File(UsefulMethods.findFile("emote." + extension, null).getAbsolutePath());	//Is not findPath because new File needs a String
				emoteURL = new URL("https://cdn.discordapp.com/emojis/" + customMatch.group(2) + "." + extension);
				//				emoteURL = new URL("https://cdn.discordapp.com/emojis/498301412116135968.gif?v=1");
				URLConnection url = emoteURL.openConnection();
				url.setRequestProperty("User-Agent", "Java");

			
				if(!StringUtils.isNumeric(emoteSize))
				{
					event.reply("That is not a valid number!");
					return;
				}
				
				int size = Integer.parseInt(emoteSize);
				
				if(size > 5000)
					throw new IllegalArgumentException();

				if(isAnimated)
				{
					//ImageIO.write(ImageIO.read(url.getInputStream()), extension, outputfile);
					InputStream is = url.getInputStream();
					OutputStream os = new FileOutputStream(outputfile);
					byte[] b = new byte[2048];
					int length;
					while ((length = is.read(b)) != -1) {
						os.write(b, 0, length);
					}
					os.close();
				}
				else
				{
					ImageIO.write(UsefulMethods.createResizedCopy(ImageIO.read(url.getInputStream()), size), extension, outputfile);
				}
				event.getTextChannel().sendFile(outputfile).queue();
			}
			else if (!EmojiParser.extractEmojis(args).isEmpty())	//if emoji (not Emote)
			{
				File outputfile = new File(UsefulMethods.findFile("emote.png", null).getAbsolutePath());
				String emoji = EmojiParser.extractEmojis(args).get(0);
				emoji = EmojiParser.parseToHtmlHexadecimal(emoji);
				URL emoteURL = new URL("https://raw.githubusercontent.com/twitter/twemoji/gh-pages/2/72x72/" + emoji.substring(3, emoji.length()-1) + ".png");

				URLConnection url = emoteURL.openConnection();
				url.setRequestProperty("User-Agent", "Java");

				int size = Integer.parseInt(emoteSize);

				if(size > 5000)
					throw new IllegalArgumentException();

				ImageIO.write(UsefulMethods.createResizedCopy(ImageIO.read(url.getInputStream()), size), "png", outputfile);
				event.getTextChannel().sendFile(outputfile).queue();
			}
			else //TODO: allow user to pass :emoteName: and extract emote from that
			{
				throw new Exception("No emote or emoji was passed");
			}
		}catch (IllegalArgumentException iae) 
		{
			System.out.println(iae.getMessage() + "... Cause: " + iae.getCause());
			iae.printStackTrace();
			event.reply("That's not a valid picture size. (Valid sizes are 0-5000)");
		}
		catch (Exception e1) 
		{
			System.out.println("BAD EMOTE");
			e1.printStackTrace();
			event.reply("Sorry, that's not a supported emote");
		}
		/*
		else
		{
			event.reply("Sorry, that's not a supported emote");
		}
		 */
	}

}
