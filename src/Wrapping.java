import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class Wrapping {

    // Wrap the given key with the wrapping key
    public byte[] wrapKey(SecretKey keyToWrap, SecretKey wrappingKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AESWrap");
        cipher.init(Cipher.WRAP_MODE, wrappingKey);
        return cipher.wrap(keyToWrap);
    }

    // Unwrap the given key with the wrapping key
    public SecretKey unwrapKey(byte[] wrappedKey, SecretKey wrappingKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AESWrap");
        cipher.init(Cipher.UNWRAP_MODE, wrappingKey);
        return (SecretKey) cipher.unwrap(wrappedKey, "AES", Cipher.SECRET_KEY);
    }
}
