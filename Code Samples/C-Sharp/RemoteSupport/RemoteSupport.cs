using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using RestSharp;
using RestSharp.Authenticators;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;

namespace RemoteSupport
{
    static class Constants
    {
        public const string BaseURL = "https://suremdm.42gears.com/api";  // Your SureMDM domain
        public const string Username = "Username"; // Your SureMDM username
        public const string Password = "Password"; // Your SureMDM password
        public const string ApiKey = "Your ApiKey"; // Your SureMDM apikey
    }
    class RemoteSupport
    {
        static void Main(string[] args)
        {
            string deviceName = "device 1"; // name of the device
            // Generate remote support URL for device
            string URL = GetRemoteSupportURL(deviceName);
            Console.WriteLine(URL);
            Console.ReadKey();
        }
        // get URL for remote support of the device
        private static string GetRemoteSupportURL(string deviceName)
        {
            // Retrieving ID of the device
            string DeviceID = GetDeviceID(deviceName);
            string URL = Constants.BaseURL + "/device/" + DeviceID;
            var client = new RestClient(URL);
            // Basic authentication header
            client.Authenticator = new HttpBasicAuthenticator(Constants.Username, Constants.Password);
            // ApiKey Header
            client.AddDefaultHeader("ApiKey", Constants.ApiKey);
            // Set content type
            client.AddDefaultHeader("Content-Type", "application/json");
            // Set request method
            var request = new RestRequest(Method.GET);
            // Getting response
            IRestResponse response = client.Execute(request);
            if (response.StatusCode == HttpStatusCode.OK)
            {
                var OutPut = JsonConvert.DeserializeObject(response.Content);
                List<JObject> jsonResponse = JsonConvert.DeserializeObject<List<JObject>>(response.Content);
                var a = jsonResponse.Any();
                if ((jsonResponse != null) && (jsonResponse.Any()))
                {
                    foreach (var device in jsonResponse)
                    {
                        if (device.GetValue("DeviceName").ToString() == deviceName)
                        {
                            string remoteSupporturl = "https://suremdm.42gears.com" + "/RemoteSupport.aspx?" +
                                "id=" + device.GetValue("DeviceID").ToString() +
                                "&name=" + device.GetValue("DeviceName").ToString() +
                                "&userid=" + device.GetValue("UserID").ToString() +
                                "&pltFrmType=" + device.GetValue("PlatformType").ToString() +
                                "&agentversion=" + device.GetValue("AgentVersion").ToString() +
                                "&perm=126,127,128,129";

                            return remoteSupporturl;
                        }
                    }
                }
            }
            return null;
        }
        // get device ID using device name
        private static string GetDeviceID(string deviceName)
        {
            string URL = Constants.BaseURL + "/device";
            var client = new RestClient(URL);
            //  Basic authentication header
            client.Authenticator = new HttpBasicAuthenticator(Constants.Username, Constants.Password);
            //  ApiKey Header
            client.AddDefaultHeader("ApiKey", Constants.ApiKey);
            //  Set content type
            client.AddDefaultHeader("Content-Type", "application/json");
            //  Set request method
            var request = new RestRequest(Method.POST);
            //  Request payload
            var RequestPayLoad = new
            {
                ID = "AllDevices",
                IsSearch = true,
                Limit = 10,
                SearchColumns = new string[] { "DeviceName" },
                SearchValue = deviceName,
                SortColumn = "LastTimeStamp",
                SortOrder = "asc"
            };
            request.AddJsonBody(RequestPayLoad);

            //  Execute request
            IRestResponse response = client.Execute(request);

            if (response.StatusCode == HttpStatusCode.OK)
            {
                var OutPut = JsonConvert.DeserializeObject<JObject>(response.Content);
                foreach (var device in OutPut["rows"])
                {
                    if ((string)device["DeviceName"] == deviceName)
                    {
                        return device["DeviceID"].ToString();
                    }
                }
            }
            return null;
        }
    }
}
