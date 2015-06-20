package ro.sapientia2015.story.controller;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.view;
import javax.annotation.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ro.sapientia2015.common.controller.ErrorController;
import ro.sapientia2015.config.ExampleApplicationContext;
import ro.sapientia2015.context.WebContextLoader;
import ro.sapientia2015.story.StatusTestUtil;
import ro.sapientia2015.story.StoryTestUtil;
import ro.sapientia2015.story.dto.StatusDTO;
import ro.sapientia2015.story.dto.StoryDTO;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = WebContextLoader.class, classes = { ExampleApplicationContext.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
@DatabaseSetup("statusData.xml")
public class ITStatusControllerTest {

	private static final String FORM_FIELD_DESCRIPTION = "description";
	private static final String FORM_FIELD_ID = "id";
	private static final String FORM_FIELD_TITLE = "title";

	@Resource
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webApplicationContextSetup(
				webApplicationContext).build();
	}

	@Test
	@ExpectedDatabase("statusData.xml")
	public void showAddForm() throws Exception {
		mockMvc.perform(get("/status/add"))
				.andExpect(status().isOk())
				.andExpect(view().name(StatusController.VIEW_ADD))
				.andExpect(forwardedUrl("/WEB-INF/jsp/status/add.jsp"))
				.andExpect(
						model().attribute(StoryController.MODEL_ATTRIBUTE,
								hasProperty("id", nullValue())))
				.andExpect(
						model().attribute(
								StatusController.MODEL_ATTRIBUTE,
								hasProperty("description",
										isEmptyOrNullString())))
				.andExpect(
						model().attribute(StatusController.MODEL_ATTRIBUTE,
								hasProperty("title", isEmptyOrNullString())));
	}

	@Test
	@ExpectedDatabase("statusData.xml")
	public void addEmpty() throws Exception {
		mockMvc.perform(
				post("/status/add").contentType(
						MediaType.APPLICATION_FORM_URLENCODED).sessionAttr(
						StatusController.MODEL_ATTRIBUTE, new StatusDTO()))
				.andExpect(status().isOk())
				.andExpect(view().name(StatusController.VIEW_ADD))
				.andExpect(forwardedUrl("/WEB-INF/jsp/status/add.jsp"))
				.andExpect(
						model().attributeHasFieldErrors(
								StatusController.MODEL_ATTRIBUTE, "title"))
				.andExpect(
						model().attribute(StatusController.MODEL_ATTRIBUTE,
								hasProperty("id", nullValue())))
				.andExpect(
						model().attribute(
								StatusController.MODEL_ATTRIBUTE,
								hasProperty("description",
										isEmptyOrNullString())))
				.andExpect(
						model().attribute(StatusController.MODEL_ATTRIBUTE,
								hasProperty("title", isEmptyOrNullString())));
	}
	
	@Test
	@ExpectedDatabase(value = "statusData-add-expected.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void add() throws Exception {
		String expectedRedirectViewPath = StatusTestUtil
				.createRedirectViewPath(StoryController.REQUEST_MAPPING_VIEW);

		mockMvc.perform(
				post("/status/add")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param(FORM_FIELD_DESCRIPTION, "description")
						.param(FORM_FIELD_TITLE, "title")
						.sessionAttr(StatusController.MODEL_ATTRIBUTE,
								new StatusDTO()))
				.andExpect(status().isOk())
				.andExpect(view().name(expectedRedirectViewPath))
				.andExpect(
						model().attribute(StatusController.PARAMETER_ID, is("3")))
				.andExpect(
						flash().attribute(
								StatusController.FLASH_MESSAGE_KEY_FEEDBACK,
								is("Status entry: title was added.")));
	}

	@Test
	@ExpectedDatabase("statusData.xml")
	public void findAll() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(view().name(StoryController.VIEW_LIST))
				.andExpect(forwardedUrl("/WEB-INF/jsp/story/list.jsp"))
				.andExpect(
						model().attribute(StatusController.MODEL_ATTRIBUTE_LIST,
								hasSize(2)))
				.andExpect(
						model().attribute(
								StatusController.MODEL_ATTRIBUTE_LIST,
								hasItem(allOf(
										hasProperty("id", is(1L)),
										hasProperty("description",
												is("Lorem ipsum")),
										hasProperty("title", is("Foo"))))))
				.andExpect(
						model().attribute(
								StatusController.MODEL_ATTRIBUTE_LIST,
								hasItem(allOf(
										hasProperty("id", is(2L)),
										hasProperty("description",
												is("Lorem ipsum")),
										hasProperty("title", is("Bar"))))));
	}

	@Test
	@ExpectedDatabase("statusData.xml")
	public void findById() throws Exception {
		mockMvc.perform(get("/status/{id}", 1L))
				.andExpect(status().isOk())
				.andExpect(view().name(StatusController.VIEW_VIEW))
				.andExpect(forwardedUrl("/WEB-INF/jsp/status/view.jsp"))
				.andExpect(
						model().attribute(StatusController.MODEL_ATTRIBUTE,
								hasProperty("id", is(1L))))
				.andExpect(
						model().attribute(StatusController.MODEL_ATTRIBUTE,
								hasProperty("description", is("Lorem ipsum"))))
				.andExpect(
						model().attribute(StatusController.MODEL_ATTRIBUTE,
								hasProperty("title", is("Foo"))));
	}
	
	@Test
	@ExpectedDatabase("statusData-delete-expected.xml")
	public void deleteById() throws Exception {
		String expectedRedirectViewPath = StoryTestUtil
				.createRedirectViewPath(StoryController.REQUEST_MAPPING_LIST);
		mockMvc.perform(get("/status/delete/{id}", 2L))
				.andExpect(status().isOk())
				.andExpect(view().name(expectedRedirectViewPath))
				.andExpect(
						flash().attribute(
								StatusController.FLASH_MESSAGE_KEY_FEEDBACK,
								is("Story entry: Bar was deleted.")));
	}

	@Test
	@ExpectedDatabase("statusData.xml")
	public void showUpdateForm() throws Exception {
		mockMvc.perform(get("/status/update/{id}", 1L))
				.andExpect(status().isOk())
				.andExpect(view().name(StoryController.VIEW_UPDATE))
				.andExpect(forwardedUrl("/WEB-INF/jsp/status/update.jsp"))
				.andExpect(
						model().attribute(StoryController.MODEL_ATTRIBUTE,
								hasProperty("id", is(1L))))
				.andExpect(
						model().attribute(StoryController.MODEL_ATTRIBUTE,
								hasProperty("description", is("Lorem ipsum"))))
				.andExpect(
						model().attribute(StoryController.MODEL_ATTRIBUTE,
								hasProperty("title", is("Foo"))));
	}

	@Test
	@ExpectedDatabase("statusData.xml")
	public void showUpdateFormWhenIsNotFound() throws Exception {
		mockMvc.perform(get("/story/update/{id}", 3L))
				.andExpect(status().isNotFound())
				.andExpect(view().name(ErrorController.VIEW_NOT_FOUND))
				.andExpect(forwardedUrl("/WEB-INF/jsp/error/404.jsp"));
	}

	@Test
	@ExpectedDatabase("statusData.xml")
	public void updateEmpty() throws Exception {
		mockMvc.perform(
				post("/status/update")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param(FORM_FIELD_ID, "1")
						.sessionAttr(StatusController.MODEL_ATTRIBUTE,
								new StoryDTO()))
				.andExpect(status().isOk())
				.andExpect(view().name(StatusController.VIEW_UPDATE))
				.andExpect(forwardedUrl("/WEB-INF/jsp/status/update.jsp"))
				.andExpect(
						model().attributeHasFieldErrors(
								StatusController.MODEL_ATTRIBUTE, "title"))
				.andExpect(
						model().attribute(StatusController.MODEL_ATTRIBUTE,
								hasProperty("id", is(1L))))
				.andExpect(
						model().attribute(
								StatusController.MODEL_ATTRIBUTE,
								hasProperty("description",
										isEmptyOrNullString())))
				.andExpect(
						model().attribute(StatusController.MODEL_ATTRIBUTE,
								hasProperty("title", isEmptyOrNullString())));
	}

	@Test
	@ExpectedDatabase(value = "statusData-update-expected.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void update() throws Exception {
		String expectedRedirectViewPath = StatusTestUtil
				.createRedirectViewPath(StatusController.REQUEST_MAPPING_VIEW);

		mockMvc.perform(
				post("/status/update")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param(FORM_FIELD_DESCRIPTION, "description")
						.param(FORM_FIELD_ID, "1")
						.param(FORM_FIELD_TITLE, "title")
						.sessionAttr(StatusController.MODEL_ATTRIBUTE,
								new StoryDTO()))
				.andExpect(status().isOk())
				.andExpect(view().name(expectedRedirectViewPath))
				.andExpect(
						model().attribute(StatusController.PARAMETER_ID, is("1")))
				.andExpect(
						flash().attribute(
								StatusController.FLASH_MESSAGE_KEY_FEEDBACK,
								is("Story entry: title was updated.")));
	}

}
