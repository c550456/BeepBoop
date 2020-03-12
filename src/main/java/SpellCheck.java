import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
//import net.dv8tion.jda.webhook.WebhookClient;
//import net.dv8tion.jda.webhook.WebhookClientBuilder;
//import net.dv8tion.jda.webhook.WebhookMessage;
//import net.dv8tion.jda.webhook.WebhookMessageBuilder;

public class SpellCheck extends ListenerAdapter
{
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		String content = event.getMessage().getContentStripped();
		String[] parts = content.split("\\s+");
		List<String> newParts = new CopyOnWriteArrayList<>();
		newParts.addAll(Arrays.asList(parts));
		boolean woah = false;
		Matcher m = null;
		for(int i = 0; i < newParts.size(); i++)
		{
			String part = newParts.get(i);
			m = Pattern.compile("^(?i)woah").matcher(part);
			if(m.find())
			{
				newParts.set(i, part.substring(0,1) + part.substring(3,4) + part.substring(1, 3)); //keeps capitalization
				woah = true;
			}
		}
		if(!(woah))
			return;
		Webhook whoa = null;
		try {
			whoa = event.getTextChannel().createWebhook(event.getMember().getEffectiveName()).submit().get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		WebhookClient client = new WebhookClientBuilder(whoa.getUrl()).build();

		String newContent = "";
		for(String part : newParts)
		{
			newContent += part + " ";
		}
		WebhookMessage message = new WebhookMessageBuilder()
				.setContent(newContent)
				.setAvatarUrl(event.getAuthor().getAvatarUrl())
				.setUsername(event.getMember().getEffectiveName())
				.build();

		event.getMessage().delete().queue();
		try {
			client.send(message).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		client.close();
		whoa.delete().queue(null, f -> System.out.println(f.getMessage()));
	}

	public void whoa(MessageReceivedEvent event)
	{

	}
}
