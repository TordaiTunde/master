package ro.sapientia2015.story.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ro.sapientia2015.story.dto.StatusDTO;
import ro.sapientia2015.story.exception.NotFoundException;
import ro.sapientia2015.story.model.Status;
import ro.sapientia2015.story.repository.StatusRepository;

@Service
public class RepositoryStatusService implements StatusService {

	@Resource
	private StatusRepository repository;

	@Transactional
	@Override
	public Status add(StatusDTO added) {
		Status model = Status.getBuilder(added.getTitle())
				.description(added.getDescription()).build();
		return repository.save(model);
	}

	@Transactional(rollbackFor = { NotFoundException.class })
	@Override
	public Status deleteById(Long id) throws NotFoundException {
		Status deleted = findById(id);
		repository.delete(deleted);
		return deleted;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Status> findAll() {
		return repository.findAll();
	}

	@Transactional(readOnly = true, rollbackFor = { NotFoundException.class })
	@Override
	public Status findById(Long id) throws NotFoundException {
		Status found = repository.findOne(id);
		if (found == null) {
			throw new NotFoundException("No entry found with id:" + id);
		}
		return found;
	}

	@Transactional(rollbackFor = { NotFoundException.class })
	@Override
	public Status update(StatusDTO updated) throws NotFoundException {
		Status model = findById(updated.getId());
		model.update(updated.getTitle(), updated.getDescription());
		return model;
	}
}
