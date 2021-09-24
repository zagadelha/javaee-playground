package javaee8.dto;

import javaee8.entity.BookEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookDTO {

//	private long id;
//	private String name;
//
//	public long getId() {
//		return id;
//	}
//
//	public void setId(long id) {
//		this.id = id;
//	}
//
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}

	public static BookEntity getEntity(BookDTO dto) {
		BookEntity book = new BookEntity();
//		book.setname(dto.getName());
		return book;
	}

}
