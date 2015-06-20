package ro.sapientia2015.story.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ro.sapientia2015.story.model.Status;

public interface StatusRepository extends JpaRepository<Status, Long>{

}
