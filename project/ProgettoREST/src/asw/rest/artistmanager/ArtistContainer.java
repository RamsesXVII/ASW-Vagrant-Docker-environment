package asw.rest.artistmanager;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import javax.persistence.*;

import java.net.URI;

import java.util.*;
import javax.ejb.*;

@Stateless
@Path("/artists")
public class ArtistContainer {
	@Context
	private UriInfo uriInfo;

	@PersistenceContext(unitName="artist-manager-pu")
	private EntityManager em;

	public ArtistContainer() { }

	/* GET: Restituisce la collezione di tutti gli artisti */
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
	public Collection<Artist> getArtists() {
		try {
			Collection<Artist> artists = em.createQuery("SELECT p FROM Artist p").getResultList();
			if (artists==null) {
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			} else {
				return artists;
			}
		} catch (Exception e) {
			String errorMessage = "Error while finding all artists: " + e.getMessage();
			throw new WebApplicationException(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(errorMessage).type("text/plain").build());
		}
	}

	/* POST: Aggiunge un nuovo artista*/
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createArtist(
			@FormParam("name") String name,
			@FormParam("country") String country) {
		Artist p =null;
		
		//Controllo che i campi name e country non siano ne vuoti ne nulli in modo da non persistere oggetti nulli
		if((name!=null && country!=null) && (!name.isEmpty() && !country.isEmpty())){
			
			try {
				p = new Artist(name, country);
				em.persist(p);
				return Response.created(URI.create("/" + p.getId())).entity(p).build();
			} catch (Exception e) {
				String errorMessage = "Error while creating Artist " + p.toString() + ": " + e.getMessage();
				throw new WebApplicationException(
						Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorMessage).type("text/plain").build());
			}
		}
		
		else{
			
			String errorMessage = "Name and Country are mandatory ";
			throw new WebApplicationException(
			Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorMessage).type("text/plain").build());
		}

	}
}