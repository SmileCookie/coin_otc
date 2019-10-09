package com.world.util.xml;

public class XmlData{
  private static String xmlHead = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

  private static String xslHead = "<?xml-stylesheet type=\"text/xsl\" href=\"";

  private static String headEnd = "</HEAD><BODY>";

  private static String End = "</BODY></ROOT>";

  private static String htmlHead = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\"><HEAD><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />";

  public static String BuildResponse(String title, String keyword, String des, String body, String xslPath, boolean isXml)
  {
    StringBuilder sb = new StringBuilder();
    if (isXml)
    {
      sb.append(xmlHead);
      if (xslPath.length() > 0)
        sb.append(xslHead + xslPath + "\" ?>");
      sb.append("<ROOT><HEAD>");
    }
    else {
      sb.append(htmlHead);
    }
    sb.append("<title>" + title + "</title>");
    if (!isXml)
    {
      if (keyword.length() > 0)
        sb.append("<meta name=\"keywords\" content=\"" + keyword + "\" />");
      else
        sb.append("<meta name=\"keywords\" content=\"" + title + "\" />");
      if (des.length() > 0)
        sb.append("<meta name=\"description\" content=\"" + des + "\" />");
      else
        sb.append("<meta name=\"description\" content=\"" + title + "\" />");
    }
    sb.append(headEnd);
    sb.append(body);
    sb.append(End);
    return sb.toString();
  }

  public static String BuildXml(String title, String inner, String xslPath)
  {
    return BuildResponse(title, "", "", inner, xslPath, true);
  }

  public static String BuildMessageXml(String des, boolean trueOrFalse, String data)
  {
    return BuildResponse(des, "", "", "<State>" + Boolean.toString(trueOrFalse) + "</State><Des>" + des + "</Des><MainData>" + data + "</MainData>", "", true);
  }
 
}