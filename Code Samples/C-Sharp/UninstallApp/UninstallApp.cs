// Description: Uninstall application from device

using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using RestSharp;
using RestSharp.Authenticators;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;

namespace UninstallApp
{
    static class Constants
    {
        public const string BaseURL = "https://suremdm.42gears.com/api";  // Your SureMDM domain
        public const string Username = "Username"; // Your SureMDM username
        public const string Password = "Password"; // Your SureMDM password
        public const string ApiKey = "Your ApiKey"; // Your SureMDM apikey
    }
    class UninstallApp
    {
        static void Main(string[] args)
        {
            string deviceName = "device 1"; // name of the device
            string appName = "AstroContacts"; // name of the application which you want to uninstall
            // Retrieve Device ID
            string DeviceID = GetDeviceID(deviceName);
            if (DeviceID != null)
            {
                // Uninstall app
                string status = UninstallApplication(DeviceID, appName);
                Console.WriteLine(status);
            }
            else
            {
                Console.WriteLine("Device not found!");
            }
            Console.ReadKey();
        }

        // method to uninstall application
        private static string UninstallApplication(string deviceID, string appName)
        {
            string URL = Constants.BaseURL + "/dynamicjob";
            var client = new RestClient(URL);
            // Basic authentication header
            client.Authenticator = new HttpBasicAuthenticator(Constants.Username, Constants.Password);
            // ApiKey Header
            client.AddDefaultHeader("ApiKey", Constants.ApiKey);
            // Set content type
            client.AddDefaultHeader("Content-Type", "application/json");
            // Set request method
            var request = new RestRequest(Method.POST);
            // Create job specific PayLoad
            var PayLoad = new
            {
                AppIds = new string[] { GetAppID(deviceID, appName) }
            };
            // convert payload to base64 string
            var PayLoadbytes = System.Text.Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(PayLoad));
            var PayLoadBase64 = System.Convert.ToBase64String(PayLoadbytes);
            // Request payload for uninstalling the app
            var RequestPayLoad = new
            {
                JobType = "UNINSTALL_APPLICATION",
                DeviceID = deviceID,
                PayLoad = PayLoadBase64
            };
            // Add request body
            request.AddJsonBody(RequestPayLoad);
            // Execute request
            IRestResponse response = client.Execute(request);

            return response.Content.ToString();
        }

        // Method to get application ID
        private static string GetAppID(string deviceID, string appName)
        {
            string URL = Constants.BaseURL + "/installedapp/android/" + deviceID + "/device";
            var client = new RestClient(URL);
            // Basic authentication header
            client.Authenticator = new HttpBasicAuthenticator(Constants.Username, Constants.Password);
            // ApiKey Header
            client.AddDefaultHeader("ApiKey", Constants.ApiKey);
            // Set content type
            client.AddDefaultHeader("Content-Type", "application/json");
            // Set request method
            var request = new RestRequest(Method.GET);
            // Execute request
            IRestResponse response = client.Execute(request);
            if (response.StatusCode == HttpStatusCode.OK)
            {
                var OutPut = JsonConvert.DeserializeObject(response.Content);
                List<JObject> jsonResponse = JsonConvert.DeserializeObject<List<JObject>>(response.Content);
                var a = jsonResponse.Any();
                if ((jsonResponse != null) && (jsonResponse.Any()))
                {
                    foreach (var app in jsonResponse)
                    {
                        if (app.GetValue("Name").ToString().ToUpper() == appName.ToUpper())
                        {
                            return app.GetValue("Id").ToString();
                        }
                    }
                }
            }
            return null;
        }

        // method to get device ID
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
