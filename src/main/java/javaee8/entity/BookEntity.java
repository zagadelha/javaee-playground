package javaee8.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "book")
public class BookEntity extends AbstractEntity {

	private static final long serialVersionUID = 7804394455738327828L;

	@Id
	@GeneratedValue
	private Long id;
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	private AuthorEntity author;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AuthorEntity getAuthor() {
		return author;
	}

	public void setAuthor(AuthorEntity author) {
		this.author = author;
	}

}
