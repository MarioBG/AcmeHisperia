
package domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Access(AccessType.PROPERTY)
@Table(indexes = {
	@Index(columnList = "govermentAgent_id")
})
public class Chirp extends DomainEntity {

	// Constructors

	public Chirp() {
		super();
	}


	// Attributes

	private String	title;
	private String	content;
	private Date	publicationDate;
	private String	image;
	private String	link;


	@NotBlank
	public String getTitle() {
		return this.title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	@NotBlank
	public String getContent() {
		return this.content;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	@Past
	@NotNull
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	public Date getPublicationDate() {
		return this.publicationDate;
	}

	public void setPublicationDate(final Date publicationDate) {
		this.publicationDate = publicationDate;
	}

	@URL
	public String getImage() {
		return this.image;
	}

	public void setImage(final String image) {
		this.image = image;
	}

	@URL
	public String getLink() {
		return this.link;
	}

	public void setLink(final String link) {
		this.link = link;
	}


	// Relationships

	private GovernmentAgent	governmentAgent;


	@Valid
	@NotNull
	@ManyToOne(optional = false)
	public GovernmentAgent getGovernmentAgent() {
		return this.governmentAgent;
	}

	public void setGovernmentAgent(final GovernmentAgent governmentAgent) {
		this.governmentAgent = governmentAgent;
	}

}
