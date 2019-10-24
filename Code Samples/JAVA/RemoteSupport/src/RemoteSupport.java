
import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RemoteSupport
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
		String deviceName = "testing devices"; // name of the device 
		//Generate remote support URL for device
		String RemoteURL = GetRemoteSupportURL(GetDeviceID(deviceName));
		System.out.println(RemoteURL);
	}

	// get URL for remote support of the device
	private static String GetRemoteSupportURL(String deviceName) throws Exception
	{
		String DeviceID = GetDeviceID(deviceName);
		// API URL
		String URL = Constants.baseURL + "/device/" + DeviceID;
		// Create request
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(URL).get()
				// Basic authentication header
				.addHeader("Authorization", Credentials.basic(Constants.username, Constants.password))
				// ApiKey Header
				.addHeader("ApiKey", Constants.apikey)
				// Set content type
				.addHeader("Content-Type", "application/json")

				.build();
		// Execute request
		Response response = client.newCall(request).execute();
		// Extracting DeviceID
		String data = response.body().string();
		if (response.isSuccessful())
		{
			if (data != "[]")
			{
				JSONArray ja = new JSONArray(data);
				for (int i = 0; i < ja.length(); i++)
				{
					JSONObject jo = ja.getJSONObject(i);
					if (jo.get("DeviceName").equals(deviceName))
					{
						String remoteSupporturl = "https://suremdm.42gears.com" + "/RemoteSupport.aspx?" + "id="
								+ jo.get("DeviceID").toString() + "&name=" + jo.get("DeviceName").toString()
								+ "&userid=" + jo.get("UserID").toString() + "&pltFrmType="
								+ jo.get("PlatformType").toString() + "&agentversion="
								+ jo.get("AgentVersion").toString() + "&perm=126,127,128,129";

						return remoteSupporturl;
					}
				}
			}
		}
		return null;
	}

	// get device ID using device name
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
