package com.github.timmystorms.ogm.neo4j;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.transaction.TransactionManager;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.tooling.GlobalGraphOperations;

import com.github.timmystorms.ogm.neo4j.entity.Item;
import com.github.timmystorms.ogm.neo4j.entity.Person;

public class OgmWithJTATest {

    public static void main(final String[] args) throws Exception {
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory("ogm-jta-neo4j");
        final EntityManager em = emf.createEntityManager();
        final TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();
        transactionManager.begin();
        Person person = new Person("John", "Doe");
        em.persist(person);
        final Item iphone = new Item("iPhone");
        em.persist(iphone);
        final Item ipod = new Item("iPod");
        em.persist(ipod);
        person.addItem(iphone);
        person.addItem(ipod);
        em.merge(person);
        transactionManager.commit();
        final Query query = em.createQuery("from Person p");
        System.out.println("Returned persons:" + query.getResultList().size());
        em.close();
        emf.close();
        printDbContents();
    }
    
    private static void printDbContents() {
        final GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase("C:/TEMP/ogm-neo4j");
        for (final Node node : GlobalGraphOperations.at(graphDb).getAllNodes()) {
            System.out.print(node.getId() + " ");
            for (final String key : node.getPropertyKeys()) {
                System.out.print(key + " - " + node.getProperty(key) + ", ");
            }
            System.out.print("\n");
        }
        graphDb.shutdown();
    }

}