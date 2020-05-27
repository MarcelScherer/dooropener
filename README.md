# dooropener
The project "dooropener" serves to detect the state of my garage (open/closed). The two buttons S1 and S2 determine the position of the garage door. The relay simulates a push button with which the garage door can be opened and closed. The app looks every evening at 21:00 if the door is closed. If it is not closed it send's a notofication.

wiring:<br>
<img src="https://github.com/MarcelScherer/dooropener/blob/master/docu/wiring.jpg" width="500">
<br>
The folder [app](https://github.com/MarcelScherer/dooropener/tree/master/app) is the source for the android app (in kotlin)<br>
In the folder [door_server](https://github.com/MarcelScherer/dooropener/tree/master/door_server) is python script for the raspberry pi zero w. It work only with pathon 2.7 because the gpio library not working with 3.x <br>
In the folder [test_client](https://github.com/MarcelScherer/dooropener/tree/master/test_client) there is a python script for testing the server.
<br>
<br>
my variant (not my best work :-))<br>
<img src="https://github.com/MarcelScherer/dooropener/blob/master/docu/pic.jpg" width="300">
<br>
<br>
the layout of the app<br>
<img src="https://github.com/MarcelScherer/dooropener/blob/master/docu/Screenshot.png" width="300">
