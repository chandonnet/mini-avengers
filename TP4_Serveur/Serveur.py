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

from os import listdir
import os
from xml.dom.minidom import parse, parseString
 

class Serveur:
    """
    Squelette du serveur
    """
 
    # Se donner un objet de la classe socket.
    un_socket = socket.socket()
 
    # Socket connexion au client.
    connexion = None
 
    MAX_RECV = 1024
 
    def __init__(self, host, port):
        self.host = host
        self.port = port
        self.backlog = 5;
        self.threads = []    
        self.serveur = None
        # Passer en mode ecoute.
    
    def sendToAll(self, msg):
        for t in self.threads:
            t.send(msg)
    
    def open_socket(self):
        try: 
            self.server = socket.socket(socket.AF_INET, socket.SOCK_STREAM) 
            self.server.bind((self.host,self.port)) 
            self.server.listen(5) 
        except socket.error, (e, message): 
            print "Could not open socket: " + message 
            sys.exit(1)
            
    def run(self):
        self.open_socket() 
        input = [self.server,sys.stdin] 
        running = 1 
        while running: 
            inputready,outputready,exceptready = select.select(input,[],[]) 
            
            for s in inputready: 
                
                if s == self.server: 
                    # handle the server socket 
                    c = Client(self.server.accept(), self) 
                    c.start()
                    self.threads.append(c) 

                elif s == sys.stdin: 
                    # handle standard input 
                    junk = sys.stdin.readline() 
                    running = 0 

        # close all threads 

        self.server.close() 
        for c in self.threads: 
            c.join() 
    
        
    def bonjour(self):
        "Traitement de <bonjour />"
        dom = parseString("<bonjour >")
        return dom.toxml()
        
    def message(self,sender,msg):
        "Traitement de <message />"
        dom = parseString("<messageClient><sender>"+sender+"</sender><message>"+msg+"</message></messageClient>")
        return dom.toxml()
    
    def messageAdmin(self,msg):
        "Traitement de <messageAdmin />"
        dom = parseString("<messageAdmin>"+msg+"</messageAdmin>")
        return dom.toxml()
    
    def videoLink(self,link):
        "Traitement de <link />"
        dom = parseString("<link>"+link+"</link>")
        return dom.toxml()

##########################################
class Client (threading.Thread):
    
    def __init__(self, (client,address), serveur):
        threading.Thread.__init__(self)
        threading.Thread.__init__(self) 
        self.client = client 
        self.serveur = serveur
        self.address = address
        self.size = 1024
        self.client.send("Serveur: Bienvenue!")

    def send(self,  msg):
        self.client.send(msg)
        
    def run(self): 
        running = 1 
        while running: 
            data = self.client.recv(self.size) 
            sys.stdout.write(data)
            if data: 
                self.serveur.sendToAll(data)
                
            
            
        
##########################################
### Main ###
if __name__ == '__main__':
    serv = Serveur('162.209.100.18', 50008)
    serv.run()
 
   
        
   
      
    
#########################################

