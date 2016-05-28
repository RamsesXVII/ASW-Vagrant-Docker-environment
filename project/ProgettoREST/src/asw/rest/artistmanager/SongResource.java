package asw.rest.artistmanager;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.persistence.*;


import javax.ejb.*;

@Stateless
@Path("/song/{id}")
public class SongResource {
	@Context
	private UriInfo uriInfo;

	@PersistenceContext(unitName="artist-manager-pu")
	private EntityManager em;

	public SongResource() { }

	/* GET: Cerca una canzone */
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
	public Song getSong(@PathParam("id") int id) {
		try {
			Song song = em.find(Song.class, id);
			if (song==null) {
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			} else {
				return song;
			}
		} catch (Exception e) {
			String errorMessage = "Error while finding Song with id: " + id +  ": " + e.getMessage();
			throw new WebApplicationException(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(errorMessage).type("text/plain").build());
		}
	}

	//TODO

	/* PUT: Aggiorna una canzone, passata con JSON o XML */
	@PUT
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
	public Response updateSong(@PathParam("id") int id, Song song) {
		/* fa questa ricerca per evitare che venga sollevata un'eccezione al momento del commit */
		Song oldSong = em.find(Song.class, id);
		if (oldSong==null) {
			String message = "The song with ID "+id+" not exists";
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity(message).type("text/plain").build()) ;
		} else {
			try {
				em.merge(song);
				String message  = "The "+ song.getName()+ " is updated"; 
				return Response.ok(song).status(Response.Status.OK).entity(message).type("text/plain") .build();
			} catch (Exception e) {
				String errorMessage = "Error while updating Song " + song.toString() + ": " + e.getMessage();
				throw new WebApplicationException(
						Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity(errorMessage).type("text/plain").build());
			}
		}
	}

	/* DELETE: Cancella una canzone */
	@DELETE
	public Response deleteSong(@PathParam("id") int id) {
		try {
			Song song = em.find(Song.class, id);
			if (song==null) {
				String message = "The song with ID "+id+" not exists";
				throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity(message).type("text/plain").build()) ;
			} else {
				em.remove(song);
				String message = "The "+ song.getName()+" is deleted";
				return Response.ok(song).status(Response.Status.OK).entity(message).type("text/plain").build();
			}
		} catch (Exception e) {
			String errorMessage = "Error while deleting Song with id: " + id + ": " + e.getMessage();
			throw new WebApplicationException(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(errorMessage).type("text/plain").build());
		}
	}

}