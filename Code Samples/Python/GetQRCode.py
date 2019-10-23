# Description: Code for generating QR code to enroll device in particular group on SureMDM account.

import requests
import json
import base64

baseurl = "http://suremdm.42gears.com/api"  # BaseURL of SureMDM
Username = "Username"
Password = "Password"
ApiKey = "Your ApiKey"

# method to get group ID
def GetGroupID(groupName):
    # For home group no need to get groupID
    if groupName.casefold() == "Home":
        return groupName
    # Api url
    url = baseurl+"/group/1/getall"
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
        data = response.json()
        for group in data['Groups']:
            if group['GroupName'] == groupName:
                return group["GroupID"]
    else:
        return None

# methos to get QRCode
def GetQRCode(groupName):
    GroupID = GetGroupID(groupName)
    # Api url
    url = baseurl + "/QRCode/" + \
        str(GroupID) + "/default/true/UseSystemGenerated"
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
        return response.text
    else:
        return None


# Main starts
groupName="testing devices" # name of the device where you want enroll device
# Get base64 QRcode
base64String = GetQRCode(groupName)
base64String = base64String.replace("\"", "")
if base64String != None:
    with open("QRcode.png", "wb") as stream:   
        stream.write(base64.b64decode(base64String))
else:
    print("Invalid request!")
