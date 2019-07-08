/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.exceptions.NonexistentEntityException;
import Entity.User;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Jhon Baron
 */
public class ImplDao<T, ID> implements IDao<T, ID> {

    public ImplDao() {
        this.emf = Persistence.createEntityManagerFactory("");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(User user) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(User user) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            user = em.merge(user);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = user.getIduser();
                if (findUser(id) == null) {
                    throw new NonexistentEntityException("The user with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getIduser();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            em.remove(user);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<User> findUserEntities() {
        return findUserEntities(true, -1, -1);
    }

    public List<User> findUserEntities(int maxResults, int firstResult) {
        return findUserEntities(false, maxResults, firstResult);
    }

    private List<User> findUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public User findUser(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<User> rt = cq.from(User.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    @Override
    public void crear(T entity) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            FacesUtil.addInfoMessage("Se alamcenó en el sistema");
        } catch (PersistenceException pe) {
            FacesUtil.addErrorMessage("Error de Persisntencia: " + pe.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                if (em.getTransaction() != null && em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                em.close();
                em = null;
            }
        }
    }

    @Override
    public <T> T consultar(Class<T> entityClass, Object primaryKey) {
        EntityManager em = getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        T ent = em.find(entityClass, primaryKey);
        et.commit();
        em.close();
        return ent;
    }

    public <T> T consultarC(Class<T> entityClass, Object primaryKey) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        T ent = em.find(entityClass, primaryKey);
        return ent;
    }

    @Override
    public <T> T modificar(T entity) {
        T ent = null;
        EntityManager em = getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            ent = em.merge(entity);
            et.commit();
            FacesUtil.addInfoMessage("Se actualizó en el sistema");
        } catch (PersistenceException pe) {
            et.rollback();
            FacesUtil.addErrorMessage("Error de persistencia " + pe.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return ent;
    }

    @Override
    public void eliminar(Object entity) {
        EntityManager em = getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            em.getTransaction().begin();
            entity = em.merge(entity);
            em.remove(entity);
            em.getTransaction().commit();
        } catch (PersistenceException pe) {
            em.getTransaction().rollback();
            FacesUtil.addErrorMessage("Error de Persistencia: " + pe.getMessage());
        } finally {
            em.close();
        }

    }

    @Override
    public List<T> consultarTodo(Class<T> entityClass) {
        List<T> entidades;
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        String clase = entityClass.getSimpleName();
        entidades=em.createQuery("select t from "+clase+" t").getResultList();
        em.close();
        return entidades;
    }
}
