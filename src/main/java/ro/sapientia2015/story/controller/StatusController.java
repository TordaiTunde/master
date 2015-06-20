package ro.sapientia2015.story.controller;

import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ro.sapientia2015.story.dto.SprintDTO;
import ro.sapientia2015.story.dto.StatusDTO;
import ro.sapientia2015.story.dto.StoryDTO;
import ro.sapientia2015.story.exception.NotFoundException;
import ro.sapientia2015.story.model.Sprint;
import ro.sapientia2015.story.model.Status;
import ro.sapientia2015.story.model.Story;
import ro.sapientia2015.story.service.StatusService;

@Controller
public class StatusController {

	@Resource
	private StatusService service;

	public static final String VIEW_LIST = "status/list";
	public static final String VIEW_ADD = "status/add";
	public static final String VIEW_VIEW = "status/view";
	public static final String VIEW_UPDATE = "status/update";

	public static final String MODEL_ATTRIBUTE = "status";
	public static final String MODEL_ATTRIBUTE_LIST = "statusList";
	protected static final String FEEDBACK_MESSAGE_KEY_UPDATED = "feedback.message.status.updated";
	protected static final String FEEDBACK_MESSAGE_KEY_ADDED = "feedback.message.status.added";
	protected static final String FEEDBACK_MESSAGE_KEY_DELETED = "feedback.message.status.deleted";

	protected static final String REQUEST_MAPPING_VIEW = "/status/{id}";

	protected static final String FLASH_MESSAGE_KEY_ERROR = "errorMessage";
	protected static final String FLASH_MESSAGE_KEY_FEEDBACK = "feedbackMessage";

	protected static final String PARAMETER_ID = "id";

	// LIST
	@RequestMapping(value = "/status/list", method = RequestMethod.GET)
	public String listStatuses(Model model) {

		List<Status> statuses = service.findAll();
		model.addAttribute("statuses", statuses);
		return VIEW_LIST;
	}

	private String createRedirectViewPath(String requestMapping) {
		StringBuilder redirectViewPath = new StringBuilder();
		redirectViewPath.append("redirect:");
		redirectViewPath.append(requestMapping);
		return redirectViewPath.toString();
	}

	// ADD
	@RequestMapping(value = "/status/add", method = RequestMethod.GET)
	public String showForm(Model model) {

		StatusDTO status = new StatusDTO();
		model.addAttribute("status", status);
		return VIEW_ADD;
	}

	@RequestMapping(value = "/status/add", method = RequestMethod.POST)
	public String add(@Valid @ModelAttribute(MODEL_ATTRIBUTE) StatusDTO dto,
			BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return VIEW_ADD;
		}
		service.add(dto);
		return createRedirectViewPath("/status/list");
	}

	//VIEW
	@RequestMapping(value = REQUEST_MAPPING_VIEW, method = RequestMethod.GET)
    public String findById(@PathVariable("id") Long id, Model model) throws NotFoundException {
        Status found = service.findById(id);
        model.addAttribute(MODEL_ATTRIBUTE, found);
        return VIEW_VIEW;
    }
	
	
	// UPDATE

	private StatusDTO constructFormObjectForUpdateForm(Status updated) {
		StatusDTO dto = new StatusDTO();

		dto.setId(updated.getId());
		dto.setDescription(updated.getDescription());
		dto.setTitle(updated.getTitle());

		return dto;
	}

	@RequestMapping(value = "/status/update/{id}", method = RequestMethod.GET)
	public String showUpdateForm(@PathVariable("id") Long id, Model model)
			throws NotFoundException {
		Status updated = service.findById(id);
		StatusDTO formObject = constructFormObjectForUpdateForm(updated);
		model.addAttribute(MODEL_ATTRIBUTE, formObject);

		return VIEW_UPDATE;
	}

	@RequestMapping(value = "/status/update", method = RequestMethod.POST)
	public String update(@Valid @ModelAttribute(MODEL_ATTRIBUTE) StatusDTO dto,
			BindingResult result, RedirectAttributes attributes)
			throws NotFoundException {
		if (result.hasErrors()) {
			return VIEW_UPDATE;
		}

		Status updated = service.update(dto);
		addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_UPDATED,
				updated.getTitle());
		attributes.addAttribute(PARAMETER_ID, updated.getId());

		return createRedirectViewPath(REQUEST_MAPPING_VIEW);
	}

	private void addFeedbackMessage(RedirectAttributes attributes,
			String messageCode, Object... messageParameters) {
		String localizedFeedbackMessage = getMessage(messageCode,
				messageParameters);
		attributes.addFlashAttribute(FLASH_MESSAGE_KEY_FEEDBACK,
				localizedFeedbackMessage);
	}

	private String getMessage(String messageCode, Object... messageParameters) {
		Locale current = LocaleContextHolder.getLocale();
		return "message";
//				messageSource
//				.getMessage(messageCode, messageParameters, current);
	}
}
