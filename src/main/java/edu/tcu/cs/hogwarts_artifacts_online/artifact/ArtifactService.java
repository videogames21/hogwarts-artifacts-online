package edu.tcu.cs.hogwarts_artifacts_online.artifact;

import edu.tcu.cs.hogwarts_artifacts_online.artifact.utils.IdWorker;
import edu.tcu.cs.hogwarts_artifacts_online.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ArtifactService {

    private final ArtifactRepository artifactRepository;
    private final IdWorker idWorker;

    public ArtifactService(ArtifactRepository artifactRepository, IdWorker idWorker) {
        this.artifactRepository = artifactRepository;
        this.idWorker = idWorker;
    }

    public Artifact findById(String ArtifactId){
        return this.artifactRepository.findById(ArtifactId).orElseThrow(()->
            new ObjectNotFoundException("artifact",ArtifactId));
    }

    public List<Artifact> findAll(){
        return this.artifactRepository.findAll();
    }

    public Artifact save(Artifact newArtifact){
        newArtifact.setId(idWorker.nextId() + "");
        return this.artifactRepository.save(newArtifact);
    }

    public Artifact update(String artifactId, Artifact update){
        return this.artifactRepository.findById(artifactId)
                .map(oldArtifact ->{
                    oldArtifact.setName(update.getName());
                    oldArtifact.setDescription(update.getDescription());
                    oldArtifact.setImageURL(update.getImageURL());
                    return this.artifactRepository.save(oldArtifact);

                })
                .orElseThrow(() -> new ObjectNotFoundException("artifact",artifactId));
    }

    public void delete(String artifactId){
        this.artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ObjectNotFoundException("artifact",artifactId));
        this.artifactRepository.deleteById(artifactId);
    }


}
