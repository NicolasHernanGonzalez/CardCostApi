package integration;

import com.cardcostapi.CardCostApiApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@AutoConfigureMockMvc
@SpringBootTest(classes = CardCostApiApplication.class)
public class ClearingCostIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createAndGetClearingCost() throws Exception {
        // Crear
        String payload = """
                {
                    "country": "AR",
                    "cost": 77.0
                }
                """;

        mockMvc.perform(post("/api/cost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.country").value("AR"))
                .andExpect(jsonPath("$.cost").value(77.0));

        // Consultar
        mockMvc.perform(get("/api/cost/AR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country").value("AR"))
                .andExpect(jsonPath("$.cost").value(77.0));
    }

}


