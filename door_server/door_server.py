import RPi.GPIO as GPIO
import time
import socket 
import struct
import requests 

IP_ADDRESS = ''
PORT_ADRESS = 2000

INPUT1 = 31
INPUT2 = 35
OUTPUT1 = 19

if __name__ == '__main__': 

   GPIO.setmode(GPIO.BOARD)
   GPIO.setup(INPUT1, GPIO.IN)
   GPIO.setup(INPUT2, GPIO.IN)
   GPIO.setup(OUTPUT1, GPIO.OUT)

   try:  
      # create an INET, STREAMing socket
      server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
      # bind the socket to a public host, and a well-known port
      server_socket.bind((IP_ADDRESS, PORT_ADRESS))
      # become a server socket
      server_socket.listen(5)
      print('create socket ...')
      while(True):
         private_connection = False
         connection, client_address = server_socket.accept()                   # wait for connection ...
         print("connection ... " + str(client_address))
         ip = requests.get('https://checkip.amazonaws.com').text.strip()
         if(str(ip) in client_address):
            print("local connection")
            private_connection = True
         num_of_byte = 2
         data_buffer = ""
         while(len(data_buffer) < num_of_byte):                             # read in buffer till all data received
            print("data leng " + str(len(data_buffer)))
            data_buffer += connection.recv(num_of_byte-len(data_buffer))
         print("data received ...")
         variant = struct.unpack('!h',data_buffer)[0]
         print("data: " + str(variant))

         if(variant == 1):
            print("send data: " + str(GPIO.input(INPUT1)))
            data = GPIO.input(INPUT1)
            connection.send(struct.pack('!h',data))
            print("send data: " + str(GPIO.input(INPUT2)))
            data = GPIO.input(INPUT2)
            connection.send(struct.pack('!h',data))
         else:
            print("activate relais")
            GPIO.output(OUTPUT1,1)
            time.sleep(1)
            print("deactivate relais")
            GPIO.output(OUTPUT1,0)
            print("send data: " + str(GPIO.input(INPUT1)))
            data = GPIO.input(INPUT1)
            connection.send(struct.pack('!h',data))
            print("send data: " + str(GPIO.input(INPUT2)))
            data = GPIO.input(INPUT2)
            connection.send(struct.pack('!h',data)) 
         connection.close()
         print("close socket ...")
  
   except KeyboardInterrupt:  
      print "Interrupt by keyboard"  
   except BaseException as e:
      print('Failed to do something: ' + str(e)) 
      #print "Other error or exception occurred!"  
   finally:  
      GPIO.cleanup() # this ensures a clean exit  





