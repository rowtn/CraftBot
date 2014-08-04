package in.parapengu.craftbot.util;

import in.parapengu.craftbot.bot.ChatColor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChatFormatter {

	public static String parse(String string) {
		try {
			JSONObject json = new JSONObject(string);
			return parse(json);
		} catch(JSONException ex) {
			return string.substring(1, string.length() - 1);
		}
	}

	public static String parse(JSONObject json) {
		StringBuilder message = new StringBuilder();
		if(json.has("bold") && json.getBoolean("bold"))
			message.append(ChatColor.BOLD);
		if(json.has("italic") && json.getBoolean("italic"))
			message.append(ChatColor.ITALIC);
		if(json.has("underlined") && json.getBoolean("underlined"))
			message.append(ChatColor.UNDERLINE);
		if(json.has("strikethrough") && json.getBoolean("strikethrough"))
			message.append(ChatColor.STRIKETHROUGH);
		if(json.has("obfuscated") && json.getBoolean("obfuscated"))
			message.append(ChatColor.MAGIC);
		if(json.has("color"))
			message.append(ChatColor.valueOf(json.getString("color").toUpperCase()));

		if(json.has("translate")) {
			String text = json.getString("translate");
			if(json.has("with")) {
				JSONArray array = json.getJSONArray("with");
				String[] translationValues = new String[array.length()];
				for(int i = 0; i < translationValues.length; i++) {
					Object object = array.get(i);

					String value;
					if(object instanceof JSONObject)
						value = parse((JSONObject) object);
					else
						value = (String) object;
					translationValues[i] = value;
				}

				text = String.format(text, (Object[]) translationValues);
			}

			message.append(text);
		} else if(json.has("text")) {
			message.append(json.get("text"));
		}

		if(json.has("extra")) {
			JSONArray extra = json.getJSONArray("extra");
			for(int i = 0; i < extra.length(); i++) {
				Object object = extra.get(i);
				if(object instanceof JSONObject) {
					message.append(parse((JSONObject) object));
				} else {
					message.append(object);
				}
			}
		}

		return message.toString();
	}

}
