// Description: Code for generating QR code to enroll device in particular group on SureMDM account.

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetQRCode
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
	String groupName = "testing devices"; // name of the device where you want enroll device

	// Get base64 QRcode
	String base64String = GetQRCodeImage(groupName).replace("\"", "");

	if (base64String != null)
	{
	    // Store QR code image in file
	    byte[] imageFile = Base64.getDecoder().decode(base64String);
	    try (OutputStream stream = new FileOutputStream("QRCode.png"))
	    {
		stream.write(imageFile);
	    }
	}
	else
	{
	    System.out.println("Invalid request!");
	}
    }

    // methos to get QRCode
    private static String GetQRCodeImage(String groupName) throws Exception
    {
	// API URL
	String URL = Constants.baseURL + "/QRCode/" + GetGroupID(groupName) + "/default/true/UseSystemGenerated";
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
	if (response.isSuccessful())
	{
	    return response.body().string();
	}
	return null;
    }

    // method to get group ID
    private static String GetGroupID(String groupName) throws Exception
    {
	// For home group no need to get groupID
	if (groupName.equalsIgnoreCase("Home"))
	{
	    return groupName;
	}

	// API URL
	String URL = Constants.baseURL + "/api/group/1/getall";
	// Create request
	OkHttpClient client = new OkHttpClient();
	Request request = new Request.Builder().url(URL).get()
		// Basic authentication header
		.addHeader("Authorization", Credentials.basic(Constants.username, Constants.password))
		// apikey Header
		.addHeader("apikey", Constants.apikey)
		// Set content type
		.addHeader("Content-Type", "application/json").build();
	// Execute request
	Response response = client.newCall(request).execute();
	// Extracting GroupID
	if (response.isSuccessful())
	{
	    String data = response.body().string();
	    JSONObject jsonObj = new JSONObject(data);
	    JSONArray groups = jsonObj.getJSONArray("Groups");
	    for (int index = 0; index < groups.length(); index++)
	    {
		JSONObject group = groups.getJSONObject(index);
		if (group.get("GroupName").equals(groupName))
		{
		    return group.get("GroupID").toString();
		}
	    }
	}
	return null;
    }
}
