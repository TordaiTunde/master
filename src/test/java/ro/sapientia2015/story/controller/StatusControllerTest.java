package ro.sapientia2015.story.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import ro.sapientia2015.story.StatusTestUtil;
import ro.sapientia2015.story.StoryTestUtil;
import ro.sapientia2015.story.config.UnitTestContext;
import ro.sapientia2015.story.controller.StoryController;
import ro.sapientia2015.story.dto.StatusDTO;
import ro.sapientia2015.story.dto.StoryDTO;
import ro.sapientia2015.story.exception.NotFoundException;
import ro.sapientia2015.story.model.Status;
import ro.sapientia2015.story.model.Story;
import ro.sapientia2015.story.service.StatusService;
import ro.sapientia2015.story.service.StoryService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { UnitTestContext.class })
public class StatusControllerTest {

	private static final String FEEDBACK_MESSAGE = "feedbackMessage";
	private static final String FIELD_DESCRIPTION = "description";
	private static final String FIELD_TITLE = "title";

	private StatusController controller;

	private MessageSource messageSourceMock;

	private StatusService serviceMock;

	@Resource
	private Validator validator;

	@Before
	public void setUp() {
		controller = new StatusController();

		messageSourceMock = mock(MessageSource.class);
		ReflectionTestUtils.setField(controller, "messageSource",
				messageSourceMock);

		serviceMock = mock(StatusService.class);
		ReflectionTestUtils.setField(controller, "service", serviceMock);
	}

	@Test
	public void showAddSatusForm() {
		BindingAwareModelMap model = new BindingAwareModelMap();

		String view = controller.showForm(model);

		verifyZeroInteractions(messageSourceMock, serviceMock);
		assertEquals(StoryController.VIEW_ADD, view);

		StatusDTO formObject = (StatusDTO) model.asMap().get(
				StatusController.MODEL_ATTRIBUTE);

		assertNull(formObject.getId());
		assertNull(formObject.getDescription());
		assertNull(formObject.getTitle());
	}

	@Test
	public void add() {
		StatusDTO formObject = StatusTestUtil.createFormObject(null,
				StatusTestUtil.DESCRIPTION, StatusTestUtil.TITLE);

		Status model = StatusTestUtil.createModel(StatusTestUtil.ID,
				StatusTestUtil.DESCRIPTION, StatusTestUtil.TITLE);
		when(serviceMock.add(formObject)).thenReturn(model);

		MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST",
				"/status/add");
		BindingResult result = bindAndValidate(mockRequest, formObject);

		RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

		initMessageSourceForFeedbackMessage(StoryController.FEEDBACK_MESSAGE_KEY_ADDED);

		String view = controller.add(formObject, result, attributes);

		verify(serviceMock, times(1)).add(formObject);
		verifyNoMoreInteractions(serviceMock);

		String expectedView = StatusTestUtil
				.createRedirectViewPath(StatusController.REQUEST_MAPPING_VIEW);
		assertEquals(expectedView, view);

		assertEquals(Long.valueOf((String) attributes
				.get(StoryController.PARAMETER_ID)), model.getId());

		assertFeedbackMessage(attributes,
				StatusController.FEEDBACK_MESSAGE_KEY_ADDED);
	}

	@Test
	public void addEmptyStory() {
		StatusDTO formObject = StatusTestUtil.createFormObject(null, "", "");

		MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST",
				"/status/add");
		BindingResult result = bindAndValidate(mockRequest, formObject);

		RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

		String view = controller.add(formObject, result, attributes);

		verifyZeroInteractions(serviceMock, messageSourceMock);

		assertEquals(StatusController.VIEW_ADD, view);
		assertFieldErrors(result, FIELD_TITLE);
	}

	@Test
	public void addWithTooLongDescriptionAndTitle() {
		String description = StatusTestUtil
				.createStringWithLength(Story.MAX_LENGTH_DESCRIPTION + 1);
		String title = StatusTestUtil
				.createStringWithLength(Story.MAX_LENGTH_TITLE + 1);

		StatusDTO formObject = StatusTestUtil.createFormObject(null,
				description, title);

		MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST",
				"/status/add");
		BindingResult result = bindAndValidate(mockRequest, formObject);

		RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

		String view = controller.add(formObject, result, attributes);

		verifyZeroInteractions(serviceMock, messageSourceMock);

		assertEquals(StatusController.VIEW_ADD, view);
		assertFieldErrors(result, FIELD_DESCRIPTION, FIELD_TITLE);
	}

	@Test
	public void findById() throws NotFoundException {
		BindingAwareModelMap model = new BindingAwareModelMap();

		Status found = StatusTestUtil.createModel(StatusTestUtil.ID,
				StatusTestUtil.DESCRIPTION, StatusTestUtil.TITLE);
		when(serviceMock.findById(StatusTestUtil.ID)).thenReturn(found);

		String view = controller.findById(StatusTestUtil.ID, model);

		verify(serviceMock, times(1)).findById(StatusTestUtil.ID);
		verifyNoMoreInteractions(serviceMock);
		verifyZeroInteractions(messageSourceMock);

		assertEquals(StatusController.VIEW_VIEW, view);
		assertEquals(found, model.asMap().get(StatusController.MODEL_ATTRIBUTE));
	}

	@Test(expected = NotFoundException.class)
	public void findByIdWhenIsNotFound() throws NotFoundException {
		BindingAwareModelMap model = new BindingAwareModelMap();

		when(serviceMock.findById(StatusTestUtil.ID)).thenThrow(
				new NotFoundException(""));

		controller.findById(StatusTestUtil.ID, model);

		verify(serviceMock, times(1)).findById(StatusTestUtil.ID);
		verifyNoMoreInteractions(serviceMock);
		verifyZeroInteractions(messageSourceMock);
	}

	@Test
	public void showUpdateStoryForm() throws NotFoundException {
		BindingAwareModelMap model = new BindingAwareModelMap();

		Status updated = StatusTestUtil.createModel(StatusTestUtil.ID,
				StatusTestUtil.DESCRIPTION, StatusTestUtil.TITLE);
		when(serviceMock.findById(StatusTestUtil.ID)).thenReturn(updated);

		String view = controller.showUpdateForm(StatusTestUtil.ID, model);

		verify(serviceMock, times(1)).findById(StatusTestUtil.ID);
		verifyNoMoreInteractions(serviceMock);
		verifyZeroInteractions(messageSourceMock);

		assertEquals(StatusController.VIEW_UPDATE, view);

		StatusDTO formObject = (StatusDTO) model.asMap().get(
				StatusController.MODEL_ATTRIBUTE);

		assertEquals(updated.getId(), formObject.getId());
		assertEquals(updated.getDescription(), formObject.getDescription());
		assertEquals(updated.getTitle(), formObject.getTitle());
	}

	@Test(expected = NotFoundException.class)
	public void showUpdateStoryFormWhenIsNotFound() throws NotFoundException {
		BindingAwareModelMap model = new BindingAwareModelMap();

		when(serviceMock.findById(StatusTestUtil.ID)).thenThrow(
				new NotFoundException(""));

		controller.showUpdateForm(StatusTestUtil.ID, model);

		verify(serviceMock, times(1)).findById(StatusTestUtil.ID);
		verifyNoMoreInteractions(serviceMock);
		verifyZeroInteractions(messageSourceMock);
	}

	private void assertFeedbackMessage(RedirectAttributes attributes,
			String messageCode) {
		assertFlashMessages(attributes, messageCode,
				StoryController.FLASH_MESSAGE_KEY_FEEDBACK);
	}

	private void assertFieldErrors(BindingResult result, String... fieldNames) {
		assertEquals(fieldNames.length, result.getFieldErrorCount());
		for (String fieldName : fieldNames) {
			assertNotNull(result.getFieldError(fieldName));
		}
	}

	private void assertFlashMessages(RedirectAttributes attributes,
			String messageCode, String flashMessageParameterName) {
		Map<String, ?> flashMessages = attributes.getFlashAttributes();
		Object message = flashMessages.get(flashMessageParameterName);

		assertNotNull(message);
		flashMessages.remove(message);
		assertTrue(flashMessages.isEmpty());

		verify(messageSourceMock, times(1)).getMessage(eq(messageCode),
				any(Object[].class), any(Locale.class));
		verifyNoMoreInteractions(messageSourceMock);
	}

	private BindingResult bindAndValidate(HttpServletRequest request,
			Object formObject) {
		WebDataBinder binder = new WebDataBinder(formObject);
		binder.setValidator(validator);
		binder.bind(new MutablePropertyValues(request.getParameterMap()));
		binder.getValidator().validate(binder.getTarget(),
				binder.getBindingResult());
		return binder.getBindingResult();
	}

	private void initMessageSourceForFeedbackMessage(String feedbackMessageCode) {
		when(
				messageSourceMock.getMessage(eq(feedbackMessageCode),
						any(Object[].class), any(Locale.class))).thenReturn(
				FEEDBACK_MESSAGE);
	}
}
