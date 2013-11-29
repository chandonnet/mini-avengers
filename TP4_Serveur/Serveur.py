'''
Created on 29 nov. 2013

@author: user
'''
#!/usr/bin/python   
# -*- coding: iso-8859-1 -*-
 
#import mysocket
import socket
import threading
from os import listdir
import os
from xml.dom.minidom import parse, parseString
 
class serveur:
    """
    Squelette du serveur
    """
 
    # Se donner un objet de la classe socket.
    un_socket = socket.socket()
 
    # Socket connexion au client.
    connexion = None
 
    MAX_RECV = 1024
 
    def __init__(self, host, port):
        "Constructeur du serveur et attendre une connection"
 
        self.un_socket.bind((host, port))    
 
        # Passer en mode ecoute.
        self.un_socket.listen(5)  
 
        c, addr = self.un_socket.accept()
        self.connexion = c
 
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
### Main ###
if __name__ == '__main__':
    serv = serveur('localhost', 12351)
    
    msgServer = ""
   
        
    # On attend un message du client.
    msgClient = serv.connexion.recv(serv.MAX_RECV)
 
    try:
        dom = parseString(msgClient)
    except:
        print "XML invalide!"
        serv.connexion.close()
        break
             
    
    # Traitement de <bonjour />
        
    for node in dom.getElementsByTagName('bonjour'): 
        if node.firstChild == None:
            msgServeur = serv.bonjour()
            
    # Traitement de <message />
        
    for node in dom.getElementsByTagName('message'): 
        if node.firstChild == None:
            msgServeur = serv.bonjour()
    
    # Traitement de <skipVideo />
        
    for node in dom.getElementsByTagName('skipVideo'): 
        if node.firstChild == None:
            msgServeur = serv.bonjour()
    
    
    # Traitement de <messageAdmin />
        
    for node in dom.getElementsByTagName('messageAdmin'): 
        if node.firstChild == None:
            msgServeur = serv.bonjour()
            
    # Traitement de <videoLink />
        
    for node in dom.getElementsByTagName('videoLink'): 
        if node.firstChild == None:
            msgServeur = serv.bonjour()
                
    serv.connexion.send(msgServeur)
    
#########################################

class  thread(threading.thread):
    
    def __init__(self):
        
        
     
        
        
    def run(self):