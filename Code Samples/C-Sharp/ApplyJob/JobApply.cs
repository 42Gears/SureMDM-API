// Description: Program for applying job on the device using SureMDM apis.

using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using RestSharp;
using RestSharp.Authenticators;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;

namespace ApplyJob
{
    static class Constants
    {
        public const string BaseURL = "https://suremdm.42gears.com";  // Your SureMDM domain
        public const string Username = "Username"; // Your SureMDM username
        public const string Password = "Password"; // Your SureMDM password
        public const string ApiKey = "Your ApiKey"; // Your SureMDM apikey
    }

    class JobApply
    {
        static void Main(string[] args)
        {
            Console.WriteLine("Apply job on the device.");

            string DeviceName = "Device 1";     // Name of the device on which job will be applied
            string JobName = "Test Job";        // Name of the job which will be applied
            string FolderName = "";             // Name of the jobfolder where the job is stored
            // Note: Keep FolderName empty if job is stored in root folder

            // Retrieve ID of the device using name of device
            string DeviceID = GetDeviceID(DeviceName);
            if (DeviceID != null)
            {
                // Retrieve ID of the job using name of the job
                string JobID = GetJobID(JobName, FolderName);
                if (JobID != null)
                {
                    // Apply job on the device
                    ApplyJob(DeviceID, JobID);
                }
                else
                {
                    Console.WriteLine("\nJob not found!");
                }
            }
            else
            {
                Console.WriteLine("\nDevice not found!");
            }
            Console.Write("\nPress any key to end...");
            Console.ReadKey();
        }

        // Function to apply job using 'Apply job on device' api
        private static void ApplyJob(string deviceID, string jobID)
        {
            // URI of the api
            string URL = Constants.BaseURL + "/api/jobassignment";
            // RequestBody for apply job
            var RequestBody = new
            {
                DeviceIds = new List<string>() { deviceID },
                JobId = jobID
            };
            var client = new RestClient(URL);
            // Basic authentication header
            client.Authenticator = new HttpBasicAuthenticator(Constants.Username, Constants.Password);
            // ApiKey Header
            client.AddDefaultHeader("ApiKey", Constants.ApiKey);
            // Set content type
            client.AddDefaultHeader("Content-Type", "application/json");
            // Set request method
            var request = new RestRequest(Method.POST);
            request.AddJsonBody(RequestBody);

            // Execute request
            IRestResponse response = client.Execute(request);
            if (response.StatusCode == HttpStatusCode.OK)
            {
                if (Convert.ToBoolean(response.Content.ToString()))
                {
                    Console.WriteLine("\nJob applied successfully!");
                }
                else
                {
                    Console.WriteLine("\nFailed to apply job!");
                }
            }
            else
            {
                Console.WriteLine(response.Content.ToString());
            }
        }

        // Function to retrieve ID of the job using 'Get all jobs' api
        private static string GetJobID(string jobName, string folderName)
        {
            // FolderID for root folder is "null"
            string FolderID = string.IsNullOrWhiteSpace(folderName) ? "null" : GetFolderID(folderName);

            // URI of the api
            string URL = Constants.BaseURL + "/api/job";
            var client = new RestClient(URL);
            // Basic authentication header
            client.Authenticator = new HttpBasicAuthenticator(Constants.Username, Constants.Password);
            // ApiKey Header
            client.AddDefaultHeader("ApiKey", Constants.ApiKey);
            // Set content type
            client.AddDefaultHeader("Content-Type", "application/json");
            // Set request method
            var request = new RestRequest(Method.GET);
            request.AddParameter("FolderID", FolderID, ParameterType.QueryString);
            // Execute request
            IRestResponse response = client.Execute(request);
            if (response.StatusCode == HttpStatusCode.OK)
            {
                var OutPut = JsonConvert.DeserializeObject(response.Content);
                var jsonResponse = JsonConvert.DeserializeObject<List<JObject>>(response.Content);
                if ((jsonResponse != null) && (jsonResponse.Any()))
                {
                    foreach (var job in jsonResponse)
                    {
                        if (job.GetValue("JobName").ToString() == jobName)
                        {
                            return job.GetValue("JobID").ToString();
                        }
                    }
                }
            }
            return null;
        }

        // Function to retrieve ID of the jobfolder using 'Get all folders' api
        private static string GetFolderID(string folderName)
        {
            // URI of the api
            string URL = Constants.BaseURL + "/api/jobfolder/all";
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
                if ((jsonResponse != null) && (jsonResponse.Any()))
                {
                    foreach (var folder in jsonResponse)
                    {
                        if (folder.GetValue("FolderName").ToString() == folderName)
                        {
                            return folder.GetValue("FolderID").ToString();
                        }
                    }
                }
            }
            return null;
        }

        // Function to retrieve ID of the device using 'Search device' api
        private static string GetDeviceID(string deviceName)
        {
            // URI of the api
            string URI = Constants.BaseURL + "/api/device";
            var client = new RestClient(URI);
            // Basic authentication header
            client.Authenticator = new HttpBasicAuthenticator(Constants.Username, Constants.Password);
            // ApiKey Header
            client.AddDefaultHeader("ApiKey", Constants.ApiKey);
            // Set content type
            client.AddDefaultHeader("Content-Type", "application/json");
            // Set request method
            var request = new RestRequest(Method.POST);
            // Request payload
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

            // Execute request
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
