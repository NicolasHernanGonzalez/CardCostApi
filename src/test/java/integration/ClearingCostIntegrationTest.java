package integration;

import com.cardcostapi.CardCostApiApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@AutoConfigureMockMvc
@SpringBootTest(classes = CardCostApiApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class ClearingCostIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    //@Test
    public void createAndGetClearingCost() throws Exception {
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

        mockMvc.perform(get("/api/cost/AR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country").value("AR"))
                .andExpect(jsonPath("$.cost").value(77.0));
    }
    @Test
    public void updateClearingCost() throws Exception {
        String createPayload = """
        {
            "country": "BR",
            "cost": 50.0
        }
    """;

        mockMvc.perform(post("/api/cost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isCreated());


        Long id = 1L;
        String updatePayload = """
        {
            "country": "BR",
            "cost": 99.9
        }
    """;

        mockMvc.perform(put("/api/cost/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country").value("BR"))
                .andExpect(jsonPath("$.cost").value(99.9));
    }

    @Test
    @Transactional
    public void deleteClearingCost() throws Exception {
        String payload = """
        {
            "country": "UY",
            "cost": 33.3
        }
        """;

        mockMvc.perform(post("/api/cost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/cost/UY"))
                .andExpect(status().isNoContent());

        // Verificar que ya no existe
        mockMvc.perform(get("/api/cost/UY"))
                .andExpect(status().isNotFound());
    }


}