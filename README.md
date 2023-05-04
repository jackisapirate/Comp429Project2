# Comp429Project2

### Team members 

+ Ayush Thapaliya

+ Kunlong Wang 

### Problem Statement

In this assignment you will implement a simplified version of the Distance Vector Routing Protocol. The protocol will be run on top of four servers/laptops (behaving as routers) using TCP. Each server runs on a machine at a pre-defined port number. The servers should be able to output their forwarding tables along with the cost and should be robust to link changes. A server should send out routing packets only in the following two conditions: a) periodic update and b) the user uses command asking for one. This is a little different from the original algorithm which immediately sends out update routing information when routing table changes.

### Server Commands/Input Format 

**The server must support the following command at startup:** 
• server -t <topology-file-name> -i <routing-update-interval> 
**topology-file-name:** The topology file contains the initial topology configuration for the 
server, e.g., timberlake_init.txt. Please adhere to the format described in 3.1 for your topology files. 
**routing-update-interval:** It specifies the time interval between routing updates in seconds. 
**port and server-id:** They are written in the topology file. The server should find its port and server-id in the topology file without changing the entry format or adding any new entries. 

 

**The following commands can be specified at any point during the run of the server:** 

+ update <server-ID1> <server-ID2> <Link Cost> 
  **server-ID1, server-ID2:** The link for which the cost is being updated. 
  **Link Cost:** It specifies the new link cost between the source and the destination server. Note that this command will be issued to both server-ID1 and server-ID2 and involve them to update the cost and no other server. 
  For example: 
  **update 1 2 inf:** The link between the servers with IDs 1 and 2 is assigned to infinity. 
  **update 1 2 8:** Change the cost of the link to 8. 

 

+ step 
  Send routing update to neighbors right away. Note that except this, routing updates only 
  happen periodically. 

 

+ packets 
  Display the number of distance vector (packets) this server has received since the last 
  invocation of this information. 

 

+ display 
  Display the current routing table as explained in our lecture, including current and 
  neighboring nodes’ distance vector. 

 

+ disable <server-ID> 
  Disable the link to a given server. Doing this “closes” the connection to a given server with server-ID. Here you need to check if the given server is its neighbor. 

 

+ crash 
  “Close” all connections. This is to simulate server crashes. Close all connections on all links. The neighboring servers must handle this close correctly and set the link cost to infinity. 