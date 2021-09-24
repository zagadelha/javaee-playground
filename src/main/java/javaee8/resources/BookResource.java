package javaee8.resources;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import javaee8.dto.BookDTO;
import javaee8.entity.BookEntity;
import javaee8.service.BookService;

/**
 *
 * @author
 */
@Path("/livros")
public class BookResource {

	@Inject
	private BookService service;

	@GET
	public Response list() {
		List<BookEntity> livros = service.listAll();
		return Response.ok(livros).build();
	}

	@GET
	@Path("/{bookId}")
	public Response get() {
		return Response.ok("ping").build();
	}

	@POST
	public Response add(BookDTO dto) {

		BookEntity book = service.add(dto);
		if (book == null) {
			// add warn message
			// return
		}
		// add sucess message
		return Response.ok("Add success").build();
	}

	@PUT
	public Response update() {
		return Response.ok("ping").build();
	}

	@DELETE
	public Response delete() {
		return Response.ok("ping").build();
	}

}
