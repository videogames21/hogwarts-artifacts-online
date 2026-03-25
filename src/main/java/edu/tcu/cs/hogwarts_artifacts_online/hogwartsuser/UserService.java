package edu.tcu.cs.hogwarts_artifacts_online.hogwartsuser;

import edu.tcu.cs.hogwarts_artifacts_online.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<HogwartsUser> findAll(){
        return this.userRepository.findAll();
    }

    public HogwartsUser findById(Integer userId){
        return this.userRepository.findById(userId).orElseThrow(()->
                new ObjectNotFoundException("user",userId));
    }

    public HogwartsUser save(HogwartsUser newHogwartsUser){
        return this.userRepository.save(newHogwartsUser);
    }

    public HogwartsUser update(Integer userId, HogwartsUser update){
        return this.userRepository.findById(userId)
                .map(oldHogwartsUser ->{
                    oldHogwartsUser.setUsername(update.getUsername());
                    oldHogwartsUser.setEnabled(update.isEnabled());
                    oldHogwartsUser.setRoles(update.getRoles());
                    return this.userRepository.save(oldHogwartsUser);

                })
                .orElseThrow(() -> new ObjectNotFoundException("user",userId));
    }

    public void delete(Integer userId){
        this.userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user",userId));
        this.userRepository.deleteById(userId);
    }
}
