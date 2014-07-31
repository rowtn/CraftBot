package in.parapengu.craftbot.auth;

public interface AuthService<T extends Session> {

	public T login(String username, String password) throws Exception;

	public void authenticate(T session, String serverId) throws Exception;

	public T validateSession(Session session) throws InvalidSessionException;

}
