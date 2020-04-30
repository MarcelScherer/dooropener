from socket import *
import struct


IP_ADDRESS = '192.168.2.107'
PORT_ADRESS = 2000

if __name__ == '__main__': 
   clientsocket = socket(AF_INET, SOCK_STREAM)                        # create socket
   clientsocket.connect((IP_ADDRESS, PORT_ADRESS))   
   data = 2
   clientsocket.send(struct.pack('!h',data))                             # send a default "1"

   num_of_byte = 2
   data_buffer = bytearray()
   while(len(data_buffer) < num_of_byte):                             # read in buffer till all data received
      data_buffer += clientsocket.recv(num_of_byte-len(data_buffer))
   data_receive_1 = struct.unpack('!h',data_buffer)[0]
   print(data_receive_1)
   data_buffer = bytearray()
   while(len(data_buffer) < num_of_byte):                             # read in buffer till all data received
      data_buffer += clientsocket.recv(num_of_byte-len(data_buffer))
   data_receive_2 = struct.unpack('!h',data_buffer)[0]
   print(data_receive_2)




