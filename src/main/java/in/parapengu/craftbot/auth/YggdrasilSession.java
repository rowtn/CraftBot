package in.parapengu.craftbot.auth;

import java.math.BigInteger;
import java.util.UUID;

public class YggdrasilSession extends Session {
	private final BigInteger accessToken;
	private final UUID clientToken;
	private final Profile[] availableProfiles;
	private final Profile selectedProfile;

	public YggdrasilSession(String username, String password, BigInteger accessToken, UUID clientToken, Profile selectedProfile, Profile... availableProfiles) {
		super(username, password);

		this.accessToken = accessToken;
		this.clientToken = clientToken;
		this.availableProfiles = availableProfiles.clone();
		this.selectedProfile = selectedProfile;
	}

	public BigInteger getAccessToken() {
		return accessToken;
	}

	public UUID getClientToken() {
		return clientToken;
	}

	public Profile[] getAvailableProfiles() {
		return availableProfiles.clone();
	}

	public Profile getSelectedProfile() {
		return selectedProfile;
	}

	@Override
	public boolean isValidForAuthentication() {
		return accessToken != null && selectedProfile != null;
	}

}
