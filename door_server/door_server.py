import RPi.GPIO as GPIO
import time
import socket 
import struct

IP_ADDRESS = ''
PORT_ADRESS = 2000

if __name__ == '__main__': 

   GPIO.setmode(GPIO.BOARD)
   GPIO.setup(3, GPIO.IN)
   GPIO.setup(5, GPIO.IN)
   GPIO.setup(29, GPIO.OUT)

   try:  
      # create an INET, STREAMing socket
      server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
      # bind the socket to a public host, and a well-known port
      server_socket.bind((IP_ADDRESS, PORT_ADRESS))
      # become a server socket
      server_socket.listen(5)
      print('create socket ...')

      while(True):
         connection, client_address = server_socket.accept()                   # wait for connection ...
         print("connection ... " + str(client_address))

         num_of_byte = 2
         data_buffer = ""
         while(len(data_buffer) < num_of_byte):                             # read in buffer till all data received
            print("data leng " + str(len(data_buffer)))
            data_buffer += connection.recv(num_of_byte-len(data_buffer))
         print("data received ...")
         variant = struct.unpack('!h',data_buffer)[0]
         print("data: " + str(variant))

         if(variant == 1):
            print("send data: " + str(GPIO.input(3)))
            data = GPIO.input(3)
            connection.send(struct.pack('!h',data))
            print("send data: " + str(GPIO.input(5)))
            data = GPIO.input(5)
            connection.send(struct.pack('!h',data))
         else:
            print("activate relais")
            GPIO.output(29,1)
            time.sleep(5)
            GPIO.output(29,0)
            print("send data: " + str(GPIO.input(3)))
            data = GPIO.input(3)
            connection.send(struct.pack('!h',data))
            print("send data: " + str(GPIO.input(5)))
            data = GPIO.input(5)
            connection.send(struct.pack('!h',data)) 
         connection.close()
         print("close socket ...")
  
   except KeyboardInterrupt:  
      print "Interrupt by keyboard"  
   except:  
      print "Other error or exception occurred!"  
   finally:  
      GPIO.cleanup() # this ensures a clean exit  





