package com.inflectra.spiratest.addons.junitextension;

import java.net.*;
import java.util.*;
import java.io.*;



/**
 * This forms the SOAP packet to communicate with the SpiraTest web service
 * 
 * @author		David S Hobbs (http://www.codeproject.com/soap/WSfromJava.asp)
 * @version		1.2.0
 *   MODIFIED by Kevin Miller kmiller@raf.com to work on both Windows and Linux.
 *   Original version used PrintWriter.println to write the soap message to the socket.
 *   This had the adverse affect of not working on both Windows and Linux because
 *   println adds a newline which is different in each O/S.  While it was possible to
 *   manually modify sendRequest int length this would mean having two different versions
 *   of code: one which runs on linux and one which runs on windows, which we found untenable.
 *   
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
				buffer.append(";&lt;");
			else if(c == '>')
				buffer.append(";&gt;");
			else if(c == '&')
				buffer.append(";&amp;");
			else if(c == '"')
				buffer.append(";&quot;");
			else if(c == '\'')
				buffer.append(";&apos;");
			else
				buffer.append(c);
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
      
      // Next section rewritten to use PrintWriter.print instead of PrintWriter.println by KM
      //  Note, when attempting to debug for purposes of determining the packet length expected with a new
      // deployment of spiratest: if you get an error instantly, it means your packet length is too small,
      // whereas if you get one after a long wait, it means your packet size is too big (it is waiting to 
      // receive more bytes before it times out...)
      
      // the length value may need to be modified if you find it isn't working on your system
      int length = 275;
      length = length + (MethodName.length() * 2) + XmlNamespace.length() + Server.length();  
          
      for (int t = 0; t < ParamNames.size(); t++)
	  {
        String name = (String) ParamNames.elementAt(t);
        String data = (String) ParamData.elementAt(t);
        length += name.length() + 2;	//Name plus open tags <>
		length += name.length() + 3;	//Name plus close tags </>
		length += data.length();
      }

      // send an HTTP request to the web service
      out.print("POST " + WebServicePath + " HTTP/1.1 \n");
	  out.print("Host: " + this.Server + " \n");
      out.print("Content-Type: text/xml; charset=utf-8 \n");
      out.print("Content-Length: " + String.valueOf(length)+ " \n");
      out.print("SOAPAction: \"" + SoapAction + "\"\n");
	  out.print("Connection: Close \n");  
	  out.print("\n");
      out.print("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
      out.print("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n");
      out.print("<soap:Body>\n");
      out.print("<" + MethodName + " xmlns=\"" + XmlNamespace + "\">\n");
      
      //Parameters passed to the method are added here
      for (int t = 0; t < ParamNames.size(); t++)
	  {
        String name = (String) ParamNames.elementAt(t);
        String data = (String) ParamData.elementAt(t);
        out.print("<" + name + ">" + data + "</" + name + ">\n");
      }
      out.print("</" + MethodName + ">\n");
      out.print("</soap:Body>\n");
      out.print("</soap:Envelope>\n");
      out.print("\n");
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
