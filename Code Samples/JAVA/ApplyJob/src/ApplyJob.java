// Description: Program for applying job on the device using SureMDM apis.
import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApplyJob
{

    public class Constants
    {
	static final String baseURL = "https://suremdm42.42gears.com";
	static final String username = "sa";
	static final String password = "sa";
	static final String apikey = "DA52E108-410A-45EE-92F4-4776D4D521A6";
    }

    public static void main(String[] args) throws Exception
    {
	String deviceName = "rajkot_elite"; // Name of the device on which job will be applied
	String jobName = "salmantest"; // Name of the job which will be applied
	String folderName = ""; // Name of the jobfolder where the job is stored
	// Note: Keep FolderName empty if job is stored in root folder

	// Retrieve ID of the device using name of device
	String deviceID = GetDeviceID(deviceName);
	if (deviceID != null)
	{
	    String jobID = GetJobID(jobName, folderName);
	    if (jobID != null)
	    {
		Applyjob(deviceID, jobID);
	    }
	    else
	    {
		System.out.println("Job not found!");
	    }
	}
	else
	{
	    System.out.print("Device not found!");
	}
    }

    private static void Applyjob(String deviceID, String jobID) throws Exception
    {
	// API URL
	String URL = Constants.baseURL + "/api/jobassignment";

	// Request body
	JSONObject PayLoad = new JSONObject();
	PayLoad.put("DeviceIds", new JSONArray("[" + deviceID + "]"));
	PayLoad.put("JobId", jobID);
	MediaType mediaType = MediaType.parse("application/json");
	RequestBody body = RequestBody.create(mediaType, PayLoad.toString());

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
	String data = response.body().toString();
	if (response.isSuccessful())
	{
	    if (data.isEmpty())
	    {
		System.out.println("Job applied successfully!");
	    }
	    else
	    {
		System.out.println("Failed to apply job!");
	    }
	}
	else
	{
	    System.out.println(data);
	}
	//System.out.print(response.body().toString());
    }

    private static String GetJobID(String jobName, String folderName) throws Exception
    {
	String folderID = folderName.trim().equalsIgnoreCase("") ? "null" : GetFolderID(folderName);

	// API URL
	String URL = Constants.baseURL + "/api/job";
	// Query parameters
	HttpUrl.Builder httpBuider = HttpUrl.parse(URL).newBuilder();
	httpBuider.addQueryParameter("FolderID", folderID);
	URL = httpBuider.build().toString();
	// Create request
	OkHttpClient client = new OkHttpClient();
	Request request = new Request.Builder().url(URL).get()
		// Basic authentication header
		.addHeader("Authorization", Credentials.basic(Constants.username, Constants.password))
		// ApiKey Header
		.addHeader("ApiKey", Constants.apikey)
		// Set content type
		.addHeader("Content-Type", "application/json").build();
	// Execute request
	Response response = client.newCall(request).execute();
	// Extracting folderid
	if (response.isSuccessful())
	{
	    String data = response.body().string();
	    if (data != "[]")
	    {
		JSONArray jobs = new JSONArray(data);
		for (int i = 0; i < jobs.length(); i++)
		{
		    JSONObject job = jobs.getJSONObject(i);
		    if (job.get("JobName").equals(jobName))
		    {
			return job.get("JobID").toString();
		    }
		}

	    }
	}
	return null;
    }

    private static String GetFolderID(String folderName) throws Exception
    {
	// API URL
	String URL = Constants.baseURL + "/api/jobfolder/all";
	// Create request
	OkHttpClient client = new OkHttpClient();
	Request request = new Request.Builder().url(URL).get()
		// Basic authentication header
		.addHeader("Authorization", Credentials.basic(Constants.username, Constants.password))
		// ApiKey Header
		.addHeader("ApiKey", Constants.apikey)
		// Set content type
		.addHeader("Content-Type", "application/json").build();
	// Execute request
	Response response = client.newCall(request).execute();
	// Extracting folderid
	if (response.isSuccessful())
	{
	    String data = response.body().string();
	    if (data != "[]")
	    {
		JSONArray ja = new JSONArray(data);
		for (int i = 0; i < ja.length(); i++)
		{
		    JSONObject jo = ja.getJSONObject(i);
		    if (jo.get("FolderName").equals(folderName))
		    {
			return jo.get("FolderID").toString();
		    }
		}

	    }
	}
	return "null";
    }

    private static String GetDeviceID(String deviceName) throws Exception
    {
	// API URL
	String URL = Constants.baseURL + "/api/device";

	// Request body
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
	if (response.isSuccessful())
	{
	    String data = response.body().string();
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
