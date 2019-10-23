
import java.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UninstallApp
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
	String deviceName = "device 1"; // name of the device
	String appName = "AstroContacts"; // name of the application which you want to uninstall

	String DeviceID = GetDeviceID(deviceName);
	if (DeviceID != null)
	{
	    String status = UninstallApplication(DeviceID, appName);
	    System.out.print(status);
	}
	else
	{
	    System.out.print("Device not found!");
	}
    }

    // method to uninstall application
    private static String UninstallApplication(String deviceID, String appName) throws Exception
    {
	// Create job specific PayLoad
	JSONObject PayLoad = new JSONObject();
	PayLoad.put("AppIds", new JSONArray("[" + GetAppID(deviceID, appName) + "]"));
	// convert payload to base64 string
	String PayLoadBase64 = Base64.getEncoder().encodeToString(PayLoad.toString().getBytes());

	// Request payload for uninstalling the app
	JSONObject RequestPayLoad = new JSONObject();
	RequestPayLoad.put("JobType", "UNINSTALL_APPLICATION");
	RequestPayLoad.put("DeviceID", deviceID);
	RequestPayLoad.put("PayLoad", PayLoadBase64);
	MediaType mediaType = MediaType.parse("application/json");
	RequestBody body = RequestBody.create(mediaType, RequestPayLoad.toString());

	// API URL
	String URL = Constants.baseURL + "/dynamicjob";
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
	return response.body().string();
    }

    // Method to get application ID
    private static String GetAppID(String deviceID, String appName) throws Exception
    {
	// API URL
	String URL = Constants.baseURL + "/installedapp/android/" + deviceID + "/device";
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
		    if (jo.get("Name").equals(appName))
		    {
			return jo.get("Id").toString();
		    }
		}
	    }
	}
	return null;
    }

    // method to get device ID
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
