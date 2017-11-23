# ChatServerTask
Chat Server repository for college assignment in module CS4400 Internet Applications - Gregory Penrose

On the automated test server site the code broke the tests, running forever midway through sending the JoinMessage for the second client, as can be seen in the image below.

![](ChatServerTask/image.png?raw=true "Automated Test Score")

The code then needs to be terminated on the server side, exiting with a result of 31. When run on a local server (several instances of putty) the code runs fine, correctly receiving input and processing output from the client to the server.
