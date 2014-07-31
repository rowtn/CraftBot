package in.parapengu.craftbot.auth;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BotAuthenticator {

	private String username;
	private String password;
	private Map<String, String> result;

	public BotAuthenticator(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public static String toString(InputStream input) throws IOException {
		return IOUtils.toString(input);
	}

	public boolean authenticate() throws IOException, JSONException {
		URL var4 = new URL("https://authserver.mojang.com/authenticate");
		HttpURLConnection hc = (HttpURLConnection) var4.openConnection();
		hc.setRequestProperty("content-type", "application/json; charset=utf-8");
		hc.setRequestMethod("POST");
		hc.setDoInput(true);
		hc.setDoOutput(true);
		hc.setUseCaches(false);
		OutputStreamWriter wr = new OutputStreamWriter(hc.getOutputStream());
		JSONObject data = new JSONObject();
		JSONObject agent = new JSONObject();
		agent.put("name", "minecraft");
		agent.put("version", "1");
		data.put("agent", agent);
		data.put("username", username);
		data.put("password", password);
		wr.write(data.toString());
		wr.flush();

		InputStream stream = hc.getInputStream();
		JSONObject json = new JSONObject(toString(stream));
		Map<String, String> map = new HashMap<>();
		map.put("access-token", json.getString("accessToken"));
		map.put("client-token", json.getString("clientToken"));
		map.put("selected-profile-id", json.getJSONObject("selectedProfile").getString("id"));
		map.put("selected-profile-name", json.getJSONObject("selectedProfile").getString("name"));
		result = map;
		return true;
	}

	public String getAccessToken() {
		return result.get("access-token");
	}

	public String getClientToken() {
		return result.get("client-token");
	}

	public String getProfileID() {
		return result.get("selected-profile-id");
	}

	public String getProfileName() {
		return result.get("selected-profile-name");
	}

}
