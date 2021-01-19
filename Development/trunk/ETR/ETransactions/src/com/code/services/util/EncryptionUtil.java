package com.code.services.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import com.code.exceptions.BusinessException;

public class EncryptionUtil {
    private static EncryptionUtil encUtil = null;
    // -----------------------------------------------------------------------------------------
    private final static String algAESSerectKey = "Zp7Yx26TZigp6kBppwp+Aw==";

    private EncryptionUtil() throws BusinessException {

    }

    public static EncryptionUtil getInstance() throws BusinessException {
	if (encUtil == null)
	    encUtil = new EncryptionUtil();

	return encUtil;
    }

    /**
     * Generate key which contains a pair of private and public key using 1024 bytes.
     */

    // ------------------------------Symmetric Encryption------------------------
    /**
     * Encrypts plainText in AES using the secret key
     * 
     * @param plainText
     * @param secKey
     * @return
     * @throws Exception
     */
    public static String encryptSymmetrically(String plainText) throws Exception {
	Cipher aesCipher = Cipher.getInstance("AES");
	byte[] encodedKey = Base64.getDecoder().decode(algAESSerectKey);
	SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
	aesCipher.init(Cipher.ENCRYPT_MODE, originalKey);
	byte[] byteCipherText = aesCipher.doFinal(plainText.getBytes());
	return DatatypeConverter.printHexBinary(byteCipherText);
    }

    /**
     * Decrypts encrypted byte array using the key used for encryption.
     * 
     * @param byteCipherText
     * @param secKey
     * @return
     * @throws Exception
     */
    public static String decryptSymmetrically(String cipherText) {
	try {
	    Cipher aesCipher = Cipher.getInstance("AES");
	    byte[] encodedKey = Base64.getDecoder().decode(algAESSerectKey);
	    SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
	    aesCipher.init(Cipher.DECRYPT_MODE, originalKey);
	    byte[] bytePlainText = aesCipher.doFinal(DatatypeConverter.parseHexBinary(cipherText));
	    return new String(bytePlainText);
	} catch (Exception e) {
	    return null;
	}
    }
}
