// Description: Re-apply all pending jobs from device jobqueue

import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReapplyJob
{
    public class Constants
    {
	static final String baseURL = "https://suremdm.42gears.com/api"; // Your SureMDM domain
	static final String username = "username"; // Your SureMDM username
	static final String password = "Password"; // Your SureMDM password
	static final String apikey = "Your ApiKey"; // Your SureMDM apikey
    }

    public static void main(String[] args) throws Exception
    {
	String deviceName = "device 1"; // Name of the device
	String DeviceID = GetDeviceID(deviceName);
	if (DeviceID != null)
	{
	    String status = Jobqueue(DeviceID, false);
	    System.out.print(status);
	}
	else
	{
	    System.out.print("Device not found!");
	}
    }

    // Function to re-apply all the pending jobs
    private static void ReapplyPendingJobs(String jobID, String deviceID, String rowID) throws Exception
    {
	//SureMDM API URL of your account
	String URL = Constants.baseURL + "/jobqueue/" + jobID + "/" + deviceID + "/" + rowID;
	// Empty body
	RequestBody body = RequestBody.create(null, new byte[] {});
	//Create request
	OkHttpClient client = new OkHttpClient();
	Request request = new Request.Builder().url(URL)
		//Send payload
		.put(body)
		//Basic authentication header
		.addHeader("Authorization", Credentials.basic(Constants.username, Constants.password))
		//ApiKey header
		.addHeader("ApiKey", Constants.apikey)
		//Set content type
		.addHeader("Content-Type", "application/json").build();
	//Send request
	Response response = client.newCall(request).execute();
	System.out.println(response.body().string());
    }

    // Function for retrieving jobqueue of device
    private static String Jobqueue(String deviceID, Boolean bShowAll) throws Exception
    {
	// API URL
	String URL = Constants.baseURL + "/jobqueue/" + deviceID + "/" + bShowAll;
	// Create request
	OkHttpClient client = new OkHttpClient();
	Request request = new Request.Builder().url(URL)
		// Send payload
		.get()
		// Basic authentication header
		.addHeader("Authorization", Credentials.basic(Constants.username, Constants.password))
		// ApiKey Header
		.addHeader("ApiKey", Constants.apikey)
		// Set content type
		.addHeader("Content-Type", "application/json").build();
	// Execute request
	Response response = client.newCall(request).execute();
	String data = response.body().string();
	if (response.isSuccessful())
	{
	    if (data != "[]")
	    {
		JSONArray ja = new JSONArray(data);
		for (int i = 0; i < ja.length(); i++)
		{
		    JSONObject jo = ja.getJSONObject(i);
		    if (jo.get("Status").equals("ERROR") || jo.get("Status").equals("FAILED")
			    || jo.get("Status").equals("SCHEDULED"))
		    {
			String rowID = jo.getString("RowId");
			String jobID = jo.getString("JobID");
			ReapplyPendingJobs(jobID, deviceID, rowID);
		    }
		}
		return "Success";
	    }
	}
	return "Failed";
    }

    // Function to retrieve ID of the device
    private static String GetDeviceID(String deviceName) throws Exception
    {
	// request body
	JSONObject PayLoad = new JSONObject();
	PayLoad.put("ID", "AllDevices");
	PayLoad.put("IsSearch", true);
	PayLoad.put("Limit", 20);
	PayLoad.put("SearchColumns", new JSONArray("[\"DeviceName\"]"));
	PayLoad.put("SearchValue", deviceName);
	PayLoad.put("SortColumn", "LastTimeStamp");
	PayLoad.put("SortOrder", "asc");
	MediaType mediaType = MediaType.parse("application/json");
	RequestBody body = RequestBody.create(mediaType, PayLoad.toString());

	// API URL
	String URL = Constants.baseURL + "/device";
	// Create request
	OkHttpClient client = new OkHttpClient();
	Request request = new Request.Builder().url(URL)
		// Send payload
		.post(body)
		// Basic authentication header
		.addHeader("Authorization", Credentials.basic(Constants.username, Constants.password))
		// ApiKey Header
		.addHeader("ApiKey", Constants.apikey)
		// Set content type
		.addHeader("Content-Type", "application/json").build();
	// Execute request
	Response response = client.newCall(request).execute();
	// Extracting DeviceID
	String data = response.body().string();
	if (response.isSuccessful())
	{
	    JSONObject jsonObj = new JSONObject(data);
	    JSONArray devices = jsonObj.getJSONArray("rows");
	    for (int index = 0; index < devices.length(); index++)
	    {
		JSONObject device = devices.getJSONObject(index);
		if (device.get("DeviceName").equals(deviceName))
		{
		    return device.get("DeviceID").toString();
		}
	    }
	}
	return null;
    }
}
