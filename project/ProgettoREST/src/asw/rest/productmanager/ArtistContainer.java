package asw.rest.productmanager;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import javax.persistence.*;

import java.net.URI;

import java.util.*;
import javax.ejb.*;

// import javax.annotation.PostConstruct;

/*
 * L'annotazione @Stateless (e' un EJB) serve soprattutto a consentire
 * l'iniezione della dipendenza dell'entity manager (em).
 * In alternativa si potrebbe fare con Java CDI (@ApplicationScoped o @RequestScoped)
 * ma nessuno degli scopi previsti e' adeguato per l'iniezione dell'em.
 * Pertanto va bene @Stateless.
 */

@Stateless
@Path("/artists")
public class ArtistContainer {
	@Context
	private UriInfo uriInfo;

    @PersistenceContext(unitName="product-manager-pu")
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

    /* POST: Aggiunge un nuovo prodotto
     * sulla base di un form con campi id, description e price */
    @POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createArtist(
			@FormParam("id") int id,
			@FormParam("name") String name,
			@FormParam("country") String country) {
    	Artist p = new Artist(id, name, country);
		/* fa questa ricerca per evitare che venga sollevata un'eccezione al momento del commit */
    	Artist oldProduct = em.find(Artist.class, id);
		if (oldProduct==null) {
			try {
				em.persist(p);
	            return Response.created(URI.create("/" + id)).entity(p).build();
			} catch (Exception e) {
	    		String errorMessage = "Error while creating Product " + p.toString() + ": " + e.getMessage();
	    		throw new WebApplicationException(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				                        .entity(errorMessage).type("text/plain").build());
			}
		} else {
    		String errorMessage = "Error while creating Artist with id " + id + ": an artist with the same id already exists";
    		throw new WebApplicationException(
			Response.status(Response.Status.INTERNAL_SERVER_ERROR)
			                        .entity(errorMessage).type("text/plain").build());
		}
    }

    /* POST: Aggiunge un nuovo prodotto, passato con JSON o XML */
    @POST
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    public Response createArtist(Artist p) {
		int id = p.getId();
		/* fa questa ricerca per evitare che venga sollevata un'eccezione al momento del commit */
		Artist oldArtist = em.find(Artist.class, id);
		if (oldArtist==null) {
			try {
				em.persist(p);
	            return Response.created(URI.create("/" + id)).entity(p).build();
			} catch (Exception e) {
	    		String errorMessage = "Error while creating Artist " + p.toString() + ": " + e.getMessage();
	    		throw new WebApplicationException(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				                        .entity(errorMessage).type("text/plain").build());
			}
		} else {
    		String errorMessage = "Error while creating Artist with id " + id + ": an Artist with the same id already exists";
    		throw new WebApplicationException(
				Response.status(Response.Status.INTERNAL_SERVER_ERROR)
			                        .entity(errorMessage).type("text/plain").build());
		}
    }

}