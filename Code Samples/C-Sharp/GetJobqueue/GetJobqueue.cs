﻿// Description: Get job queue details of the device using SureMDM apis.

using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using RestSharp;
using RestSharp.Authenticators;
using System;
using System.Net;

namespace GetJobqueue
{
    static class Constants
    {
        public const string BaseURL = "https://suremdm.42gears.com/api";  // Your SureMDM domain
        public const string Username = "Username"; // Your SureMDM username
        public const string Password = "Password"; // Your SureMDM password
        public const string ApiKey = "Your ApiKey"; // Your SureMDM apikey
    }
    class GetJobqueue
    {
        static void Main(string[] args)
        {
            string deviceName = "Device 1";     // Name of the device 
            string DeviceID = GetDeviceID(deviceName);
            if (DeviceID != null)
            {
                string retVal = Jobqueue(DeviceID, true); // true to get all jobs and false to get only pending jobs
                Console.WriteLine(retVal);
            }
            else
            {
                Console.WriteLine("Device not found!");
            }
            Console.ReadKey();
        }

        // Function to retrieve device jobqueue
        private static string Jobqueue(string deviceID, Boolean bShowAll)
        {
            string URL = Constants.BaseURL + "/jobqueue/" + deviceID + "/" + bShowAll;
            var client = new RestClient(URL);
            //  Basic authentication header
            client.Authenticator = new HttpBasicAuthenticator(Constants.Username, Constants.Password);
            //  ApiKey Header
            client.AddDefaultHeader("ApiKey", Constants.ApiKey);
            //  Set content type
            client.AddDefaultHeader("Content-Type", "application/json");
            // Set request method
            var request = new RestRequest(Method.GET);
            // Execute method
            IRestResponse response = client.Execute(request);

            return response.Content.ToString();
        }

        // Function to retrieve ID of the device
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
