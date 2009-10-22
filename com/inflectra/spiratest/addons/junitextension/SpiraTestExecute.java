package com.inflectra.spiratest.addons.junitextension;

import java.lang.*;
import java.util.*;
import java.text.*;

/**
 * This defines the 'SpiraTestExecute' class that provides the Java facade
 * for calling the SOAP web service exposed by SpiraTest
 * (Current implementation doesn't support SSL)
 * 
 * @author		Inflectra Corporation
 * @version		2.3.0
 *
 */
public class SpiraTestExecute
{
	static final String WEB_SERVICE_NAMESPACE = "http://www.inflectra.com/SpiraTest/Services/v2.2/";
	static final String WEB_SERVICE_URL_SUFFIX = "/Services/v2_2/ImportExport.asmx";
	
	public String url;
	public String userName;
	public String password;
	public int projectId;

	/**
	 * Records a test run
	 * 
		@param testerUserId			The user id of the person who's running the test (-1 for logged in user)
		@param testCaseId			The test case being executed
		@param releaseId			The release being executed against (optional)
		@param testSetId			The test set being executed against (optional)
		@param executionStatusId	The status of the test run (pass/fail/not run)
		@param runnerName			The name of the automated testing tool
		@param runnerTestName		The name of the test as stored in JUnit
		@param runnerAssertCount	The number of assertions
		@param runnerMessage		The failure message (if appropriate)
		@param runnerStackTrace		The error stack trace (if any)
		@param endDate				When the test run ended
		@param startDate			When the test run started
	 */
	public int recordTestRun(int testerUserId, int testCaseId, int releaseId, int testSetId, Date startDate, Date endDate, int executionStatusId, String runnerName, String runnerTestName, int runnerAssertCount, String runnerMessage, String runnerStackTrace) 
	{
		String response = "";
		
		//Break up the URL into server name and the service path
		String fullUrl = this.url + WEB_SERVICE_URL_SUFFIX;

		//Remove the http:// prefix (since we don't support SSL)
		fullUrl = fullUrl.replaceFirst ("http://", "");
		
		//Now extract the server name
		String[] urlComponents = fullUrl.split ("/", 2);
		String serverName = urlComponents [0];
		String webServicePath = urlComponents [1];

		//Format the dates into YYYY-MM-DDTHH:MM:SS format required by SOAP
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String startDateSerialized = dateFormat.format (startDate);
		String endDateSerialized = dateFormat.format (endDate);

		//Instantiate the new SoapRequester class and populate
		SoapRequestBuilder soapRequest = new SoapRequestBuilder();
		soapRequest.Server = serverName;
		soapRequest.MethodName = "TestRun_RecordAutomated2";	//Sessionless version of API method
		soapRequest.XmlNamespace = WEB_SERVICE_NAMESPACE;
		soapRequest.WebServicePath = "/" + webServicePath;
		soapRequest.SoapAction = soapRequest.XmlNamespace + soapRequest.MethodName;
		soapRequest.AddParameter("userName", userName);
		soapRequest.AddParameter("password", password);
		soapRequest.AddParameter("projectId", Integer.toString(projectId));
		soapRequest.AddParameter("testerUserId", Integer.toString(testerUserId));
		soapRequest.AddParameter("testCaseId", Integer.toString(testCaseId));
		if (releaseId != -1)
		{
			soapRequest.AddParameter("releaseId", Integer.toString(releaseId));
		}
		if (testSetId != -1)
		{
			soapRequest.AddParameter("testSetId", Integer.toString(testSetId));
		}
		soapRequest.AddParameter("startDate", startDateSerialized);
		soapRequest.AddParameter("endDate", endDateSerialized);
		soapRequest.AddParameter("executionStatusId", Integer.toString(executionStatusId));
		soapRequest.AddParameter("runnerName", runnerName);
		soapRequest.AddParameter("runnerTestName", runnerTestName);
		soapRequest.AddParameter("runnerAssertCount", Integer.toString(runnerAssertCount));
		soapRequest.AddParameter("runnerMessage", runnerMessage);
		soapRequest.AddParameter("runnerStackTrace", runnerStackTrace);

		//Send the request and capture the response (should be the test run id)
		response = soapRequest.sendRequest();

		int testRunId = -1;
		try
		{
			testRunId = Integer.parseInt (response);
		}
		catch (NumberFormatException e)
		{
			//Display the error
			System.out.print ("Error sending results to SpiraTest (" + response + ")\n\n");
		}
		return testRunId;
	}
}