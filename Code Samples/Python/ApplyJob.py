# Description: Program for applying job on the device using SureMDM apis.

import requests,json

BaseURL="https://suremdm.42gears.com"  # Your SureMDM domain
Username="Username" # Your SureMDM username
Password="Password" # Your SureMDM password
ApiKey="Your ApiKey" # Your SureMDM apikey

# Function to retrieve ID of the device using 'Search device' api
def GetDeviceID(deviceName):
    # URI of the api
    URI = BaseURL+"/api/device"
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
    # Execute request
    response = requests.post(URI,auth=Credentials,json=PayLoad,headers=headers)
    # Extracting required GroupID
    if response.status_code == 200:
        if response.text != '[]':
            data = response.json()
            for device in data['rows']:
                if device['DeviceName'] == deviceName:
                    return device["DeviceID"]
    else:
        return None

# Function to retrieve ID of the jobfolder using 'Get all folders' api
def GetFolderID(folderName):
    # URI of the api
    URI = BaseURL+"/api/jobfolder/all"
    # Headers
    headers = {
        # Api-Key header
        'ApiKey': ApiKey,
        # Set Content type
        'Content-Type': "application/json",
        }
    # Basic authentication credentials
    Credentials=(Username,Password)
    # Execute request
    response = requests.get(URI,auth=Credentials,headers=headers)
    # Extracting required FolderID
    if response.status_code == 200:
        if response.text != '[]':
            data = response.json()
            for folder in data:
                if folder['FolderName'] == folderName:
                    return folder['FolderID']
            return 'null'
    else:
        return "null" # folder ID for default folder is 'null'

# Function to retrieve ID of the job using 'Get all jobs' api
def GetJobID(jobName,folderName):
    # FolderID for root folder is "null"
    FolderID="null" if not folderName.strip() else GetFolderID(folderName)

    # URI of the api
    URI = BaseURL+"/api/job"
    # Headers
    headers = {
        # Api-Key header
        'ApiKey': ApiKey,
        # Set Content type
        'Content-Type': "application/json",
        }
    # Basic authentication credentials
    Credentials=(Username,Password)
    # Query parameters
    params={"FolderID":FolderID}
    # Execute request
    response = requests.get(URI,auth=Credentials,params=params,headers=headers)
    # Extracting required JobID
    if response.status_code == 200:
        if response.text != '[]':
            data = response.json()
            for job in data:
                if job['JobName'] == jobName:
                    return job['JobID']
    else:
        return None

# Function to apply job using 'Apply job on device' api
def ApplyJob(deviceID,jobID):
    
    # URI of the api
    URI = BaseURL+"/api/jobassignment" 
    # Headers
    headers = {
        # Api-Key header
        'ApiKey': ApiKey,
        # Set Content type
        'Content-Type': "application/json",
        }
    # Basic authentication credentials
    Credentials=(Username,Password)
    # RequestBody for apply job
    RequestBody={
        "DeviceIds": [deviceID],
        "JobId": jobID
    }
    # Execute request
    response = requests.post(URI,auth=Credentials,json=RequestBody,headers=headers)
    if response.status_code == 200:
        print("Job applied successfully!") if response.text.casefold()=="true" else print("Failed to apply job!")
    else:
        print(response.text)



# Main starts
DeviName="rajkot_elite" # Name of the device on which job will be applied
JobName="salmantest"    # Name of the job which will bw applied
FolderName=""           # Name of the jobfolder where the job is stored
# Note: Keep FolderName empty if job is stored in root folder

# Retrieve ID of the device using name of device
DeviceID=GetDeviceID(DeviName)

if DeviceID!=None:

    # Retrieve ID of the job using name of the job
    JobID=GetJobID(JobName,FolderName)

    if JobID != None:
        # Apply job on the device
        ApplyJob(DeviceID,JobID)
    else:
        print('Job not found!')
else:
    print('Device not found!')