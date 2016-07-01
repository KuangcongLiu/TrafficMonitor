# TrafficMonitor
  This is an Android app that could track the data usage of the phone, basically using the method of "TrafficStats".
  
  
## Functions
  1. 
  
    a)  Recieved (Bytes)=Total data recieved since the app was installed 

    b)  Recieved (Bytes)=Total data sent since the app was installed
    
    c)  Latest = Total Bytes at this moment (or after click the button (Take Snapshot))
    
    d)  Previous = Total Bytes before click the button (Take Snapshot)
  
  2. 
   The ListView under the table shows what apps are using the data currently.
  
   By clicking on the package name of the app, the user could get an alert, showing how many bytes this specific app has recieved and sent. 

  3. 
    Since "TrafficStats" will be cleared every time of rebooting, I solved this problem by saving all the data in a database using SQLite. 

## 
