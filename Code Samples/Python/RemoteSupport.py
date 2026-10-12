# Description: Get remote support URL of the device

import requests
import json
import base64

BaseURL = "<baseURL>"  # BaseURL of SureMDM
Username = "<your-username>"
Password = "<your-password>"
ApiKey = "<your-apikey>"

# get device ID using device name
def GetDeviceID(deviceName):
    # Api url
    url = BaseURL+"/device"
    # Headers
    headers = {
        # Api-Key header
        'ApiKey': ApiKey,
        # Set Content type
        'Content-Type': "application/json",
    }
    # Basic authentication credentials
    Credentials = (Username, Password)
    # Request body
    PayLoad = {
        "ID": "AllDevices",
        "IsSearch": bool(1),
        "Limit": 10,
        "SearchColumns": ["DeviceName"],
        "SearchValue": deviceName,
        "SortColumn": "LastTimeStamp",
        "SortOrder": "asc"
    }
    # Executing request
    response = requests.post(url, auth=Credentials,
                             json=PayLoad, headers=headers)
    # Extracting required GroupID
    if response.status_code == 200:
        if response.text != '[]':
            data = response.json()
            for device in data['rows']:
                if device['DeviceName'] == deviceName:
                    return device["DeviceID"]
    else:
        return None

# get URL for remote support of the device
def GetRemoteSupportURL(deviceName):
    DeviceID = GetDeviceID(deviceName)
    # Api url
    url = BaseURL+"/device/"+DeviceID
    # Add headers
    headers = {
        # Api-Key header
        'ApiKey': ApiKey,
        # Set Content type
        'Content-Type': "application/json",
    }
    # Basic authentication credentials
    Credentials = (Username, Password)
    # Executing request
    response = requests.get(url, auth=Credentials, headers=headers)
    # Extracting required GroupID
    if response.status_code == 200:
        if response.text != '[]':
            data = response.json()
            for device in data:
                if device['DeviceName'] == deviceName:
                    remoteSupporturl = BaseURL + "/RemoteSupport.aspx?" + "id=" + str(device["DeviceID"]) + "&name=" + str(device["DeviceName"]) + "&userid=" + str(
                        device["UserID"]) + "&pltFrmType=" + str(device["PlatformType"]) + "&agentversion=" + str(device["AgentVersion"]) + "&perm=126,127,128,129"
                    return remoteSupporturl
    else:
        return None

#Main starts
deviceName="device 1"
# Generate remote support URL for device
URL = GetRemoteSupportURL(deviceName)
print(URL)
