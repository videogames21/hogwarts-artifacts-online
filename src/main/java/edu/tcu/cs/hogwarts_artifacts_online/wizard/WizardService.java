package edu.tcu.cs.hogwarts_artifacts_online.wizard;

import edu.tcu.cs.hogwarts_artifacts_online.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class WizardService {

    private final WizardRepository wizardRepository;

    public WizardService(WizardRepository wizardRepository) {
        this.wizardRepository = wizardRepository;
    }

    public Wizard findById(Integer WizardId){
        return this.wizardRepository.findById(WizardId).orElseThrow(()->
                new ObjectNotFoundException("wizard",WizardId));

    }

    public List<Wizard> findAll(){
        return this.wizardRepository.findAll();
    }

    public Wizard save(Wizard newWizard){
        return this.wizardRepository.save(newWizard);
    }

    public Wizard update(Integer wizardId, Wizard update){
        return this.wizardRepository.findById(wizardId)
                .map(oldWizard ->{
                    oldWizard.setName(update.getName());
                    return this.wizardRepository.save(oldWizard);

                })
                .orElseThrow(() -> new ObjectNotFoundException("wizard",wizardId));
    }

    public void delete(Integer wizardId){
        Wizard deleted = this.wizardRepository.findById(wizardId)
                .orElseThrow(() -> new ObjectNotFoundException("wizard",wizardId));
        deleted.removeArtifacts();
        this.wizardRepository.deleteById(wizardId);
    }
}
