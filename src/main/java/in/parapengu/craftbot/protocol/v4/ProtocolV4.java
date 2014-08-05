package in.parapengu.craftbot.protocol.v4;

import in.parapengu.craftbot.auth.AuthService;
import in.parapengu.craftbot.auth.EncryptionUtil;
import in.parapengu.craftbot.auth.InvalidSessionException;
import in.parapengu.craftbot.auth.Session;
import in.parapengu.craftbot.bot.CraftBot;
import in.parapengu.craftbot.event.EventHandler;
import in.parapengu.craftbot.event.Listener;
import in.parapengu.craftbot.event.bot.connection.BotConnectServerEvent;
import in.parapengu.craftbot.event.packet.ReceivePacketEvent;
import in.parapengu.craftbot.event.packet.SentPacketEvent;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.Protocol;
import in.parapengu.craftbot.protocol.State;
import in.parapengu.craftbot.protocol.stream.BotPacketStream;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;
import in.parapengu.craftbot.protocol.v4.handshaking.PacketHandshakeOutStatus;
import in.parapengu.craftbot.protocol.v4.login.server.PacketLoginInDisconnect;
import in.parapengu.craftbot.protocol.v4.login.server.PacketLoginInEncryptionRequest;
import in.parapengu.craftbot.protocol.v4.login.server.PacketLoginInSuccess;
import in.parapengu.craftbot.protocol.v4.login.client.PacketLoginOutEncryptionResponse;
import in.parapengu.craftbot.protocol.v4.login.client.PacketLoginOutStart;
import in.parapengu.craftbot.protocol.v4.play.client.PacketPlayOutClientStatus;
import in.parapengu.craftbot.protocol.v4.play.client.PacketPlayOutKeepAlive;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInAnimation;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInBlockAction;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInBlockBreakAnimation;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInBlockChange;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInChangeGameState;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInChatMessage;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInChunkBulk;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInChunkData;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInCollectItem;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInDisconnect;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInEntity;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInEntityAttach;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInEntityDestroy;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInEntityEffect;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInEntityEquipment;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInEntityLook;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInEntityLookRelativeMove;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInEntityMetadata;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInEntityProperties;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInEntityRelativeMove;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInEntityRemoveEffect;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInEntityRotate;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInEntityStatus;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInEntityTeleport;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInEntityVelocity;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInExperience;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInHeldItemChange;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInJoinGame;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInKeepAlive;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInMultipleBlockChange;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInParticle;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInPlayerAbilities;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInPlayerListItem;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInPlayerPosition;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInPluginMessage;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInRespawn;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInSetSlot;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInSpawnExperienceOrb;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInSpawnMob;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInSpawnObject;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInSpawnPainting;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInSpawnPlayer;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInSpawnPosition;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInStatistics;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInTimeUpdate;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInUpdateHealth;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInUpdateTileEntity;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInUseBed;
import in.parapengu.craftbot.protocol.v4.play.server.PacketPlayInWindowItems;
import in.parapengu.craftbot.util.ClassUtils;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ProtocolV4 extends Protocol implements Listener {

	private CraftBot bot;
	private BotPacketStream stream;

	public ProtocolV4() {
		super(4, "1.7.2", "1.7.5");

		packets = new HashMap<>();
		maxIds = new HashMap<>();

		Map<Integer, Class<? extends Packet>> login = new HashMap<>();
		login.put(0x00, PacketLoginInDisconnect.class);
		login.put(0x01, PacketLoginInEncryptionRequest.class);
		login.put(0x02, PacketLoginInSuccess.class);
		packets.put(State.LOGIN, login);
		maxIds.put(State.LOGIN, 0x02);

		Map<Integer, Class<? extends Packet>> play = new HashMap<>();
		play.put(0x00, PacketPlayInKeepAlive.class);
		play.put(0x01, PacketPlayInJoinGame.class);
		play.put(0x02, PacketPlayInChatMessage.class);
		play.put(0x03, PacketPlayInTimeUpdate.class);
		play.put(0x04, PacketPlayInEntityEquipment.class);
		play.put(0x05, PacketPlayInSpawnPosition.class);
		play.put(0x06, PacketPlayInUpdateHealth.class);
		play.put(0x07, PacketPlayInRespawn.class);
		play.put(0x08, PacketPlayInPlayerPosition.class);
		play.put(0x09, PacketPlayInHeldItemChange.class);
		play.put(0x0A, PacketPlayInUseBed.class);
		play.put(0x0B, PacketPlayInAnimation.class);
		play.put(0x0C, PacketPlayInSpawnPlayer.class);
		play.put(0x0D, PacketPlayInCollectItem.class);
		play.put(0x0E, PacketPlayInSpawnObject.class);
		play.put(0x0F, PacketPlayInSpawnMob.class);
		play.put(0x10, PacketPlayInSpawnPainting.class);
		play.put(0x11, PacketPlayInSpawnExperienceOrb.class);
		play.put(0x12, PacketPlayInEntityVelocity.class);
		play.put(0x13, PacketPlayInEntityDestroy.class);
		play.put(0x14, PacketPlayInEntity.class);
		play.put(0x15, PacketPlayInEntityRelativeMove.class);
		play.put(0x16, PacketPlayInEntityLook.class);
		play.put(0x17, PacketPlayInEntityLookRelativeMove.class);
		play.put(0x18, PacketPlayInEntityTeleport.class);
		play.put(0x19, PacketPlayInEntityRotate.class);
		play.put(0x1A, PacketPlayInEntityStatus.class);
		play.put(0x1B, PacketPlayInEntityAttach.class);
		play.put(0x1C, PacketPlayInEntityMetadata.class);
		play.put(0x1D, PacketPlayInEntityEffect.class);
		play.put(0x1E, PacketPlayInEntityRemoveEffect.class);
		play.put(0x1F, PacketPlayInExperience.class);
		play.put(0x20, PacketPlayInEntityProperties.class);
		play.put(0x21, PacketPlayInChunkData.class);
		play.put(0x22, PacketPlayInMultipleBlockChange.class);
		play.put(0x23, PacketPlayInBlockChange.class);
		play.put(0x24, PacketPlayInBlockAction.class);
		play.put(0x25, PacketPlayInBlockBreakAnimation.class);
		play.put(0x26, PacketPlayInChunkBulk.class);
		play.put(0x2A, PacketPlayInParticle.class);
		play.put(0x2B, PacketPlayInChangeGameState.class);
		play.put(0x2F, PacketPlayInSetSlot.class);
		play.put(0x30, PacketPlayInWindowItems.class);
		play.put(0x35, PacketPlayInUpdateTileEntity.class);
		play.put(0x37, PacketPlayInStatistics.class);
		play.put(0x38, PacketPlayInPlayerListItem.class);
		play.put(0x39, PacketPlayInPlayerAbilities.class);
		play.put(0x3F, PacketPlayInPluginMessage.class);
		play.put(0x40, PacketPlayInDisconnect.class);
		packets.put(State.PLAY, play);
		maxIds.put(State.PLAY, 0x40);
	}

	@EventHandler
	public void onConnect(BotConnectServerEvent event) {
		event.getBot().getLogger().info("Testing connection to " + event.getAddress() + ":" + event.getPort());
		event.getBot().getLogger().debug("Loaded Packets: " + packets);

		bot = event.getBot();
		String address = event.getAddress();
		int port = event.getPort();

		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(address, port), 20*1000);
			output = new PacketOutputStream(socket.getOutputStream());
			input = new PacketInputStream(socket.getInputStream());
			bot.setSocket(socket);
			bot.setOutput(output);
			bot.setInput(input);
		} catch(IOException ex) {
			bot.getLogger().log("Could not connect to " + address + (port != 25565 ? ":" + port : "") + ": ", ex);
			return;
		}

		stream = new BotPacketStream(this, socket, output, input, bot);
		new Thread(stream::start).start();

		bot.setState(State.LOGIN);
		stream.sendPacket(new PacketHandshakeOutStatus(this, address, port, State.LOGIN));
		stream.sendPacket(new PacketLoginOutStart(bot.getUsername()));
	}

	@EventHandler
	public void onPacket(ReceivePacketEvent event) {
		if(event.getPacket() instanceof PacketLoginInEncryptionRequest) {
			bot.getLogger().info("Attempting to authenticate!");
			PacketLoginInEncryptionRequest request = (PacketLoginInEncryptionRequest) event.getPacket();
			String serverId = request.getServer().trim();
			PublicKey publicKey;
			try {
				publicKey = EncryptionUtil.generatePublicKey(request.getKey());
			} catch(GeneralSecurityException exception) {
				throw new Error("Unable to generate public key", exception);
			}
			SecretKey secretKey = EncryptionUtil.generateSecretKey();

			if(!serverId.equals("-")) {
				try {
					AuthService service = bot.getAuth();
					Session session = bot.getSession();

					String hash = new BigInteger(EncryptionUtil.encrypt(serverId, publicKey, secretKey)).toString(16);
					service.authenticate(service.validateSession(session), hash);
				} catch(InvalidSessionException exception) {
					stream.getLogger().log("Session invalid: ", exception);
					stream.close();
				} catch(NoSuchAlgorithmException | UnsupportedEncodingException exception) {
					stream.getLogger().log("Unable to hash: ", exception);
					stream.close();
				} catch(Exception exception) {
					stream.getLogger().log("Unable to authenticate: ", exception);
					stream.close();
				}
			}

			stream.sendPacket(new PacketLoginOutEncryptionResponse(secretKey, publicKey, request.getToken()));
		} else if(event.getPacket() instanceof PacketLoginInSuccess) {
			bot.getLogger().info("Logged in successfully!");
			bot.setState(State.PLAY);

			new Thread(() -> {
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						if(stream.getInput() == null) {
							timer.cancel();
							return;
						}

						stream.sendPacket(new PacketPlayOutKeepAlive((int) (System.nanoTime() / 1000000L)));
					}
				}, 100L, 5000L);
			}).start();
		} else if(event.getPacket() instanceof PacketPlayInKeepAlive) {
			stream.sendPacket(new PacketPlayOutKeepAlive(((PacketPlayInKeepAlive) event.getPacket()).getAliveId()));
		} else if(event.getPacket() instanceof PacketPlayInChatMessage) {
			String message = ((PacketPlayInChatMessage) event.getPacket()).getMessage();
			bot.getLogger().info(message);
		} else if(event.getPacket() instanceof PacketPlayInDisconnect) {
			String message = ((PacketPlayInDisconnect) event.getPacket()).getReason();
			bot.getLogger().info("Disconnected: " + message);
			stream.close();
		} else if(event.getPacket() instanceof PacketPlayInUpdateHealth) {
			float fl = ((PacketPlayInUpdateHealth) event.getPacket()).getHealth();
			int health = (int) fl;
			bot.getLogger().info("Health is " + health);
			if(health <= 0) {
				bot.getLogger().info("Attempting to respawn!");
				stream.sendPacket(new PacketPlayOutClientStatus(0));
			}
		} else {
			bot.getLogger().debug(event.getPacket());
		}
	}

	@EventHandler
	public void onPacket(SentPacketEvent event) {
		if(event.getPacket() instanceof PacketLoginOutEncryptionResponse) {
			PacketLoginOutEncryptionResponse packet = (PacketLoginOutEncryptionResponse) event.getPacket();
			stream.encrypt(packet.getSecretKey());
			stream.decrypt(packet.getSecretKey());
		}
	}

	@Override
	public String toString() {
		return ClassUtils.build(getClass(), this, true);
	}

}
