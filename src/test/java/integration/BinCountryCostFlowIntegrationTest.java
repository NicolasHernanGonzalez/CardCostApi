package integration;

import com.cardcostapi.CardCostApiApplication;
import com.cardcostapi.services.IBinLookupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = CardCostApiApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class BinCountryCostFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBinLookupService binLookupService;

    @Test
    public void AllClearingCostFlow_Success() throws Exception {

        //Setup
        String payload = """
            {
              "country": "BR",
              "cost": 123.45
            }
        """;

        //Sut && Assert
        mockMvc.perform(post("/api/cost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());

        //Mock
        when(binLookupService.getCountryByBin("12345678")).thenReturn("BR");

        String panPayload = """
            {
              "card_number": "1234567890123456"
            }
        """;

        //Sut && Assert
        mockMvc.perform(post("/payment-cards-cost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(panPayload))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country").value("BR"))
                .andExpect(jsonPath("$.cost").value(123.45));

        verify(binLookupService).getCountryByBin("12345678");
    }

    @Test
    public void AllClearingCostFlow_DefaultCountry() throws Exception {

        //Setup
        String payload = """
            {
              "country": "Others",
              "cost": 77
            }
        """;

        //Sut && Assert
        mockMvc.perform(post("/api/cost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());

        //Mock
        when(binLookupService.getCountryByBin("45717562")).thenReturn("AR");

        String panPayload = """
            {
              "card_number": "457175621111111"
            }
        """;

        //Sut
        mockMvc.perform(post("/payment-cards-cost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(panPayload))
        //Verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country").value("Others"))
                .andExpect(jsonPath("$.cost").value(77));

        verify(binLookupService).getCountryByBin("45717562");
    }


}
