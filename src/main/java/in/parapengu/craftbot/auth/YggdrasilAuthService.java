package in.parapengu.craftbot.auth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class YggdrasilAuthService implements AuthService<YggdrasilSession> {

	private static final String LOGIN_URL = "https://authserver.mojang.com";
	private static final String LOGIN_AUTHENTICATE_URL = LOGIN_URL + "/authenticate";
	private static final String LOGIN_REFRESH_URL = LOGIN_URL + "/refresh";
	private static final String LOGIN_VALIDATE_URL = LOGIN_URL + "/validate";
	private static final String LOGIN_SIGNOUT_URL = LOGIN_URL + "/signout";
	private static final String LOGIN_INVALIDATE_URL = LOGIN_URL + "/invalidate";
	private static final String SERVER_AUTHENTICATE_URL = "https://sessionserver.mojang.com/session/minecraft/join";
	private static final int LOGIN_VERSION = 1;

	@Override
	public YggdrasilSession login(String username, String password) throws Exception {
		JSONObject agent = new JSONObject();
		agent.put("name", "Minecraft");
		agent.put("version", LOGIN_VERSION);

		JSONObject request = new JSONObject();
		request.put("agent", agent);
		request.put("username", username);
		request.put("password", password);

		JSONObject response = post(LOGIN_AUTHENTICATE_URL, request);
		if(response == null)
			throw new IOException("Empty login response");
		checkError(response);
		return parse(response, password);
	}

	public void logout(YggdrasilSession session) throws AuthenticationException, IOException {
		logout(session.getUsername(), session.getPassword());
	}

	public void logout(String username, String password) throws AuthenticationException, IOException {
		if(username == null || username.trim().isEmpty())
			throw new IllegalArgumentException("Invalid username");
		if(password == null || password.trim().isEmpty())
			throw new IllegalArgumentException("Invalid password");

		JSONObject request = new JSONObject();
		request.put("username", username);
		request.put("password", password);

		JSONObject response = post(LOGIN_SIGNOUT_URL, request);
		if(response != null)
			checkError(response);
	}

	public void validate(YggdrasilSession session) throws AuthenticationException, IOException {
		if(!session.isValidForAuthentication())
			throw new IllegalArgumentException("Session must be usable for authentication to validate");

		JSONObject request = new JSONObject();
		request.put("accessToken", session.getAccessToken().toString(16));

		JSONObject response = post(LOGIN_VALIDATE_URL, request);
		if(response != null)
			checkError(response);
	}

	public void invalidate(YggdrasilSession session) throws AuthenticationException, IOException {
		if(!session.isValidForAuthentication())
			throw new IllegalArgumentException("Session must be usable for authentication to invalidate");
		if(session.getClientToken() == null)
			throw new IllegalArgumentException("Session must have client token to refresh");

		JSONObject request = new JSONObject();
		request.put("accessToken", session.getAccessToken().toString(16));
		request.put("clientToken", session.getClientToken().toString());

		JSONObject response = post(LOGIN_INVALIDATE_URL, request);
		if(response != null)
			checkError(response);
	}

	public YggdrasilSession refresh(YggdrasilSession session) throws AuthenticationException, IOException {
		if(!session.isValidForAuthentication())
			throw new IllegalArgumentException("Session must be usable for authentication to refresh");
		if(session.getClientToken() == null)
			throw new IllegalArgumentException("Session must have client token to refresh");

		JSONObject request = new JSONObject();
		request.put("accessToken", session.getAccessToken().toString(16));
		request.put("clientToken", session.getClientToken().toString());

		JSONObject response = post(LOGIN_REFRESH_URL, request);
		try {
			YggdrasilSession newSession = parse(response, session.getPassword());
			return new YggdrasilSession(newSession.getUsername(), newSession.getPassword(), newSession.getAccessToken(), newSession.getClientToken(), newSession.getSelectedProfile(), session.getAvailableProfiles());
		} catch(Exception exception) {
			throw new IOException("Unable to parse server response", exception);
		}
	}

	@Override
	public void authenticate(YggdrasilSession session, String serverId) throws AuthenticationException, IOException {
		if(!session.isValidForAuthentication())
			throw new IllegalArgumentException("Session must be usable for authentication to refresh");
		if(session.getSelectedProfile() == null)
			throw new IllegalArgumentException("Session must have selected profile");

		JSONObject request = new JSONObject();
		request.put("accessToken", session.getAccessToken().toString(16));
		request.put("selectedProfile", session.getSelectedProfile().getId());
		request.put("serverId", serverId);

		JSONObject response = post(SERVER_AUTHENTICATE_URL, request);
		if(response != null)
			checkError(response);
	}

	private JSONObject post(String targetURL, JSONObject request) throws IOException {
		String requestValue = request.toString(2);
		HttpsURLConnection connection = null;
		try {
			URL url = new URL(targetURL);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");

			connection.setRequestProperty("Content-Length", Integer.toString(requestValue.length()));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setReadTimeout(3000);
			connection.setConnectTimeout(3000);

			connection.connect();

			try(DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
				out.writeBytes(requestValue);
				out.flush();
			}

			try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				StringBuilder response = new StringBuilder();
				String line;
				while((line = reader.readLine()) != null) {
					if(response.length() > 0)
						response.append('\n');
					response.append(line);
				}
				if(response.toString().trim().isEmpty())
					return null;
				try {
					return new JSONObject(response.toString());
				} catch(JSONException exception) {
					throw new IOException("Response not valid JSON: " + response, exception);
				}
			}
		} catch(IOException exception) {
			throw exception;
		} catch(Exception exception) {
			throw new IOException("Error connecting", exception);
		} finally {
			if(connection != null)
				connection.disconnect();
		}
	}

	private void checkError(JSONObject response) throws AuthenticationException {
		if(response.has("error")) {
			String error = (String) response.get("error");
			String errorMessage = (String) response.get("errorMessage");
			if(response.has("cause")) {
				String errorCause = (String) response.get("cause");
				throw new YggdrasilAuthenticationException(error, errorMessage, errorCause);
			} else
				throw new YggdrasilAuthenticationException(error, errorMessage);
		}
	}

	private YggdrasilSession parse(JSONObject response, String password) {
		String accessTokenValue = response.getString("accessToken");
		String clientTokenValue = response.getString("clientToken");
		JSONArray availableProfilesValue = response.getJSONArray("availableProfiles");
		JSONObject selectedProfileValue = response.getJSONObject("selectedProfile");

		BigInteger accessToken = new BigInteger(accessTokenValue, 16);
		UUID clientToken = clientTokenValue != null ? UUID.fromString(clientTokenValue.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5")) : null;
		List<Profile> availableProfiles = new ArrayList<>();
		if(availableProfilesValue != null) {
			for(int i = 0; i < availableProfilesValue.length(); i++) {
				JSONObject object = availableProfilesValue.getJSONObject(i);
				String id = object.getString("id");
				String name = object.getString("name");
				if(id != null && name != null)
					availableProfiles.add(new Profile(id, name));
			}
		}
		String profileId = (String) selectedProfileValue.get("id");
		String profileName = (String) selectedProfileValue.get("name");
		if(profileId == null || profileName == null)
			throw new NullPointerException("No selected profile id or name passed");
		Profile selectedProfile = new Profile(profileId, profileName);

		return new YggdrasilSession(selectedProfile.getName(), password, accessToken, clientToken, selectedProfile, availableProfiles.toArray(new Profile[availableProfiles.size()]));
	}

	@Override
	public YggdrasilSession validateSession(Session session) throws InvalidSessionException {
		if(!(session instanceof YggdrasilSession))
			throw new InvalidSessionException("Wrong type of session");
		if(session.getUsername() == null || session.getUsername().trim().isEmpty())
			throw new InvalidSessionException("Invalid session username");
		return (YggdrasilSession) session;
	}

}
