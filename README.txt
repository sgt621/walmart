Welcome to the Walmart homework assignment for Samira Ghazi-Tehrani.
Please note: UI is available, but no web services.
The following steps will build and deploy the walmart app:
1. mvn clean install
2. copy walmart/target/tickets.war to the appropriate directory in your container (i.e. tomcat)
3. point your browser to http://localhost/tickets

Assumptions:
1. All seats should be together. If the user requests 6000 seats, the system will request that the user split up the seats into appropriate sizes.
2. Based on requirements provided, it is OK to expose the holdSeatSeq to the user.
3. Once the server is stopped, it is OK if the database is wiped.