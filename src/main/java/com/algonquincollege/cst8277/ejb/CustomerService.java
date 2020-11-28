/*****************************************************************c******************o*******v******id********
 * File: CustomerService.java
 * Course materials (20F) CST 8277
 *
 * @author (original) Mike Norman
 * 
 * update by : I. Am. A. Student 040nnnnnnn
 *
 */
package com.algonquincollege.cst8277.ejb;

import static com.algonquincollege.cst8277.models.SecurityRole.ROLE_BY_NAME_QUERY;
import static com.algonquincollege.cst8277.models.SecurityUser.SECURITY_USER_BY_NAME_QUERY;
import static com.algonquincollege.cst8277.utils.MyConstants.DEFAULT_KEY_SIZE;
import static com.algonquincollege.cst8277.utils.MyConstants.DEFAULT_PROPERTY_ALGORITHM;
import static com.algonquincollege.cst8277.utils.MyConstants.DEFAULT_PROPERTY_ITERATIONS;
import static com.algonquincollege.cst8277.utils.MyConstants.DEFAULT_SALT_SIZE;
import static com.algonquincollege.cst8277.utils.MyConstants.DEFAULT_USER_PASSWORD;
import static com.algonquincollege.cst8277.utils.MyConstants.DEFAULT_USER_PREFIX;
import static com.algonquincollege.cst8277.utils.MyConstants.PARAM1;
import static com.algonquincollege.cst8277.utils.MyConstants.PROPERTY_ALGORITHM;
import static com.algonquincollege.cst8277.utils.MyConstants.PROPERTY_ITERATIONS;
import static com.algonquincollege.cst8277.utils.MyConstants.PROPERTY_KEYSIZE;
import static com.algonquincollege.cst8277.utils.MyConstants.PROPERTY_SALTSIZE;
import static com.algonquincollege.cst8277.utils.MyConstants.USER_ROLE;

import static com.algonquincollege.cst8277.models.CustomerPojo.ALL_CUSTOMERS_QUERY_NAME;
import static com.algonquincollege.cst8277.models.CustomerPojo.FIND_CUSTOMERS_BY_ID_QUERY;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
//import javax.transaction.Transactional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import javax.transaction.Transactional;

import com.algonquincollege.cst8277.models.AddressPojo;
import com.algonquincollege.cst8277.models.CustomerPojo;
import com.algonquincollege.cst8277.models.OrderLinePojo;
import com.algonquincollege.cst8277.models.OrderLinePojo_;
import com.algonquincollege.cst8277.models.OrderPojo;
import com.algonquincollege.cst8277.models.OrderPojo_;
import com.algonquincollege.cst8277.models.ProductPojo;
import com.algonquincollege.cst8277.models.ProductPojo_;
import com.algonquincollege.cst8277.models.SecurityRole;
import com.algonquincollege.cst8277.models.SecurityUser;
import com.algonquincollege.cst8277.models.ShippingAddressPojo;
import com.algonquincollege.cst8277.models.StorePojo;
import com.algonquincollege.cst8277.models.StorePojo_;

/**
 * Stateless Singleton Session Bean - CustomerService
 */
@Singleton
public class CustomerService implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final String CUSTOMER_PU = "20f-groupProject-PU";

    @PersistenceContext(name = CUSTOMER_PU)
    protected EntityManager em;

    @Inject
    protected Pbkdf2PasswordHash pbAndjPasswordHash;
    
    //TODO

    public List<CustomerPojo> getAllCustomers() {
        return em.createNamedQuery(ALL_CUSTOMERS_QUERY_NAME, CustomerPojo.class).getResultList();
    }

    public CustomerPojo getCustomerById(int custPK) {
        CustomerPojo cust = null;
        try {
            //TODO actually use db
            cust = em.createNamedQuery(FIND_CUSTOMERS_BY_ID_QUERY, CustomerPojo.class)
            .setParameter(custPK, PARAM1)
            .getSingleResult();
            return cust;
        }
        catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
    }
    
    @Transactional
    public CustomerPojo persistCustomer(CustomerPojo newCustomer) {
        return null;
    }
    
    @Transactional
    public void buildUserForNewCustomer(CustomerPojo newCustomerWithIdTimestamps) {
        SecurityUser userForNewCustomer = new SecurityUser();
        userForNewCustomer.setUsername(DEFAULT_USER_PREFIX + "" + newCustomerWithIdTimestamps.getId());
        Map<String, String> pbAndjProperties = new HashMap<>();
        pbAndjProperties.put(PROPERTY_ALGORITHM, DEFAULT_PROPERTY_ALGORITHM);
        pbAndjProperties.put(PROPERTY_ITERATIONS, DEFAULT_PROPERTY_ITERATIONS);
        pbAndjProperties.put(PROPERTY_SALTSIZE, DEFAULT_SALT_SIZE);
        pbAndjProperties.put(PROPERTY_KEYSIZE, DEFAULT_KEY_SIZE);
        pbAndjPasswordHash.initialize(pbAndjProperties);
        String pwHash = pbAndjPasswordHash.generate(DEFAULT_USER_PASSWORD.toCharArray());
        userForNewCustomer.setPwHash(pwHash);
        userForNewCustomer.setCustomer(newCustomerWithIdTimestamps);
        SecurityRole userRole = em.createNamedQuery(ROLE_BY_NAME_QUERY,
            SecurityRole.class).setParameter(PARAM1, USER_ROLE).getSingleResult();
        userForNewCustomer.getRoles().add(userRole);
        userRole.getUsers().add(userForNewCustomer);
        em.persist(userForNewCustomer);
    }

    @Transactional
    public CustomerPojo setAddressFor(int custId, AddressPojo newAddress) {
        CustomerPojo updatedCustomer = em.find(CustomerPojo.class, custId);
        if (newAddress instanceof ShippingAddressPojo) {
            updatedCustomer.setShippingAddress(newAddress);
        }
        else {
            updatedCustomer.setBillingAddress(newAddress);
        }
        em.merge(updatedCustomer);
        return updatedCustomer;
    }
 
    @Transactional
    public boolean deleteCustomer(int custId) {
        CustomerPojo cust = em.find(CustomerPojo.class, custId);
        boolean flag = false;
        try {
            if(cust != null) {
                em.remove(cust);
                flag= true;
            }
            return flag;
            
        }catch(Exception e){
            
            flag= false;
            return flag;
        }
    }

    public List<ProductPojo> getAllProducts() {
        //example of using JPA Criteria query instead of JPQL
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<ProductPojo> q = cb.createQuery(ProductPojo.class);
            Root<ProductPojo> c = q.from(ProductPojo.class);
            q.select(c);
            TypedQuery<ProductPojo> q2 = em.createQuery(q);
            List<ProductPojo> allProducts = q2.getResultList();
            return allProducts;
        }
        catch (Exception e) {
            return null;
        }
    }

    public ProductPojo getProductById(int prodId) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<ProductPojo> q1 = cb.createQuery(ProductPojo.class);
            Root<ProductPojo> root = q1.from(ProductPojo.class);
            q1.where(cb.equal((root.get(ProductPojo_.id)),prodId));
            
            TypedQuery<ProductPojo> tq = em.createQuery(q1);
            ProductPojo product = tq.getSingleResult();
            return product;
        }
        catch (Exception e) {
            return null;
        }
    }

    public List<StorePojo> getAllStores() {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<StorePojo> q = cb.createQuery(StorePojo.class);
            Root<StorePojo> c = q.from(StorePojo.class);
            q.select(c);
            TypedQuery<StorePojo> q2 = em.createQuery(q);
            List<StorePojo> allStores = q2.getResultList();
            return allStores;
        }
        catch (Exception e) {
            return null;
        }
    }

    public StorePojo getStoreById(int id) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<StorePojo> q1 = cb.createQuery(StorePojo.class);
            Root<StorePojo> root = q1.from(StorePojo.class);
            q1.where(cb.equal((root.get(StorePojo_.id)),id));
            
            TypedQuery<StorePojo> tq = em.createQuery(q1);
            StorePojo product = tq.getSingleResult();
            return product;
        }
        catch (Exception e) {
            return null;
        }
    }
    
    /*
    
    public OrderPojo getAllOrders ... getOrderbyId ... build Orders with OrderLines ...
     
    */
    public List<OrderPojo> getAllOrders() {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<OrderPojo> q = cb.createQuery(OrderPojo.class);
            Root<OrderPojo> c = q.from(OrderPojo.class);
            q.select(c);
            TypedQuery<OrderPojo> q2 = em.createQuery(q);
            List<OrderPojo> allOrders = q2.getResultList();
            return allOrders;
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public OrderPojo getOrderById(int orderId) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<OrderPojo> q1 = cb.createQuery(OrderPojo.class);
            Root<OrderPojo> root = q1.from(OrderPojo.class);
            q1.where(cb.equal((root.get(OrderPojo_.id)), orderId));
            
            TypedQuery<OrderPojo> tq = em.createQuery(q1);
            OrderPojo order = tq.getSingleResult();
            return order;
        }
        catch (Exception e) {
            return null;
        }
    }
    
    
    
    
    
    
    public List<OrderLinePojo> getAllOrderLine() {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<OrderLinePojo> q = cb.createQuery(OrderLinePojo.class);
            Root<OrderLinePojo> c = q.from(OrderLinePojo.class);
            q.select(c);
            TypedQuery<OrderLinePojo> q2 = em.createQuery(q);
            List<OrderLinePojo> allOrderLines = q2.getResultList();
            return allOrderLines;
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public OrderLinePojo getOrderLineById(int orderLineId) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<OrderLinePojo> q1 = cb.createQuery(OrderLinePojo.class);
            Root<OrderLinePojo> root = q1.from(OrderLinePojo.class);
            q1.where(cb.equal((root.get(OrderLinePojo_.pk)), orderLineId));
            
            TypedQuery<OrderLinePojo> tq = em.createQuery(q1);
            OrderLinePojo order = tq.getSingleResult();
            return order;
        }
        catch (Exception e) {
            return null;
        }
    }
    
    
    
    
    @Transactional
    public boolean deleteOrder(int orderId) {
        OrderPojo or = em.find(OrderPojo.class, orderId);
        boolean flag = false;
        try {
            if(or != null) {
                em.remove(or);
                flag= true;
            }
            return flag;
            
        }catch(Exception e){
            
            flag= false;
            return flag;
        }
    }
    
    
    
    @Transactional
    public boolean deleteOrderLine(int orderLineId) {
        OrderLinePojo or = em.find(OrderLinePojo.class, orderLineId);
        boolean flag = false;
        try {
            if(or != null) {
                em.remove(or);
                flag= true;
            }
            return flag;
            
        }catch(Exception e){
            
            flag= false;
            return flag;
        }
    }
    
    
    @Transactional
    public OrderPojo updateOrder(OrderPojo  or) {
        try {
            
            return em.merge(or);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
       
    }
    
    @Transactional
    public OrderLinePojo updateOrderLine(OrderLinePojo  or) {
        try {
            
            return em.merge(or);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
       
    }
    
    @Transactional
    public void createOrder(OrderPojo or) {
        em.persist(or);
    }
    

    @Transactional
    public void createOrderLine(OrderLinePojo or) {
        em.persist(or);
    }
    
    
    
    
    
    
    
}