package edu.tcu.cs.hogwarts_artifacts_online.artifact;

import edu.tcu.cs.hogwarts_artifacts_online.artifact.dto.ArtifactDto;
import edu.tcu.cs.hogwarts_artifacts_online.system.StatusCode;
import edu.tcu.cs.hogwarts_artifacts_online.system.exception.ObjectNotFoundException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(value = "dev")
class ArtifactControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ArtifactService artifactService;

    @Autowired
    ObjectMapper objectMapper;

    List<Artifact> artifacts;

    @Value("${api.endpoint.base-url}")
    String baseURL;


    @BeforeEach
    void setUp() {
        this.artifacts = new ArrayList<>();

        Artifact a1 = new Artifact();
        a1.setId("1250808601744904191");
        a1.setName("Deluminator");
        a1.setDescription("Takes the light from the surrounding area and can place it elsewhere");
        a1.setImageURL("ImageURL");
        this.artifacts.add(a1);

        Artifact a2 = new Artifact();
        a2.setId("1250808601744904192");
        a2.setName("Invisibility Cloak");
        a2.setDescription("A cloak that turns wearer invisible when worn.");
        a2.setImageURL("ImageURL");
        this.artifacts.add(a2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindArtifactByIdSuccess() throws Exception{
        //Given
        given(this.artifactService.findById("1250808601744904191")).willReturn(this.artifacts.get(0));

        //When and Then
        this.mockMvc.perform(get(this.baseURL + "/artifacts/1250808601744904191").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Found One Success"))
                .andExpect(jsonPath("$.data.id").value("1250808601744904191"))
                .andExpect(jsonPath("$.data.name").value("Deluminator"));

    }

    @Test
    void testFindArtifactByIdNotFound() throws Exception{
        //Given
        given(this.artifactService.findById("1250808601744904191")).willThrow(new ObjectNotFoundException("artifact","1250808601744904191"));

        //When and Then
        this.mockMvc.perform(get(this.baseURL + "/artifacts/1250808601744904191").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1250808601744904191 :("))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    void testFindAllArtifactSuccess() throws Exception {
        //Given
        given(this.artifactService.findAll()).willReturn(this.artifacts);
        //When and Then
        this.mockMvc.perform(get(this.baseURL + "/artifacts").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(this.artifacts.size())))
                .andExpect(jsonPath("$.data[0].id").value("1250808601744904191"))
                .andExpect(jsonPath("$.data[0].name").value("Deluminator"))
                .andExpect(jsonPath("$.data[1].id").value("1250808601744904192"))
                .andExpect(jsonPath("$.data[1].name").value("Invisibility Cloak"));
    }

    @Test
    void testAddArtifactSuccess() throws Exception{
        //Given
        ArtifactDto artifactDto = new ArtifactDto(null,
                                            "Rememberance",
                                        "What was lost? Was it important?",
                                        "ImageURL",
                                            null);
        String json = this.objectMapper.writeValueAsString(artifactDto);

        Artifact savedArtifact = new Artifact();
        savedArtifact.setId("125008086010744904197");
        savedArtifact.setName("Rememberance");
        savedArtifact.setDescription("What was lost? Was it important?");
        savedArtifact.setImageURL("ImageURL");

        given(this.artifactService.save(Mockito.any(Artifact.class))).willReturn(savedArtifact);
        //When and Then
        this.mockMvc.perform(post(this.baseURL + "/artifacts").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value(savedArtifact.getName()))
                .andExpect(jsonPath("$.data.description").value(savedArtifact.getDescription()))
                .andExpect(jsonPath("$.data.imageURL").value(savedArtifact.getImageURL()));

    }

    @Test
    void testUpdateArtifactSuccess() throws Exception {
        //Given
        ArtifactDto artifactDto = new ArtifactDto("125008086010744904197",
                "Invisibility Cloak",
                "A new Description.",
                "ImageURL",
                null);
        String json = this.objectMapper.writeValueAsString(artifactDto);

        Artifact updatedArtifact = new Artifact();
        updatedArtifact.setId("125008086010744904197");
        updatedArtifact.setName("Invisibility Cloak");
        updatedArtifact.setDescription("A new Description.");
        updatedArtifact.setImageURL("ImageURL");

        given(this.artifactService.update(eq("125008086010744904197"),Mockito.any(Artifact.class))).willReturn(updatedArtifact);
        //When and Then
        this.mockMvc.perform(put(this.baseURL + "/artifacts/125008086010744904197").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value("125008086010744904197"))
                .andExpect(jsonPath("$.data.name").value(updatedArtifact.getName()))
                .andExpect(jsonPath("$.data.description").value(updatedArtifact.getDescription()))
                .andExpect(jsonPath("$.data.imageURL").value(updatedArtifact.getImageURL()));
    }

    @Test
    void testUpdateArtifactErrorWithNonExistantId() throws Exception {
        //Given
        ArtifactDto artifactDto = new ArtifactDto("125008086010744904197",
                "Invisibility Cloak",
                "A new Description.",
                "ImageURL",
                null);
        String json = this.objectMapper.writeValueAsString(artifactDto);

        given(this.artifactService.update(eq("125008086010744904197"),Mockito.any(Artifact.class))).willThrow(new ObjectNotFoundException("artifact","125008086010744904197"));
        //When and Then
        this.mockMvc.perform(put(this.baseURL + "/artifacts/125008086010744904197").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 125008086010744904197 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteArtifactSuccess() throws Exception {
        //Given
        doNothing().when(this.artifactService).delete("");
        //When and then
        this.mockMvc.perform(delete(this.baseURL + "/artifacts/125008086010744904197").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteArtifactErrorWithNonExistantId() throws Exception {
        //Given
        doThrow(new ObjectNotFoundException("artifact","125008086010744904197")).when(this.artifactService).delete("125008086010744904197");
        //When and then
        this.mockMvc.perform(delete(this.baseURL + "/artifacts/125008086010744904197").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 125008086010744904197 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

}