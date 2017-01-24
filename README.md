# grindrtest
Pre-condition: Maven is needed to run this code
Step to run from command prompt:
  1. Browse to the code directory on terminal.
  2. run :> mvn clean install
  3.     :> mvn exec:java -Dexec.mainClass="com.grindr.donors.test.DonorsApiApp" -Dexec.args="Los Angeles"
> -Dexec.args="Los Angeles", is the search parameter passed to the app from command line.
