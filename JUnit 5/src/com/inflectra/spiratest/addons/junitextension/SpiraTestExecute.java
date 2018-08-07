package com.inflectra.spiratest.addons.junitextension;

import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.*;
import java.net.URL;

/**
 * This defines the 'SpiraTestExecute' class that provides the Java facade for
 * calling the SOAP web service exposed by SpiraTest
 *
 * @author Inflectra Corporation
 * @version 4.0.0
 */
public class SpiraTestExecute {
    private static final String WEB_SERVICE_SUFFIX = "/Services/v3_0/ImportExport.svc?WSDL";
    /**
     * The URL appended to the base URL to access REST. Note that it ends with a slash
     */
    private static final String REST_SERVICE_URL = "/services/v5_0/RestService.svc/";

    public String url;
    public String userName;
    public String token;
    public int projectId;

    SpiraTestExecute(String url, String userName, String token, int projectId) {
        this.userName = userName;
        this.token = token;
        this.url = url;
        this.projectId = projectId;

    }

    /**
     * Performs an HTTP POST request ot the specified URL
     *
     * @param input The URL to perform the query on
     * @param body  The request body to be sent
     * @return An InputStream containing the JSON returned from the POST request
     * @throws IOException
     */
    public static void httpPost(String input, String body) throws IOException {
        URL url = new URL(input);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //allow sending a request body
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        //have the connection send and retrieve JSON
        connection.setRequestProperty("accept", "application/json; charset=utf-8");
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.connect();
        //used to send data in the REST request
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        //write the body to the stream
        outputStream.writeBytes(body);
        //send the OutputStream to the server
        outputStream.flush();
        outputStream.close();
        connection.getInputStream();
    }


    /**
     * Records a test run
     *
     * @param testCaseId        The test case being executed
     * @param releaseId         The release being executed against (optional)
     * @param testSetId         The test set being executed against (optional)
     * @param executionStatusId The status of the test run (pass/fail/not run)
     * @param runnerName        The name of the automated testing tool
     * @param runnerTestName    The name of the test as stored in JUnit
     * @param runnerAssertCount The number of assertions
     * @param runnerMessage     The failure message (if appropriate)
     * @param runnerStackTrace  The error stack trace (if any)
     * @param endDate           When the test run ended
     * @param startDate         When the test run started
     * @return ID of the new test run
     */
    public void recordTestRun(int testCaseId, Integer releaseId, Integer testSetId, Date startDate,
                             Date endDate, int executionStatusId, String runnerName, String runnerTestName, int runnerAssertCount,
                             String runnerMessage, String runnerStackTrace) {
        String url = this.url + REST_SERVICE_URL + "projects/" + this.projectId + "/test-runs/record?username=" + this.userName + "&api-key=" + this.token;

        Gson gson = new Gson();


        //create the body of the request
        String body = "{\"TestRunFormatId\": 1, \"RunnerName\": \"" + runnerName;
        body += "\", \"RunnerTestName\": \"" + runnerTestName + "\",";
        body += "\"RunnerStackTrace\": " + gson.toJson(runnerStackTrace) + ",";
        body += "\"StartDate\": \"" + formatDate(startDate) + "\", " + "\"EndDate\": \"" + formatDate(endDate) + "\",";
        body += "\"ExecutionStatusId\": " + executionStatusId + ",\"RunnerAssertCount\": " + runnerAssertCount;
        body += ",\"RunnerMessage\": \"" + runnerMessage + "\",";
        body += "\"TestCaseId\": " + testCaseId;

        if(releaseId != null) {
            body += ", \"ReleaseId\": " + releaseId;
        }
        if(testSetId != null) {
            body += ", \"TestSetId\": " + testSetId;
        }

        body += "}";



        //send the request
        try {
            httpPost(url, body);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Turn the date into the format readable by Spira
     * @param d
     * @return
     */
    private static String formatDate(Date d) {
        return "/Date(" + d.getTime() + "-0000)/";
    }

    /**
     * Send a test run to Spira from the info in the given test run
     *
     * @return ID of the new test run
     */
    public void recordTestRun(TestRun testRun) {
        Date now = new Date();
        recordTestRun(testRun.testCaseId, testRun.releaseId == -1 ? null : testRun.releaseId,
                testRun.testSetId == -1 ? null : testRun.testSetId, now, now, testRun.executionStatusId,
                "JUnit", testRun.testName, 1, testRun.message, testRun.stackTrace);
    }

}