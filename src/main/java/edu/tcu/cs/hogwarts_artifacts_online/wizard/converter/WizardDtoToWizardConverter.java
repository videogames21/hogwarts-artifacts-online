package edu.tcu.cs.hogwarts_artifacts_online.wizard.converter;

import edu.tcu.cs.hogwarts_artifacts_online.wizard.Wizard;
import edu.tcu.cs.hogwarts_artifacts_online.wizard.dto.WizardDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class WizardDtoToWizardConverter implements Converter<WizardDto, Wizard>{

    @Override
    public Wizard convert(WizardDto source) {
        Wizard wizard = new Wizard();
        wizard.setName(source.name());
        wizard.setId(source.id());
        return wizard;
    }
}
