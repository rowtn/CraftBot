package in.parapengu.craftbot.protocol.v4.login;

import in.parapengu.craftbot.auth.EncryptionUtil;
import in.parapengu.craftbot.protocol.Destination;
import in.parapengu.craftbot.protocol.Packet;
import in.parapengu.craftbot.protocol.PacketException;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;

public class PacketLoginOutEncryptionResponse extends Packet {

	private SecretKey secretKey;
	private PublicKey publicKey;
	private byte[] sharedSecret;
	private byte[] verifyToken;

	public PacketLoginOutEncryptionResponse(SecretKey secretKey, PublicKey publicKey, byte[] verifyToken) {
		super(0x01);
		this.secretKey = secretKey;
		this.publicKey = publicKey;

		try {
			this.sharedSecret = EncryptionUtil.cipher(1, publicKey, secretKey.getEncoded());
			this.verifyToken = EncryptionUtil.cipher(1, publicKey, verifyToken);
		} catch(GeneralSecurityException exception) {
			throw new Error("Unable to cipher", exception);
		}
	}

	@Override
	public void build(PacketInputStream input) throws IOException {
		throw new PacketException("Can not receive an outbound packet", getClass(), Destination.CLIENT);
	}

	@Override
	public void send(PacketOutputStream output) throws IOException {
		output.writeByteArray(sharedSecret);
		output.writeByteArray(verifyToken);
	}

	public SecretKey getSecretKey() {
		return secretKey;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public byte[] getSharedSecret() {
		return sharedSecret;
	}

	public byte[] getVerifyToken() {
		return verifyToken;
	}

}
