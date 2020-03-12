import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class UrbanDictionary extends Command 
{
	private String linkUrl = "https://www.urbandictionary.com/define.php?term=";
	public UrbanDictionary()
	{
		this.name = "urbandictionary";
		this.aliases = new String[] {"urban", "urbandict"};
		this.help = "Returns the top result from urban dictionary of your search terms";
	}

	@Override
	protected void execute(CommandEvent event)
	{
		String args = event.getArgs().replaceAll("\\s+", "+");
		try {
			URL url = new URL("http://api.urbandictionary.com/v0/define?term=" + args);
			URLConnection con = url.openConnection();
			InputStream in = con.getInputStream();

			Scanner scan = new Scanner(in);
			String jsonstring = "";
			while(scan.hasNext())
			{
				jsonstring += scan.next() + " ";
			}
			scan.close();

			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create(); 
			JsonObject json = gson.fromJson(jsonstring, JsonElement.class).getAsJsonObject();
//			System.out.println(json);
			JsonArray array = json.get("list").getAsJsonArray();
			if(array.size() == 0)
			{
				event.reply("**No results found!**");
				return;
			}
			List<String> definitions = new ArrayList<>();
			for(int i = 0; i < Math.min(10, array.size()); i++)
			{
				definitions.add(array.get(i).getAsJsonObject().get("definition").getAsString()
						+ "\n\n**Example:** " + array.get(i).getAsJsonObject().get("example").getAsString());
			}

			String linkPatternString = "\\[(.*?)\\]"; //Group 1 is the actual words in the brackets
			Pattern pattern = Pattern.compile(linkPatternString);
			
			//formatting all links to other UrbanDictionary terms
			for(int i = 0; i < definitions.size(); i++)
			{
				Matcher m = pattern.matcher(definitions.get(i));
				String updatedDefinition = definitions.get(i);
				List<String> alreadyFound = new ArrayList<>();
				while(m.find())
				{
					String match = m.group(1);
					if(alreadyFound.contains(match))
					{
						continue;
					}
					else
					{
						alreadyFound.add(match);
					}
					updatedDefinition = updatedDefinition.replaceAll("\\[" + match + "\\]", 
							"[" + match + "](" + linkUrl + match.replaceAll("\\s+", "+") + ")");
				}
				
				if(updatedDefinition.length() > 2048)
				{
					definitions.set(i, updatedDefinition.substring(0, 2044) + "...");
				}
				else
				{
					definitions.set(i, updatedDefinition);
				}
			}

			UsefulMethods.displayListNonNumbered(definitions, "**Urban Dictionary results for:** `" + event.getArgs() + "`", new Color(232, 98, 34), event.getChannel(), event.getAuthor(), BeepBoop.waiter);
			/*			MessageEmbed embed = new EmbedBuilder()
					.setTitle("Urban Dictionary result for: " + event.getArgs())
					.setDescription(updatedDefinition)
					.setColor(new Color(232, 98, 34)) //UrbanDictionary's shade of orange
					.build();
			event.reply(embed);
			 */
		} catch (MalformedURLException e) 
		{
			System.out.println("BAD URL");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

}
