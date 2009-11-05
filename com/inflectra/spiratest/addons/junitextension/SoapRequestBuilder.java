package com.inflectra.spiratest.addons.junitextension;

import java.net.*;
import java.util.*;
import java.io.*;



/**
 * This forms the SOAP packet to communicate with the SpiraTest web service
 * 
 * @author		David S Hobbs (http://www.codeproject.com/soap/WSfromJava.asp)
 * @version		2.3.0
 * Modified by Inflectra Corporation to avoid need to hard-code content-length
 */
class SoapRequestBuilder
{
  public String Server = "";
  public String WebServicePath = "";
  public String SoapAction = "";
  public String MethodName = "";
  public String XmlNamespace = "";
  private Vector<String> ParamNames = new Vector<String>();
  private Vector<String> ParamData = new Vector<String>();

	// 

	/**
	 * Escapes XML reserved characters
	 * (assumes UTF-8 or UTF-16 as encoding)
	 * 
	 * @param content	The content to be escaped
	 * @return			The escaped string
	 */
	protected String escape(String content)
	{
		StringBuffer buffer = new StringBuffer();
		for(int i = 0;i < content.length();i++)
		{
			char c = content.charAt(i);
			if(c == '<')
			{
				buffer.append(";&lt;");
			}
			else if(c == '>')
			{
				buffer.append(";&gt;");
			}
			else if(c == '&')
			{
				buffer.append(";&amp;");
			}
			else if(c == '"')
			{
				buffer.append(";&quot;");
			}
			else if(c == '\'')
			{
				buffer.append(";&apos;");
			}
			else if (c == 0x00 || c == 0x01 || c == 0x02 || c == 0x03 || c == 0x04
				 || c == 0x05 || c == 0x06 || c == 0x07 || c == 0x08 || c == 0x0B
				 || c == 0x0C || c == 0x0E || c == 0x0F || c == 0x10 || c == 0x11
				 || c == 0x12 || c == 0x13 || c == 0x14 || c == 0x15 || c == 0x16
				 || c == 0x17 || c == 0x18 || c == 0x19 || c == 0x1A || c == 0x1B
				 || c == 0x1C || c == 0x1D || c == 0x1E || c == 0x1F)
			{
				//ignore such control characters
			}
			else
			{
				buffer.append(c);
			}
		}
		return buffer.toString();
	}

  /**
   * Adds a parameter to the SOAP Request
   * 
   * @param Name	the name of the parameter
   * @param Data	the value of the parameter
   */
  public void AddParameter(String Name, String Data)
  {
    ParamNames.addElement (Name);
    ParamData.addElement (escape (Data));
  }

  /**
   * Sends the SOAP Request to the web service
   * 
   * @return	The value (if any) returned from the web service
   */
  public String sendRequest()
  {
    String retval = "";
    Socket socket = null;
    try
	{
      socket = new Socket(Server, 80);
    }
    catch (Exception ex1)
	{
      return ("Error: "+ex1.getMessage());
    }

    try
	{
      OutputStream os = socket.getOutputStream();
      boolean autoflush = true;
	  //The following is for debugging only
      //PrintWriter out = new PrintWriter(System.out, autoflush);	
      PrintWriter out = new PrintWriter(socket.getOutputStream(), autoflush);
      	
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));   

	  //Create the SOAP message payload
	  StringBuffer body = new StringBuffer();
      body.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
      body.append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n");
      body.append("<soap:Body>\n");
      body.append("<" + MethodName + " xmlns=\"" + XmlNamespace + "\">\n");
	  
      //Parameters passed to the method are added here
      for (int t = 0; t < ParamNames.size(); t++)
	  {
        String name = (String) ParamNames.elementAt(t);
        String data = (String) ParamData.elementAt(t);
        if (data.equals(""))
        {
			body.append("<" + name + "/>\n");
        }
        else
        {
			body.append("<" + name + ">" + data + "</" + name + ">\n");
		}
      }
      body.append("</" + MethodName + ">\n");
      body.append("</soap:Body>\n");
      body.append("</soap:Envelope>\n");
      body.append("\n");
      
      int length = body.length();

      // send an HTTP request to the web service
      out.print("POST " + WebServicePath + " HTTP/1.1 \n");
	  out.print("Host: " + this.Server + " \n");
      out.print("Content-Type: text/xml; charset=utf-8 \n");
      out.print("Content-Length: " + String.valueOf(length)+ " \n");
      out.print("SOAPAction: \"" + SoapAction + "\"\n");
	  out.print("Connection: Close \n");
	  out.print("\n");
	  out.print(body.toString());
      out.flush();
      
      // Read the response from the server ... times out if the response takes
      // more than 3 seconds
      String inputLine;
      StringBuffer sb = new StringBuffer(1000);

      int wait_seconds = 3;  // change if you find that your server is too slow
      boolean timeout = false;
      long m = System.currentTimeMillis();
      while ( (inputLine = in.readLine()) != null && !timeout)
	  {
        sb.append(inputLine + "\n");
        if ( (System.currentTimeMillis() - m) > (1000 * wait_seconds))
			timeout = true;
      }
      in.close();

      // The StringBuffer sb now contains the complete result from the
      // webservice in XML format.  You can parse this XML if you want to
      // get more complicated results than a single value.

      if (!timeout)
	  {
		//Locate the return parameter
        String returnparam = MethodName + "Result";
        int start = sb.toString().indexOf("<" + returnparam + ">") + returnparam.length() + 2;
        int end = sb.toString().indexOf("</" + returnparam + ">");
		
		//If we have no return value, display the error message
		if (end == -1)
		{
			//Check to see if we have a mismatching namespace
			if (sb.toString().indexOf ("Server did not recognize the value of HTTP Header SOAPAction") != -1)
			{
				//Print out a friendly error
				System.out.println ("The version of the web service being connected to does not match\n");
			}
			else
			{
				//Print out the error message for other errors
				System.out.println(sb.toString());
			}
			retval = "";
		}
		else
		{
			//Extract a single return parameter
			retval = sb.toString().substring(start, end);
		}
      }
      else
	  {
        retval="Error: response timed out.";
      }

      socket.close();
    }
    catch (Exception ex)
	{
      return ("Error: cannot communicate (" + ex + ")");
    }

    return retval;
  }
}
