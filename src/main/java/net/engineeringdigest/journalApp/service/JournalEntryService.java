package net.engineeringdigest.journalApp.service;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class JournalEntryService  {

    @Autowired
    private JournalEntryRepository JournalEntryRepository;

    @Autowired
    private UserService userService;




    @Transactional
    public void saveEntry(JournalEntry JournalEntry, String userName){
        try {
            JournalEntry.setDate(LocalDateTime.now());
            User user = userService.findByUserName(userName);
            JournalEntry saved = JournalEntryRepository.save(JournalEntry);
            user.getJournalEntries().add(saved);
            userService.saveEntry(user);
        }
        catch(Exception e){

            throw new RuntimeException("an error occurred while saving the entry.",e);
        }
    }

    public void saveEntry(JournalEntry JournalEntry){

        JournalEntryRepository.save(JournalEntry);
    }

    public List<JournalEntry> getAll(){
         return JournalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId id){
        return JournalEntryRepository.findById(id);
    }

    @Transactional
    public boolean deleteById(ObjectId id, String userName){
        boolean removed = false;
        try {
            User user = userService.findByUserName(userName);
            removed = user.getJournalEntries().removeIf(x -> x.getId().equals(id));
            if (removed) {
                userService.saveEntry(user);
                JournalEntryRepository.deleteById(id);
            }

        } catch (Exception e) {
            log.error("ERROR ",e);
            throw new RuntimeException("An error occurred while deleting the entry." , e);
        }
        return removed;

    }
}
