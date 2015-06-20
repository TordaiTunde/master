package ro.sapientia2015.story.service;

import java.util.List;

import ro.sapientia2015.story.dto.StatusDTO;
import ro.sapientia2015.story.exception.NotFoundException;
import ro.sapientia2015.story.model.Status;

public interface StatusService {

	public Status add(StatusDTO added);

	public Status deleteById(Long id) throws NotFoundException;

	public List<Status> findAll();

	public Status findById(Long id) throws NotFoundException;

	public Status update(StatusDTO updated) throws NotFoundException;
}
