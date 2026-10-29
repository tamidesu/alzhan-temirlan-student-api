package kz.temirlan.studentapi.info;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicationInfoController.class)
class ApplicationInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BuildProperties buildProperties;

    @Test
    void infoReturnsOwnerDetailsAndVersionFromBuildMetadata() throws Exception {
        when(buildProperties.getVersion()).thenReturn("2.3.4");

        mockMvc.perform(get("/alzhan-temirlan/api/info").contextPath("/alzhan-temirlan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.owner").value("Temirlan Alzhan"))
                .andExpect(jsonPath("$.group").value("IT2-2307"))
                .andExpect(jsonPath("$.studentId").value("38372"))
                .andExpect(jsonPath("$.version").value("2.3.4"));
    }
}
