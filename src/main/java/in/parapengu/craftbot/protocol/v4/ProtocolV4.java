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
import in.parapengu.craftbot.protocol.v4.login.PacketLoginInDisconnect;
import in.parapengu.craftbot.protocol.v4.login.PacketLoginInEncryptionRequest;
import in.parapengu.craftbot.protocol.v4.login.PacketLoginInSuccess;
import in.parapengu.craftbot.protocol.v4.login.PacketLoginOutEncryptionResponse;
import in.parapengu.craftbot.protocol.v4.login.PacketLoginOutStart;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInAnimation;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInBlockAction;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInBlockBreakAnimation;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInBlockChange;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInChatMessage;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInChunkBulk;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInChunkData;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInCollectItem;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInDisconnect;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInEntity;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInEntityAttach;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInEntityDestroy;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInEntityEffect;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInEntityEquipment;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInEntityLook;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInEntityLookRelativeMove;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInEntityMetadata;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInEntityProperties;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInEntityRelativeMove;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInEntityRemoveEffect;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInEntityRotate;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInEntityStatus;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInEntityTeleport;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInEntityVelocity;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInExperience;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInHeldItemChange;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInJoinGame;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInKeepAlive;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInMultipleBlockChange;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInPlayerAbilities;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInPlayerListItem;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInPlayerPosition;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInPluginMessage;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInRespawn;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInSetSlot;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInSpawnExperienceOrb;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInSpawnMob;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInSpawnObject;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInSpawnPainting;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInSpawnPlayer;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInSpawnPosition;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInStatistics;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInTimeUpdate;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInUpdateHealth;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInUseBed;
import in.parapengu.craftbot.protocol.v4.play.PacketPlayInWindowItems;
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
		play.put(0x2F, PacketPlayInSetSlot.class);
		play.put(0x30, PacketPlayInWindowItems.class);
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
