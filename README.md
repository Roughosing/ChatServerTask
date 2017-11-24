# ChatServerTask
Chat Server repository for college assignment in module CS4400 Internet Applications - Gregory Penrose 14316190

To run, pull repository and type java -jar ChatServerTask.jar 22 //22 specifying port number 

On the automated test server site the code broke the tests, running forever midway through sending the JoinMessage for the second client, as can be seen in the image.png stored in the master of this repository. I attempted for several hours over many days to solve the problem, seeking help from demonstrators but nothing led me to a solution, as such the code present in this repository is not currently passing the automated tests, but I believe it to be sufficient enough for what the assignment set out to achieve.

The code then needs to be terminated on the server side, exiting with a result of 31. When run on a local server (several instances of putty) the code runs fine, correctly receiving input and processing output from the client to the server. An image contained in this repository shows the code acting normally amongst several instances of putty.

N.B. The code has been altered slightly to look nice in putty ('\r' added before each newline), this may cause errors when attempting to run on the automated server, although for the fact that my code is not currently successfully passing the tests anyway, I hoped to make the code look presentable and easy to read for the person marking this assignment.
