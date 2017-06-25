/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import domain.Messages;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author vasso
 */
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

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Messages find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_JSON})
    public List<Messages> findAll() {
        return super.findAll();
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
    @GET
    @Path("conversation")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Messages> findByConversation(@QueryParam("convId")Integer convId) {
        Query query = em.createNamedQuery("findByConversation");
        query.setParameter("convId", convId);
        return query.getResultList();
    }
    
    public boolean addNewMessage(Integer senderId, Integer conversationId, String msgBody) {
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
    public void updateConversation(Messages entity) {
        int senderId        = entity.getUserId().getId();
        int conversationId  = entity.getConversationId().getId();
        String msgBody      = entity.getBody();
        
        addNewMessage(senderId, conversationId, msgBody);
    }
}