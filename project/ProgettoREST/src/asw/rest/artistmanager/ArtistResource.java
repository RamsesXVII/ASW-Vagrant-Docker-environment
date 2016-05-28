package asw.rest.artistmanager;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.persistence.*;
import javax.ejb.*;

@Stateless
@Path("/artist/{id}")
public class ArtistResource {
	@Context
	private UriInfo uriInfo;

	@PersistenceContext(unitName="artist-manager-pu")
	private EntityManager em;

	public ArtistResource() { }

	/* GET: Cerca un artista */
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
	public Artist getArtist(@PathParam("id") int id) {
		try {
			Artist p = em.find(Artist.class, id);
			if (p==null) {
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			} else {
				return p;
			}
		} catch (Exception e) {
			String errorMessage = "Error while finding Artist with id: " + id +  ": " + e.getMessage();
			throw new WebApplicationException(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(errorMessage).type("text/plain").build());
		}
	}

	/* PUT: Aggiorna un prodotto, passato con JSON o XML */
	@PUT
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
	public Response updateArtist(@PathParam("id") int id, Artist p) {
		/* fa questa ricerca per evitare che venga sollevata un'eccezione al momento del commit */
		Artist oldArtist = em.find(Artist.class, id);
		if (oldArtist==null) {
			String message = "The artist with ID "+id+" not exists";
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity(message).type("text/plain").build()) ;
		} else {
			try {
				em.merge(p);
				String message  = "The "+ p.getName()+ " is updated"; 
				return Response.ok(p).status(Response.Status.OK).entity(message).type("text/plain") .build();
			} catch (Exception e) {
				String errorMessage = "Error while updating Artist " + p.toString() + ": " + e.getMessage();
				throw new WebApplicationException(
						Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity(errorMessage).type("text/plain").build());
			}
		}
	}

	/* DELETE: Cancella un artista */
	@DELETE
	public Response deleteArtist(@PathParam("id") int id) {
		try {
			Artist artist = em.find(Artist.class, id);
			if (artist==null) {
				String message = "The artist with ID "+id+" not exists";
				throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity(message).type("text/plain").build()) ;
			} else {
				em.remove(artist);
				String message = "The "+ artist.getName()+" is deleted";
				return Response.ok(artist).status(Response.Status.OK).entity(message).type("text/plain").build();
			}
		} catch (Exception e) {
			String errorMessage = "Error while deleting Artist with id: " + id + ": " + e.getMessage();
			throw new WebApplicationException(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(errorMessage).type("text/plain").build());
		}
	}

}