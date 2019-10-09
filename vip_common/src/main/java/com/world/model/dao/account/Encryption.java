package com.world.model.dao.account;

import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class Encryption
{
  private static final String PASSWORD_CRYPT_KEY = ":(G^l%g_7ntNDe0BZ%T#<t.Q";
  private static final String DES = "DES";

  public static byte[] encrypt(byte[] src, byte[] key)
    throws Exception
  {
    SecureRandom sr = new SecureRandom();

    DESKeySpec dks = new DESKeySpec(key);

    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

    SecretKey securekey = keyFactory.generateSecret(dks);

    Cipher cipher = Cipher.getInstance("DES");

    cipher.init(1, securekey, sr);

    return cipher.doFinal(src);
  }

  public static byte[] decrypt(byte[] src, byte[] key)
    throws Exception
  {
    SecureRandom sr = new SecureRandom();

    DESKeySpec dks = new DESKeySpec(key);

    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

    SecretKey securekey = keyFactory.generateSecret(dks);

    Cipher cipher = Cipher.getInstance("DES");

    cipher.init(2, securekey, sr);

    return cipher.doFinal(src);
  }

  public static final String decrypt(String data)
  {
    try
    {
      return new String(decrypt(hex2byte(data.getBytes()), 
        ":(G^l%g_7ntNDe0BZ%T#<t.Q".getBytes()));
    }
    catch (Exception localException)
    {
    }

    return null;
  }

  public static final String encrypt(String password)
  {
    try
    {
      return byte2hex(encrypt(password.getBytes(), ":(G^l%g_7ntNDe0BZ%T#<t.Q".getBytes()));
    }
    catch (Exception localException)
    {
    }
    return null;
  }

  public static String byte2hex(byte[] b)
  {
    String hs = "";

    String stmp = "";

    for (int n = 0; n < b.length; n++)
    {
      stmp = Integer.toHexString(b[n] & 0xFF);

      if (stmp.length() == 1)
      {
        hs = hs + "0" + stmp;
      }
      else
      {
        hs = hs + stmp;
      }
    }

    return hs.toUpperCase();
  }

  public static byte[] hex2byte(byte[] b)
  {
    if (b.length % 2 != 0)
    {
      throw new IllegalArgumentException("���Ȳ���ż��");
    }
    byte[] b2 = new byte[b.length / 2];

    for (int n = 0; n < b.length; n += 2)
    {
      String item = new String(b, n, 2);

      b2[(n / 2)] = (byte)Integer.parseInt(item, 16);
    }

    return b2;
  }
}