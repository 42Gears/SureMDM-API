import requests,json

baseurl="https://suremdm.42gears.com/api"    # BaseURL of SureMDM
Username="Username"
Password="Password"
ApiKey="Your ApiKey"

# Method for getting device info
def GetDeviceID(deviceName):
    # Api url
    url = baseurl+"/device"
    # Headers
    headers = {
        # Api-Key header
        'ApiKey': ApiKey,
        # Set Content type
        'Content-Type': "application/json",
        }
    # Basic authentication credentials
    Credentials=(Username,Password)
    # Request body
    PayLoad={
        "ID" : "AllDevices",
        "IsSearch" : bool(1),
        "Limit" : 10,
        "SearchColumns" : ["DeviceName"],
        "SearchValue" : deviceName,
        "SortColumn" : "LastTimeStamp",
        "SortOrder" : "asc"
    }
    # Executing request
    response = requests.post(url,auth=Credentials,json=PayLoad,headers=headers)
    # Extracting required GroupID
    if response.status_code == 200:
        if response.text != '[]':
            data = response.json()
            for device in data['rows']:
                if device['DeviceName'] == deviceName:
                    return device["DeviceID"]
    else:
        return None

# Method to apply dynamic jobs
def RefreshDeviceInfo(deviceID):
    PayLoad={
        "JobType": "Refresh_Device",
        "DeviceID": deviceID
    }
    # Api url
    url = baseurl+"/dynamicjob"
    # Headers
    headers = {
        # Api-Key header
        'ApiKey': ApiKey,
        # Set Content type
        'Content-Type': "application/json",
        }
    # Basic authentication credentials
    Credentials=(Username,Password)
    # Executing request
    response = requests.post(url,auth=Credentials,json=PayLoad,headers=headers)
    return response.text

# Main starts
deviceName="device 1" #Name of the device 
DeviceID=GetDeviceID(deviceName)
if DeviceID!=None:
    status=RefreshDeviceInfo(DeviceID)
    print(status)
else:
    print('Device not found!')