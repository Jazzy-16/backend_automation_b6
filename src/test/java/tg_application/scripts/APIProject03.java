package tg_application.scripts;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import static org.hamcrest.Matchers.*;

import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tg_application.pojo_classes.CreateNewUser;
import tg_application.pojo_classes.UpdatedNewUser;
import utils.ConfigReader;
import utils.DBUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class APIProject03 {
    RequestSpecification baseSpec;
    Response response;
    Faker faker = new Faker();

    String firstName;
    String lastName;
    String email;
    String dob;

    @BeforeMethod
    public void setAPI(){

        baseSpec = new RequestSpecBuilder().log(LogDetail.ALL)
                .setBaseUri(ConfigReader.getProperty("TGSchoolBaseURI"))
                .setContentType(ContentType.JSON)
                .build();
        DBUtil.createDBConnection();

    }

    @Test
    public void tg_applicationAPI(){

        /* Create a new user */

        CreateNewUser createNewUser = CreateNewUser.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .dob("1994-07-27")
                .build();

        response = RestAssured.given()
                .spec(baseSpec)
                .body(createNewUser)
                .when().post("/students")
                .then().log().all()
                .assertThat().statusCode(200)
                .time(Matchers.lessThan(4000L))
                .extract().response();

        int id = response.jsonPath().getInt("id");


        // Validate the student is created in the database
        String query = "SELECT * FROM STUDENT WHERE id = " + id;

        List<List<Object>> queryResultList = DBUtil.getQueryResultList(query);

        List<Object> dbResult = queryResultList.get(0);

        BigDecimal dbId = (BigDecimal) dbResult.get(0);
        int dbIdInt = dbId.intValue();

        List<Object> formattedDBResult = new ArrayList<>(dbResult);
        // This is where we replace BigDecimal id with our cast int id
        formattedDBResult.set(0, dbIdInt);

        for (Object o : formattedDBResult) {
            System.out.println(o);
        }

        Assert.assertEquals(formattedDBResult, Arrays.asList(id, createNewUser.getDob(),
                createNewUser.getEmail(), createNewUser.getFirstName(),
                createNewUser.getLastName()));

        /* Retrieve a specific user-created */

        response = RestAssured.given()
                .spec(baseSpec)
                .when().get("/students/"+ String.valueOf(id))
                .then().log().all()
                .assertThat().statusCode(200)
                .time(Matchers.lessThan(4000L))
                .extract().response();

        String query1 = "SELECT * FROM STUDENT WHERE id = " + id;

        List<List<Object>> queryResultList1 = DBUtil.getQueryResultList(query1);

        List<Object> dbResult1 = queryResultList1.get(0);

        BigDecimal dbId1 = (BigDecimal) dbResult1.get(0);
        int dbIdInt1 = dbId1.intValue();

        List<Object> formattedDBResult1 = new ArrayList<>(dbResult1);

        formattedDBResult1.set(0, dbIdInt1);

        for (Object o : formattedDBResult1) {
            System.out.println(o);
        }

        Assert.assertEquals(formattedDBResult1, Arrays.asList(id, createNewUser.getDob(),
                createNewUser.getEmail(), createNewUser.getFirstName(),
                createNewUser.getLastName()));

        /* Update an existing user */

        UpdatedNewUser updatedNewUser = UpdatedNewUser.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .dob("1994-12-06")
                .build();

        response = RestAssured.given()
                .spec(baseSpec)
                .body(updatedNewUser)
                .when().put("/students/" + String.valueOf(id))
                .then().log().all()
                .assertThat().statusCode(200)
                .time(Matchers.lessThan(4000L))
                .extract().response();

        String query2 = "SELECT * FROM STUDENT WHERE id = " + id;

        List<List<Object>> queryResultList2 = DBUtil.getQueryResultList(query2);

        List<Object> dbResult2 = queryResultList2.get(0);

        BigDecimal dbId2 = (BigDecimal) dbResult2.get(0);
        int dbIdInt2 = dbId2.intValue();

        List<Object> formattedDBResult2 = new ArrayList<>(dbResult2);

        formattedDBResult2.set(0, dbIdInt2);

        for (Object o : formattedDBResult2) {
            System.out.println(o);
        }

        Assert.assertEquals(formattedDBResult2, Arrays.asList(id, updatedNewUser.getDob(),
                updatedNewUser.getEmail(), updatedNewUser.getFirstName(),
                updatedNewUser.getLastName()));

        /* Retrieve a specific user created to confirm the update. */

        response = RestAssured.given()
                .spec(baseSpec)
                .when().get("/students/" + String.valueOf(id))
                .then().log().all()
                .assertThat().statusCode(200)
                .time(Matchers.lessThan(4000L))
                .extract().response();

        String query3 = "SELECT * FROM STUDENT WHERE id = " + id;

        List<List<Object>> queryResultList3 = DBUtil.getQueryResultList(query3);

        List<Object> dbResult3 = queryResultList3.get(0);

        BigDecimal dbId3 = (BigDecimal) dbResult3.get(0);
        int dbIdInt3 = dbId3.intValue();

        List<Object> formattedDBResult3 = new ArrayList<>(dbResult3);

        formattedDBResult3.set(0, dbIdInt3);

        for (Object o : formattedDBResult3) {
            System.out.println(o);
        }

        Assert.assertEquals(formattedDBResult3, Arrays.asList(id, updatedNewUser.getDob(),
                updatedNewUser.getEmail(), updatedNewUser.getFirstName(),
                updatedNewUser.getLastName()));

        /* Finally, delete the user that you created. */

        response = RestAssured.given()
                .spec(baseSpec)
                .when().delete("/students/")
                .then().log().all()
                .assertThat().statusCode(200)
                .extract().response();

        queryResultList3 = DBUtil.getQueryResultList(query3);

        Assert.assertTrue(queryResultList3.isEmpty(), " The student with id: " + id + " was not deleted from the database.");

    }
}
