package il.cshaifa.hmo_system.entities;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class HMOUtilities {

  /**
   * @param password Unencoded string, user password
   * @param salt Random string use as a salt for SHA-512 encoding
   * @return encoded password
   */
  public static String encodePassword(String password, String salt)
      throws NoSuchAlgorithmException {
    /* Encode password+salt and return */
    MessageDigest md = MessageDigest.getInstance("SHA-512");
    byte[] messageDigest = md.digest((password + salt).getBytes(StandardCharsets.UTF_8));
    BigInteger no = new BigInteger(1, messageDigest);
    StringBuilder hashtext = new StringBuilder(no.toString(16));
    while (hashtext.length() < 32) {
      hashtext.insert(0, "0");
    }
    return hashtext.toString();
  }

  /** @return generated random salt */
  public static String generateSalt() {
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16];
    random.nextBytes(salt);
    return new String(salt);
  }
}
