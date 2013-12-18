'''
Created on 29 nov. 2013

@author: user
'''
#!/usr/bin/python   
# -*- coding: iso-8859-1 -*-
 
#import mysocket
import select 
import socket 
import sys 
import threading 
import json
import os 
 

class Serveur:
    """
    Class du serveur, capable de gérer plusieurs clients
    """

 
    MAX_RECV = 1024
 
    def __init__(self, host, port):
        self.host = host
        self.port = port
        self.backlog = 5;
        self.threads = []    
        self.server = None
        self.open_socket()
    
    #Envoie le message a tous les clients
    def sendToAll(self, msg):
        for thread in self.threads:
            try:
                thread.send(msg)
            except socket.error, e:
                self.threads.remove(thread)
                thread.close()
                
                print e 
                
    #Ouvre le socket du serveur
    def open_socket(self):
        try: 
            self.server = socket.socket(socket.AF_INET, socket.SOCK_STREAM) 
            self.server.bind((self.host,self.port)) 
            self.server.listen(5) 
        except socket.error, (e, message): 
            print "Could not open socket: " + message 
            sys.exit(1)
            
    def run(self):
                                        
        input = [self.server,sys.stdin] 
        running = 1 
        while running: 
            inputready,outputready,exceptready = select.select(input,[],[]) 
            
            for s in inputready: 
                
                if s == self.server: 
                    #Ajoute un nouveau socket pour le client
                    c = Client(self.server.accept(), self) 
                    c.start()
                    self.threads.append(c) 

                elif s == sys.stdin: 
                    junk = sys.stdin.readline() 
                    running = 0
                    
             

        #Ferme les threads de clients 

        self.server.close() 
        for thread in self.threads: 
            thread.join() 
    
#Thread servant à gérer un client
##########################################
class Client (threading.Thread):
    
    def __init__(self, (client,address), serveur):
        threading.Thread.__init__(self)
        threading.Thread.__init__(self) 
        self.client = client 
        self.serveur = serveur
        self.address = address
        self.size = 1024
        

    #Envoie des données à partir du socket 
    def send(self,  msg):
        self.client.send(msg)
    #Retourne le socket    
    def getClient(self):
        return self.client
    #retourne l'adresse du socket
    def getAddress(self):
        return self.address
    #Ferme le socket
    def close(self):
        self.client.close()

    def run(self): 
        running = 1 
        self.client.send("Serveur: Bienvenue! \n")
        while running: 
            data = self.client.recv(self.size) 
            sys.stdout.write(data)
            if data: 
                
                self.serveur.sendToAll(data)
                
        self.client.close()
            
        
##########################################
### Main ###
if __name__ == '__main__':
    serv = Serveur('162.209.100.18', 50008)
    serv.run()
 
   
        
   
      
    
#########################################

