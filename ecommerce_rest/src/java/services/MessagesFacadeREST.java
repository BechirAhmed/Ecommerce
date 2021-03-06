package services;

import domain.Messages;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import utils.AuthenticationFilter;
import utils.KeyHolder;

@Stateless
@Path("messages")
public class MessagesFacadeREST extends AbstractFacade<Messages> {

    @PersistenceContext(unitName = "ecommerce_restPU")
    private EntityManager em;

    public MessagesFacadeREST() {
        super(Messages.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_JSON})
    public void create(Messages entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, Messages entity) {
        super.edit(entity);
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Messages> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    /*** CUSTOM METHODS ***/
    
    private static String className = MessagesFacadeREST.class.getName();
    
    @DELETE
    @Path("delete/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public String remove(@HeaderParam("Authorization") String token, @PathParam("id")String id) {
        if (KeyHolder.checkToken(token, className)) {
            super.remove(super.find(Integer.parseInt(id)));
            token = KeyHolder.issueToken(null);
        }
        return token;
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Messages find(@HeaderParam("Authorization")String token, @PathParam("id") Integer id) {
        if (KeyHolder.checkToken(token, className)) {
            return super.find(id);
        }
        return null;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public List<Messages> findAll(@HeaderParam("Authorization") String token) {
        List<Messages> data = new ArrayList<Messages>();
        if (KeyHolder.checkToken(token, className)) {
            data = super.findAll();
        }
        return data;
    }
    
    @GET
    @Path("conversation")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Messages> findByConversation(@HeaderParam("Authorization") String token, @QueryParam("convId")Integer convId) {
        List<Messages> data = new ArrayList<Messages>();
        if (KeyHolder.checkToken(token, className)) {
            Query query = em.createNamedQuery("findByConversation");
            query.setParameter("convId", convId);
            data = query.getResultList();
        }
        return data;
    }
    
    private boolean addNewMessage(Integer senderId, Integer conversationId, String msgBody) {
        Query query = em.createNativeQuery("INSERT IGNORE INTO messages (user_id, conversation_id, body) VALUES (?, ?, ?)");
        query.setParameter(1, senderId);
        query.setParameter(2, conversationId);
        query.setParameter(3, msgBody);

        int result = query.executeUpdate();
        if (result > 0) return true;
        return false;
    }
    
    @POST
    @Path("addmessage")
    @Consumes(MediaType.APPLICATION_JSON)
    public String updateConversation(@HeaderParam("Authorization") String token, Messages entity) {
        if (KeyHolder.checkToken(token, className)) {
            int senderId        = entity.getUserId().getId();
            int conversationId  = entity.getConversationId().getId();
            String msgBody      = entity.getBody();
        
            addNewMessage(senderId, conversationId, msgBody);
            return token;
        }
        return "";
    }
}
