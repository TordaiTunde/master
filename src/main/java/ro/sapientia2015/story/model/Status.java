package ro.sapientia2015.story.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

import ro.sapientia2015.story.model.Story.Builder;

@Entity
@Table(name = "status")
public class Status {

	public static final int MAX_LENGTH_DESCRIPTION = 500;
	public static final int MAX_LENGTH_TITLE = 100;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "title", nullable = false, length = MAX_LENGTH_TITLE)
	private String title;

	@Column(name = "description", nullable = true, length = MAX_LENGTH_DESCRIPTION)
	private String description;

	public Status() {

	}

	public static Builder getBuilder(String title) {
		return new Builder(title);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void update(String description, String title) {
		this.description = description;
		this.title = title;
	}

	
	
	public static class Builder {

		private Status built;

		public Builder(String title) {
			built = new Status();
			built.setTitle(title);
		}

		public Builder() {
			built = new Status();
		}
		
		public Builder setStatus(String title) {
			this.built.title = title;
			return this;
		}

		public Status build() {
			return built;
		}

		public Builder description(String description) {
			built.description = description;
			return this;
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
