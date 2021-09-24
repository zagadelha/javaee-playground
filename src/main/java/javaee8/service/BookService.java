package javaee8.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import javaee8.dto.BookDTO;
import javaee8.entity.BookEntity;
import javaee8.repository.BookRepository;

@Stateless
public class BookService {

	@Inject
	private BookRepository repository;

	public BookEntity add(BookDTO dto) {
		BookEntity book = BookDTO.getEntity(dto);
		return repository.save(book);
	}

	public List<BookEntity> listAll() {
		return repository.findAll();
	}

}
