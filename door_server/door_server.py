import RPi.GPIO as GPIO
import time
import socket 
import struct
import logging
import time
import sys
from Crypto.Signature import PKCS1_v1_5
from Crypto.Hash import SHA256
from Crypto.PublicKey import RSA
from base64 import b64decode

IP_ADDRESS = ''
PORT_ADRESS = 2000

INPUT1 = 31
INPUT2 = 35
OUTPUT1 = 19

def main():

   GPIO.setmode(GPIO.BOARD)
   GPIO.setup(INPUT1, GPIO.IN)
   GPIO.setup(INPUT2, GPIO.IN)
   GPIO.setup(OUTPUT1, GPIO.OUT)

   keyDER = b64decode(open('public.pem').read())
   print(keyDER)
   pubkey = RSA.importKey(keyDER)

   try:  
      # create an INET, STREAMing socket
      server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
      server_socket.settimeout(None)
      # bind the socket to a public host, and a well-known port
      server_socket.bind((IP_ADDRESS, PORT_ADRESS))
      # become a server socket
      server_socket.listen(5)
      print('create socket ...')
      while(True):
             
         try:
            private_connection = False
            connection, client_address = server_socket.accept()                   # wait for connection ...
            print("connection ... " + str(client_address))
            server_socket.settimeout(10)
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
               print("try to open door ... " )
               unixtime = int(time.time())
               print(str(unixtime))
               connection.send(struct.pack('!i',unixtime))
               #-----------------------------------
               chipher = b''
               server_socket.settimeout(20)
               while(len(chipher) < 256):                           
                  chipher += connection.recv(256-len(chipher))
                  print("data leng " + str(len(chipher)))
               print("encrypt chipher")
               try:
                  hash_value = SHA256.new(str(unixtime))
                  print(hash_value.hexdigest())
                  verifier = PKCS1_v1_5.new(pubkey)
                  if verifier.verify(hash_value, chipher):
                     print("The signature is authentic.")
                     #print("activate relais")
                     #GPIO.output(OUTPUT1,1)
                     #time.sleep(1)
                     #print("deactivate relais")
                     #GPIO.output(OUTPUT1,0)
                  else:
                     print("The signature is not authentic.")
               except Exception as e:
                  print("encrpyt error: %s", e)
            connection.close()
            server_socket.settimeout(None)
            print("close socket ...")
         except Exception as e:
            logging.error("connection error. Error: %s", e)
            print("Oops!  That was no valid number.  Try again...")
   
   except KeyboardInterrupt:  
      print "Interrupt by keyboard"  
   except BaseException as e:
      print('Failed to do something: ' + str(e)) 
      #print "Other error or exception occurred!"  
   finally:  
      time.sleep(5)
      GPIO.cleanup() # this ensures a clean exit  


if __name__ == '__main__': 
   logging.basicConfig(filename='garage.log', filemode='w', format='%(name)s - %(levelname)s - %(message)s')
   while True:
      print("start skript ...")
      try:
         main()
      except Exception as e:
         logging.error("main crashed. Error: %s", e)
         time.sleep(5)




