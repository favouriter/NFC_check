package com.yjkj.nfcscandatasave.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class AppUtil
{
  public static String ByteArrayToHexString(byte[] paramArrayOfByte)
  {
    String[] arrayOfString = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
    String str1 = "";
    for (int i = 0; ; i++)
    {
      if (i >= paramArrayOfByte.length)
      {
        if (str1.endsWith(":"))
          str1 = str1.substring(0, -1 + str1.length());
        return str1;
      }
      int j = 0xFF & paramArrayOfByte[i];
      int k = 0xF & j >> 4;
      String str2 = str1 + arrayOfString[k];
      int m = j & 0xF;
      str1 = new StringBuilder(String.valueOf(str2)).append(arrayOfString[m]).toString() + ":";
    }
  }

  public static String getNFCUid(byte[] paramArrayOfByte)
  {
    return Arrays.toString(paramArrayOfByte);
  }

  public static String getNowTimeToString(String paramString)
  {
    return new SimpleDateFormat(paramString).format(new Date());
  }

  public static String getTimeToString(long paramLong, String paramString)
  {
    if (paramLong <= 0L)
      return "无";
    return new SimpleDateFormat(paramString).format(new Date(1000L * paramLong));
  }

  public static long getUnixTime()
  {
    return new Date().getTime() / 1000L;
  }

}

/* Location:           E:\Android getapk\jd-gui-0.3.5.windows\classes_dex2jar.jar
 * Qualified Name:     com.yjkj.nfcscandatasave.util.AppUtil
 * JD-Core Version:    0.6.2
 */