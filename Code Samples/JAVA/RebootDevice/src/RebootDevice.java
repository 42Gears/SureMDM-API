// Description: Rebooting device using SureMDM apis.

import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RebootDevice
{
    public class Constants
    {
	static final String baseURL = "https://suremdm.42gears.com";
	static final String username = "username";
	static final String password = "Password";
	static final String apikey = "Your ApiKey";
    }

    public static void main(String[] args) throws Exception
    {
	String deviceName = "device 1"; // Name of the device on which job will be applied
	String DeviceID = GetDeviceID(deviceName);
	if (DeviceID != null)
	{
	    String status = Reboot(DeviceID);
	    System.out.print(status);
	}
	else
	{
	    System.out.print("Device not found!");
	}
    }

    // method to apply reboot dynamic job
    private static String Reboot(String deviceID) throws Exception
    {
	// API URL
	String URL = Constants.baseURL + "/dynamicjob";
	// PayLoad data
	JSONObject PayLoad = new JSONObject();
	PayLoad.put("DeviceID", deviceID);
	PayLoad.put("JobType", "Reboot");
	// request body
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

	return response.body().string();
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