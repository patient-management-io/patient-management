import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatientIntegrationTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:4004";
    }

    @Test
    public void shouldReturnPatientsWithValidToken() {
        String token = getToken();

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/patients")
                .then()
                .statusCode(200)
                .body("patients", notNullValue());
    }

    @Test
    public void shouldReturn429AfterLimitExceeded() throws InterruptedException {
        String token = getToken();
        int total = 15;
        int tooManyRequests = 0;

        System.out.println("Starting rate limit test...");

        for (int i = 1; i <= total; i++) {
            Response response = given()
                    .header("Authorization", "Bearer " + token)
                    .get("/api/patients");

            System.out.printf("Request %d -> Status: %d%n", i, response.statusCode());

            if (response.statusCode() == 429) {
                tooManyRequests++;
            }

            Thread.sleep(10);
        }

        System.out.printf("Total 429 responses: %d out of %d requests%n", tooManyRequests, total);
        assertTrue(tooManyRequests > 0,
                String.format("Expected at least one 429 response, but got %d out of %d requests", tooManyRequests, total));
    }

    private static String getToken() {
        String loginPayload = """
                    {
                        "email": "testuser@test.com",
                        "password": "password123"
                    }
                """;

        return given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("token");
    }
}
