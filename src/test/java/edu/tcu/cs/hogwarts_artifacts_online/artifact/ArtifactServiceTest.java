package edu.tcu.cs.hogwarts_artifacts_online.artifact;

import edu.tcu.cs.hogwarts_artifacts_online.artifact.utils.IdWorker;
import edu.tcu.cs.hogwarts_artifacts_online.system.exception.ObjectNotFoundException;
import edu.tcu.cs.hogwarts_artifacts_online.wizard.Wizard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles(value = "dev")
class ArtifactServiceTest {

    @Mock
    ArtifactRepository artifactRepository;

    @Mock
    IdWorker idWorker;

    @InjectMocks
    ArtifactService artifactService;

    List<Artifact> artifacts;

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
    void testFindByIdSuccess() {
        //Given. Arrange inputs and Targets. Define the behavior of Mock object Artifact Repository
        Artifact a = new Artifact();
        a.setId("1250808601744904192");
        a.setName("Invisibility Cloak");
        a.setDescription("A cloak that turns wearer invisible when worn.");
        a.setImageURL("ImageURL");

        Wizard w = new Wizard();
        w.setId(2);
        w.setName("Harry Potter");

        a.setOwner(w);

        given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(a));

        //When. Act on the Target Behavior. When steps should cover the method to be tested.
        Artifact returnedArtifact = artifactService.findById("1250808601744904192");

        //Then. Assert expected outcomes.
        assertThat(returnedArtifact.getId()).isEqualTo(a.getId());
        assertThat(returnedArtifact.getName()).isEqualTo(a.getName());
        assertThat(returnedArtifact.getDescription()).isEqualTo(a.getDescription());
        assertThat(returnedArtifact.getImageURL()).isEqualTo(a.getImageURL());
        verify(artifactRepository, times(1)).findById("1250808601744904192");
    }

    @Test
    void testFindByIdNotFound(){
        //Given
        given(artifactRepository.findById(Mockito.any(String.class))).willReturn(Optional.empty());
        //When
        Throwable thrown = catchThrowable(() -> {
            Artifact returnedArtifact = artifactService.findById("1250808601744904192");
        });
        //Then
        assertThat(thrown).isInstanceOf(ObjectNotFoundException.class).hasMessage("Could not find artifact with Id 1250808601744904192 :(");
        verify(artifactRepository, times(1)).findById("1250808601744904192");
    }

    @Test
    void testFindAllSuccess(){
        //Given
        given(artifactRepository.findAll()).willReturn(this.artifacts);
        //When
        List<Artifact> actualArtifacts = artifactService.findAll();
        //Then
        assertThat(actualArtifacts.size()).isEqualTo(this.artifacts.size());
        verify(artifactRepository, times(1)).findAll();
    }

    @Test
    void testSaveSuccess(){
        //Given
        Artifact newArtifact = new Artifact();
        newArtifact.setName("Artifact 3");
        newArtifact.setDescription("New Description");
        newArtifact.setImageURL("ImageURL");

        given(idWorker.nextId()).willReturn(123456L);
        given(artifactRepository.save(newArtifact)).willReturn(newArtifact);
        //When
        Artifact savedArtifact = artifactService.save(newArtifact);
        //Then
        assertThat(savedArtifact.getId()).isEqualTo("123456");
        assertThat(savedArtifact.getName()).isEqualTo("Artifact 3");
        assertThat(savedArtifact.getDescription()).isEqualTo("New Description");
        assertThat(savedArtifact.getImageURL()).isEqualTo("ImageURL");
        verify(artifactRepository, times(1)).save(newArtifact);

    }

    @Test
    void testUpdateSuccess(){
        //Given
        Artifact oldArtifact = new Artifact();
        oldArtifact.setId("1250808601744904192");
        oldArtifact.setName("Invisibility Cloak");
        oldArtifact.setDescription("A cloak that turns wearer invisible when worn.");
        oldArtifact.setImageURL("ImageURL");

        Artifact update = new Artifact();
        update.setId("1250808601744904192");
        update.setName("Invisibility Cloak");
        update.setDescription("A new Description.");
        update.setImageURL("ImageURL");

        given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(oldArtifact));
        given(artifactRepository.save(oldArtifact)).willReturn(oldArtifact);
        //When
        Artifact updatedArtifact = artifactService.update("1250808601744904192",update);
        //Then
        assertThat(updatedArtifact.getId()).isEqualTo("1250808601744904192");
        assertThat(updatedArtifact.getDescription()).isEqualTo(update.getDescription());
        verify(artifactRepository, times(1)).findById("1250808601744904192");
        verify(artifactRepository, times(1)).save(oldArtifact);
    }

    @Test
    void testUpdateNotFound(){
        //Given
        Artifact update = new Artifact();
        update.setName("Invisibility Cloak");
        update.setDescription("A new Description.");
        update.setImageURL("ImageURL");

        given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.empty());

        //When
        assertThrows(ObjectNotFoundException.class, () ->{
            artifactService.update("1250808601744904192", update);
        });
        //Then
        verify(artifactRepository,times(1)).findById("1250808601744904192");
    }

    @Test
    void testDeleteSuccess(){
        //Given
        Artifact artifact = new Artifact();
        artifact.setId("1250808601744904192");
        artifact.setName("Invisibility Cloak");
        artifact.setDescription("A cloak that turns wearer invisible when worn.");
        artifact.setImageURL("ImageURL");

        given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(artifact));
        doNothing().when(artifactRepository).deleteById("1250808601744904192");
        //When
        artifactService.delete("1250808601744904192");
        //Then
        verify(artifactRepository,times(1)).deleteById("1250808601744904192");
    }

    @Test
    void testDeleteNotFound(){
        //Given
        given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.empty());
        //When
        assertThrows(ObjectNotFoundException.class, () ->{
            artifactService.delete("1250808601744904192");
        });
        //Then
        verify(artifactRepository,times(1)).findById("1250808601744904192");
    }

}