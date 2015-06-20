package ro.sapientia2015.story.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import ro.sapientia2015.story.StatusTestUtil;
import ro.sapientia2015.story.StoryTestUtil;
import ro.sapientia2015.story.dto.StatusDTO;
import ro.sapientia2015.story.dto.StoryDTO;
import ro.sapientia2015.story.exception.NotFoundException;
import ro.sapientia2015.story.model.Status;
import ro.sapientia2015.story.model.Story;
import ro.sapientia2015.story.repository.StatusRepository;
import ro.sapientia2015.story.repository.StoryRepository;
import ro.sapientia2015.story.service.RepositoryStoryService;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.*;

public class RepositoryStatusServiceTest {

    private RepositoryStatusService service;
    private StatusRepository repositoryMock;

    @Before
    public void setUp() {
        service = new RepositoryStatusService();

        repositoryMock = mock(StatusRepository.class);
        ReflectionTestUtils.setField(service, "repository", repositoryMock);
    }

    @Test
    public void add() {
        StatusDTO dto = StatusTestUtil.createFormObject(null, StatusTestUtil.DESCRIPTION, StatusTestUtil.TITLE);

        service.add(dto);

        ArgumentCaptor<Status> statusArgument = ArgumentCaptor.forClass(Status.class);
        verify(repositoryMock, times(1)).save(statusArgument.capture());
        verifyNoMoreInteractions(repositoryMock);

        Status model = statusArgument.getValue();

        assertNull(model.getId());
        assertEquals(dto.getDescription(), model.getDescription());
        assertEquals(dto.getTitle(), model.getTitle());
    }

    @Test
    public void deleteById() throws NotFoundException {
        Status model = StatusTestUtil.createModel(StatusTestUtil.ID, StatusTestUtil.DESCRIPTION, StatusTestUtil.TITLE);
        when(repositoryMock.findOne(StatusTestUtil.ID)).thenReturn(model);

        Status actual = service.deleteById(StatusTestUtil.ID);

        verify(repositoryMock, times(1)).findOne(StatusTestUtil.ID);
        verify(repositoryMock, times(1)).delete(model);
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(model, actual);
    }

    @Test(expected = NotFoundException.class)
    public void deleteByIdWhenIsNotFound() throws NotFoundException {
        when(repositoryMock.findOne(StatusTestUtil.ID)).thenReturn(null);

        service.deleteById(StatusTestUtil.ID);

        verify(repositoryMock, times(1)).findOne(StatusTestUtil.ID);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    public void findAll() {
        List<Status> models = new ArrayList<Status>();
        when(repositoryMock.findAll()).thenReturn(models);

        List<Status> actual = service.findAll();

        verify(repositoryMock, times(1)).findAll();
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(models, actual);
    }

    @Test
    public void findById() throws NotFoundException {
        Status model = StatusTestUtil.createModel(StatusTestUtil.ID, StatusTestUtil.DESCRIPTION, StatusTestUtil.TITLE);
        when(repositoryMock.findOne(StatusTestUtil.ID)).thenReturn(model);

        Status actual = service.findById(StatusTestUtil.ID);

        verify(repositoryMock, times(1)).findOne(StatusTestUtil.ID);
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(model, actual);
    }

    @Test(expected = NotFoundException.class)
    public void findByIdWhenIsNotFound() throws NotFoundException {
        when(repositoryMock.findOne(StatusTestUtil.ID)).thenReturn(null);

        service.findById(StatusTestUtil.ID);

        verify(repositoryMock, times(1)).findOne(StatusTestUtil.ID);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    public void update() throws NotFoundException {
        StatusDTO dto = StatusTestUtil.createFormObject(StatusTestUtil.ID, StatusTestUtil.DESCRIPTION_UPDATED, StatusTestUtil.TITLE_UPDATED);
        Status model = StatusTestUtil.createModel(StatusTestUtil.ID, StatusTestUtil.DESCRIPTION, StatusTestUtil.TITLE);
        when(repositoryMock.findOne(dto.getId())).thenReturn(model);

        Status actual = service.update(dto);

        verify(repositoryMock, times(1)).findOne(dto.getId());
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(dto.getId(), actual.getId());
        assertEquals(dto.getDescription(), actual.getDescription());
        assertEquals(dto.getTitle(), actual.getTitle());
    }

    @Test(expected = NotFoundException.class)
    public void updateWhenIsNotFound() throws NotFoundException {
        StatusDTO dto = StatusTestUtil.createFormObject(StatusTestUtil.ID, StatusTestUtil.DESCRIPTION_UPDATED, StatusTestUtil.TITLE_UPDATED);
        when(repositoryMock.findOne(dto.getId())).thenReturn(null);

        service.update(dto);

        verify(repositoryMock, times(1)).findOne(dto.getId());
        verifyNoMoreInteractions(repositoryMock);
    }
}
