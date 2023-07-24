package tg_application.scripts;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import static org.hamcrest.Matchers.*;

import org.hamcrest.Matchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tg_application.pojo_classes.CreateNewUser;
import tg_application.pojo_classes.PatchUser;
import tg_application.pojo_classes.UpdatedNewUser;
import utils.ConfigReader;

public class TG_Automation {
    RequestSpecification baseSpec;
    //ObjectMapper objectMapper = new ObjectMapper();
    Response response;
    Faker faker = new Faker();

    String firstName;
    String lastName;
    String email;
    String dob;

    @BeforeMethod
    public void setAPI(){

        baseSpec = new RequestSpecBuilder().log(LogDetail.ALL)
                .setBaseUri(ConfigReader.getProperty("TG_Application"))
                .setContentType(ContentType.JSON)
                .build();

    }

    @Test
    public void tg_applicationAPI(){
        /* Retrieve a list of all users*/

        response = RestAssured.given()
                .spec(baseSpec)
                .when().get()
                .then().log().all()
                .extract().response();

        /* Create a new user */

        CreateNewUser createNewUser = CreateNewUser.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .dob("1999-07-27")
                .build();

        response = RestAssured.given()
                .spec(baseSpec)
                .body(createNewUser)
                .when().post()
                .then().log().all()
                .assertThat().statusCode(200)
                .time(Matchers.lessThan(4000L))
                .body("firstName", equalTo(createNewUser.getFirstName()))
                .body("lastName", equalTo(createNewUser.getLastName()))
                .body("email", equalTo(createNewUser.getEmail()))
                .body("dob", equalTo(createNewUser.getDob()))
                .extract().response();

        int user_id = response.jsonPath().getInt("id");

        /* Retrieve a specific user-created */

        response = RestAssured.given()
                .spec(baseSpec)
                .when().get(String.valueOf(user_id))
                .then().log().all()
                .assertThat().statusCode(200)
                .time(Matchers.lessThan(4000L))
                .body("firstName", equalTo(createNewUser.getFirstName()))
                .body("lastName", equalTo(createNewUser.getLastName()))
                .body("email", equalTo(createNewUser.getEmail()))
                .body("dob", equalTo(createNewUser.getDob()))
                .extract().response();

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
                .when().put(String.valueOf(user_id))
                .then().log().all()
                .assertThat().statusCode(200)
                .time(Matchers.lessThan(4000L))
                .body("firstName", equalTo(updatedNewUser.getFirstName()))
                .body("lastName", equalTo(updatedNewUser.getLastName()))
                .body("email", equalTo(updatedNewUser.getEmail()))
                .body("dob", equalTo(updatedNewUser.getDob()))
                .extract().response();

        /* Partially update an existing User */

        PatchUser patchUser = PatchUser.builder()
                .email(faker.internet().emailAddress())
                .dob("2001-06-06")
                .build();
        response = RestAssured.given()
                .spec(baseSpec)
                .body(patchUser)
                .when().patch(String.valueOf(user_id))
                .then().log().all()
                .assertThat().statusCode(200)
                .time(Matchers.lessThan(4000L))
                .body("email", equalTo(patchUser.getEmail()))
                .body("dob", equalTo(patchUser.getDob()))
                .extract().response();

        /* Retrieve a list of all users again */

        response = RestAssured.given()
                .spec(baseSpec)
                .when().get()
                .then().log().all()
                .extract().response();
        /* Retrieve a specific user created to confirm the update. */

        response = RestAssured.given()
                .spec(baseSpec)
                .when().get(String.valueOf(user_id))
                .then().log().all()
                .assertThat().statusCode(200)
                .time(Matchers.lessThan(4000L))
                .body("firstName", equalTo(updatedNewUser.getFirstName()))
                .body("lastName", equalTo(updatedNewUser.getLastName()))
                .body("email", equalTo(patchUser.getEmail()))
                .body("dob", equalTo(patchUser.getDob()))
                .extract().response();

        /* Finally, delete the user that you created. */

        response = RestAssured.given()
                .spec(baseSpec)
                .when().delete()
                .then().log().all()
                .extract().response();

    }
}
