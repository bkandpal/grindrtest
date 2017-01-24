package com.grindr.donors.test;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;

import java.util.List;


/**
 * Created by bkandpal on 1/23/17.
 */
public class DonorsApiApp {

    static String costToCompleteRangeString = "0 TO 2000";
    static int maxSearchResults = 5;
    static String stateStr = "CA";
    static String sortStr = "0";
    static String stateFullName = "California";
    static double fundingCostRangeMin = 0.0;
    static double fundingCostRangeMax = 2000.0;

    public static void main(String[] args) throws Exception{

        String searchParameter=null;

        //Capture the search parameter passed as an argument
        for(int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
            searchParameter = args[0];
        }
        parseDonorsApiResponse(searchParameter);
    }

    public static void parseDonorsApiResponse(String searchParameter) {
        RestAssured.baseURI = "https://api.donorschoose.org/common/json_feed.html";
        Double avgOfTotalPrice = 0.0;
        Double avgOfPercentFunded = 0.0;
        Double avgOfCostToComplete = 0.0;
        int avgOfTotalDonars = 0;
        int avgOfTotalStudents = 0;

        Response webResponse = given().
                queryParam("keywords", searchParameter).
                queryParam("costToCompleteRange", costToCompleteRangeString).
                queryParam("max", maxSearchResults).
                queryParam("state", stateStr).
                queryParam("sortBy", sortStr).
                when().
                get();

        JsonPath jsonPath = new JsonPath(webResponse.getBody().asString());

        //Get total number of proposals from response
        List totalNumOfProposals = webResponse.getBody().jsonPath().getList("proposals", String.class);

        //If there are proposals returned in the response
        if(totalNumOfProposals.size() > 0) {

            //Assert when search results are more than maxSearchResults
            assertThat(totalNumOfProposals.size(), is(lessThanOrEqualTo(maxSearchResults)));

            //Assert when results are not limited to stateFullName
            assertThat(webResponse.getBody().jsonPath().getList("proposals.stateFullName", String.class), everyItem(is(stateFullName)));

            List<Double> costToComplete = webResponse.getBody().jsonPath().getList("proposals.costToComplete", Double.class);

            //Assert when funding cost range is not between fundingCostRangeMin and fundingCostRangeMax
            assertThat(costToComplete, everyItem(greaterThan(fundingCostRangeMin)));
            assertThat(costToComplete, everyItem(lessThan(fundingCostRangeMax)));

            //Print the data from json and get average of values
            for (int i = 0; i < totalNumOfProposals.size(); i++) {
                System.out.println("\n\n********************************** Details from Proposal Number: " + (i + 1) + " ***********************************************");
                System.out.println("Title: " + jsonPath.get("proposals[" + i + "].title"));
                System.out.println("Short Description: " + jsonPath.get("proposals[" + i + "].shortDescription"));
                System.out.println("Proposal URL: " + jsonPath.get("proposals[" + i + "].proposalURL"));
                System.out.println("Cost To Complete: " + jsonPath.get("proposals[" + i + "].costToComplete"));

                avgOfTotalPrice = avgOfTotalPrice + jsonPath.getDouble("proposals[" + i + "].totalPrice");
                avgOfPercentFunded = avgOfPercentFunded + jsonPath.getDouble("proposals[" + i + "].percentFunded");
                avgOfTotalDonars = avgOfTotalDonars + jsonPath.getInt("proposals[" + i + "].numDonors");
                avgOfCostToComplete = avgOfCostToComplete + jsonPath.getDouble("proposals[" + i + "].costToComplete");
                avgOfTotalStudents = avgOfTotalStudents + jsonPath.getInt("proposals[" + i + "].numStudents");

            }
            System.out.println("******************************************************************************************************************\n\n");

            //Print average of values
            System.out.println("********************************************* ==>Average<== ******************************************************");
            System.out.println("AVG Total Price: " + avgOfTotalPrice);
            System.out.println("AVG Percent Funded: " + avgOfPercentFunded);
            System.out.println("Num Donors: " + avgOfTotalDonars);
            System.out.println("Cost To Complete: " + avgOfCostToComplete);
            System.out.println("Num Students: " + avgOfTotalStudents);
            System.out.println("******************************************************************************************************************\n\n");
        } else {
            //Print info when no Proposal found
            System.out.println("******************************************************************************************************************\n\n");
            System.out.println("=======> No Proposal found matching the search scenario! <=======");
            System.out.println("\n\n******************************************************************************************************************");
        }

    }
}
