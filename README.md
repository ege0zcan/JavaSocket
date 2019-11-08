# JavaSocket

Socket programming with a Java Client and a Python Server. Assignment for Computer Networks course.
The server code was not written by me.

## Process
- The client connects to the given server at given port
- Client authenticates itself 
- Client asks for images
- Client retrieves images
- The program terminates

## How to run

First start the server from the console:`python3 Server.py <ip> <port>`

Then run the client code: `java ImageLabeler.java <ip> <port> `

Example:
`python3 Server.py 127.0.0.1 60000` and
`java ImageLabeler.java 127.0.0.1 60000 `

