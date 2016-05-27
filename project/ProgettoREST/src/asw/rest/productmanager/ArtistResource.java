package asw.rest.productmanager;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.persistence.*;
import javax.transaction.*;

import java.net.URI;

import javax.servlet.http.HttpServletResponse;

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
@Path("/artist/{id}")
public class ArtistResource {
	@Context
	private UriInfo uriInfo;

    @PersistenceContext(unitName="product-manager-pu")
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
    		String errorMessage = "Error while finding Product with id: " + id +  ": " + e.getMessage();
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
    	Artist oldProduct = em.find(Artist.class, id);
		if (oldProduct==null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} else {
			try {
				em.merge(p);
				return Response.ok(p).status(Response.Status.OK).build();
			} catch (Exception e) {
	    		String errorMessage = "Error while updating Product " + p.toString() + ": " + e.getMessage();
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
			Artist product = em.find(Artist.class, id);
	    	if (product==null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
			} else {
				em.remove(product);
				return Response.ok(product).status(Response.Status.OK).build();
	    	}
		} catch (Exception e) {
    		String errorMessage = "Error while deleting Product with id: " + id + ": " + e.getMessage();
    		throw new WebApplicationException(
				Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				                        .entity(errorMessage).type("text/plain").build());
		}
    }

}