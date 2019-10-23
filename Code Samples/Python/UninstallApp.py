# Description: Uninstall application from device

import requests,json,base64

BaseURL="https://suremdm.42gears.com/api"  # BaseURL of SureMDM
Username="Username"
Password="Password"
ApiKey="Your ApiKey"

# Method for getting device info
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

# method to get app id
def GetAppID(deviceID,appName):
    url = BaseURL+"/installedapp/android/"+deviceID+"/device"
    #  Headers
    headers = {
        #  Api-Key header
        'ApiKey': ApiKey,
        #  Set Content type
        'Content-Type': "application/json",
        }
    #  Basic authentication credentials
    Credentials=(Username,Password)
    #  send request
    response = requests.get(url,auth=Credentials,headers=headers)
    # Extracting required GroupID
    if response.status_code == 200:
        if response.text != '[]':
            jsonResponse = response.json()
            for app in jsonResponse:
                if app['Name'] == appName:
                    return app['Id']
    else:
        return None

# method to uninstall application
def UninstallApp(deviceID,appName):
    url = BaseURL+"/dynamicjob"
    #  Headers
    headers = {
        #  Api-Key header
        'ApiKey': ApiKey,
        #  Set Content type
        'Content-Type': "application/json",
        }
    #  Basic authentication credentials
    Credentials=(Username,Password)
    # Create job specific PayLoad
    PayLoad={
        "AppIds":[GetAppID(deviceID,appName)]
    }
    # Convert payload to base64 string
    PayLoadStr=json.dumps(PayLoad)
    PayLoadBase64=base64.b64encode(PayLoadStr.encode('utf-8'))
    # Request payload for refreshing device
    RequestPayLoad={
        "JobType": "UNINSTALL_APPLICATION",
        "DeviceID": deviceID,
        "PayLoad":PayLoadBase64.decode('utf-8')
    }
    #  send request
    response = requests.post(url,auth=Credentials,json=RequestPayLoad,headers=headers)
    return response.text

#Main starts
deviceName="device 1" # name of the device
appName="AstroContacts" # name of the application which you want to uninstall

DeviceID=GetDeviceID(deviceName)
if DeviceID!=None:
    # Uninstall app
    status=UninstallApp(DeviceID,appName)
    print(status)
else:
    print('Device not found!')