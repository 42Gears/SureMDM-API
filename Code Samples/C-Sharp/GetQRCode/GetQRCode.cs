using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using RestSharp;
using RestSharp.Authenticators;
using System;
using System.IO;
using System.Net;

namespace GetQRCode
{
    static class Constants
    {
        public const string BaseURL = "https://suremdm.42gears.com/api";  // Your SureMDM domain
        public const string Username = "Username"; // Your SureMDM username 
        public const string Password = "Password"; // Your SureMDM password
        public const string ApiKey = "Your ApiKey"; // Your SureMDM apikey
    }
    class GetQRCode
    {
        static void Main(string[] args)
        {
            string groupName = "testing devices"; // name of the device where you want enroll device

            //Get base64 QRcode
            string base64String = GetQRCodeImage(groupName);
            base64String = base64String.Replace("\"", "");
            Console.WriteLine(base64String);
            if (base64String != null)
            {
                Byte[] QRCode = Convert.FromBase64String(base64String);
                using (var imageFile = new FileStream("QRCode.png", FileMode.Create))
                {
                    imageFile.Write(QRCode, 0, QRCode.Length);
                    imageFile.Flush();
                }
            }
            else
            {
                Console.WriteLine("Invalid request!");
            }
            Console.ReadKey();
        }

        // methos to get QRCode
        private static string GetQRCodeImage(string groupName)
        {
            // API URL
            string URL = Constants.BaseURL + "/QRCode/" + GetGroupID(groupName) + "/default/true/UseSystemGenerated";
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

            if (response.StatusCode == HttpStatusCode.OK && !string.IsNullOrWhiteSpace(response.Content))
            {
                return response.Content;
            }
            return null;
        }

        // method to get group ID
        static string GetGroupID(string GroupName)
        {
            // For home group no need to get groupID
            if (string.Equals(GroupName, "Home", StringComparison.InvariantCultureIgnoreCase))
            {
                return GroupName;
            }


            // API URL
            string URL = Constants.BaseURL + "/group/1/getall";
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
                var OutPut = JsonConvert.DeserializeObject<JObject>(response.Content);
                foreach (var group in OutPut["Groups"])
                {
                    if ((string)group["GroupName"] == GroupName)
                    {
                        Console.WriteLine("hello " + group["GroupID"].ToString());
                        return group["GroupID"].ToString();
                    }
                }
            }
            return null;
        }
    }
}
