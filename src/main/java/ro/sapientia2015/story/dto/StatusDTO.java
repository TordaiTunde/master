package ro.sapientia2015.story.dto;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import ro.sapientia2015.story.model.Status;
import ro.sapientia2015.story.model.Status;
import ro.sapientia2015.story.model.Story;

public class StatusDTO {

	private Long id;
	
	@Length(max = Status.MAX_LENGTH_DESCRIPTION)
	private String description;

	@NotEmpty
	@Length(max = Status.MAX_LENGTH_TITLE)
	private String title;

	private Status.Builder builder = new Status.Builder();

	public StatusDTO() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Status.Builder getBuilder() {
		return builder;
	}

	public void setBuilder(Status.Builder builder) {
		this.builder = builder;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
