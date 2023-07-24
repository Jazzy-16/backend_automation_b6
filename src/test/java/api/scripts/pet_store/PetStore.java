package api.scripts.pet_store;

import api.pojo_classes.pet_store.AddPet;
import api.pojo_classes.pet_store.Category;
import api.pojo_classes.pet_store.Tags;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.ConfigReader;

import java.util.Arrays;

public class PetStore {

    Response response;
    private RequestSpecification baseSpec;


    @BeforeMethod
    public void setAPI() {

        baseSpec = new RequestSpecBuilder().log(LogDetail.ALL)
                .setBaseUri(ConfigReader.getProperty("PetStoreBaseURI"))
                .setContentType(ContentType.JSON)
                .build();
    }


    @Test
    public void petStoreAPI(){

        Category category = Category.builder()
                .id(10).name("horse").build();

        Tags tags = Tags.builder()
                .id(11).name("unicorn").build();

        AddPet addPet = AddPet.builder().id(10)
                .category(category).name("mustang")
                .photoUrls(Arrays.asList("Horse photo URL"))
                .tags(Arrays.asList(tags)).status("available").build();

        response = RestAssured.given()
                .spec(baseSpec)
                .body(addPet)
                .when().post("/v2/pet").then().log().all()
                .assertThat().statusCode(200)
                .extract().response();
    }
}