package com.buddhadata.sandbox.neo4j.ogm.intro;

import com.buddhadata.sandbox.neo4j.ogm.intro.node.Person;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.cypher.BooleanOperator;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;
import org.neo4j.ogm.metadata.schema.Node;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.transaction.Transaction;

import java.time.Year;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OGM Intro for loading data
 * Demonstrates communication between Eclipse and Neo4j database by implementing CRUD operations 
 */
public class Loader {

    /**
     * Session factory for connecting to Neo4j database
     */
    private final SessionFactory sessionFactory;

    //  Configuration info for connecting to the Neo4J database
    static private final String SERVER_URI = "bolt://54.90.28.212:32813";
    static private final String SERVER_USERNAME = "neo4j";
    static private final String SERVER_PASSWORD = "shields-letterhead-passivations";
    int countPeople = 12;

    /**
     * Constructor
     */
    public Loader() {
        //  Define session factory for connecting to Neo4j database
        Configuration configuration = new Configuration.Builder().uri(SERVER_URI).credentials(SERVER_USERNAME, SERVER_PASSWORD).build();
        sessionFactory = new SessionFactory(configuration, "com.buddhadata.sandbox.neo4j.ogm.intro.node", "com.buddhadata.sandbox.neo4j.ogm.intro.relationship");
    }


    /**
     * Main method for starting Java program
     * @param args command line arguments
     */
    public static void main (String[] args) {

        //  Create an instance of the class and process the file.
        new Loader().process();
    }

    /**
     * Method for doing work
     */
    private void process () {
        //  For demo purposes, create session and purge to cleanup whatever you have
        Session session = sessionFactory.openSession();
        session.purgeDatabase();

        //  Load the data via OGM
        load (session);

        System.out.println();
        Collection<Person> people = new Loader().readPeople(session);

        // Prints out existing people from Neo4j
        printPeople(people);
    }


    /**
     * Load the data.
     * This is kind of method where an Object(Person) from Eclipse is written to Neo4j Database 
     */
    private void load (Session session) {

        //  All work done in single transaction.
        Transaction txn = session.beginTransaction();

        //  Following Person Objects will be what we will see in Neo4j database initially 
        //  Create all persons.
        Person Carol = new Person ("Carol Maureen", 1945);
        Person Courtney = new Person ("Courtney Janice", 1945);
        Person Esme = new Person ("Esme Alexis", 1981);
        Person Gabe = new Person ("Gabriel Josiah", 1979);
        Person Gail = new Person ("Gail Ann", 1942);
        Person Jeremy = new Person ("Jeremy Douglas", 1969);
        Person Jesse = new Person ("Jesse Lucas", 1977);
        Person Kelly = new Person ("Kelly Leigh", 1977);
        Person Mike = new Person ("Michael Blevins", 1945);
        Person Scott = new Person ("Scott Christoper", 1965);
        Person Steve = new Person ("Steven Lester", 1950);
        Person Zane = new Person ("Michael Zane", 1973);

        //  Add children to each parent.
        //  There is-a relationship between a parent(Person) and their children(List-of-Person)
        //  We have defined this relationship in a Person class with an attribute that has @Relationship annotation on it
        List<Person> children = Carol.getChildren();
        children.add (Scott);
        children.add (Courtney);
        children.add (Jeremy);
        children.add (Jesse);
        children.add (Gabe);
        children.add (Esme);
        children = Mike.getChildren();
        children.add (Scott);
        children.add (Courtney);
        children.add (Jeremy);
        children.add (Zane);
        children.add (Kelly);
        children = Gail.getChildren();
        children.add (Zane);
        children.add (Kelly);
        children = Steve.getChildren();
        children.add (Jesse);
        children.add (Gabe);
        children.add (Esme);

        //  Save to database
        // Upon invocation of save() with an entity, it checks the given object graph for changes compared with the data that was 
        // loaded from the database.
        // If there is a difference, it saves the most recent change that happened
        session.save (Carol);
        session.save (Courtney);
        session.save (Esme);
        session.save (Gabe);
        session.save (Gail);
        session.save (Jeremy);
        session.save (Jesse);
        session.save (Kelly);
        session.save (Mike);
        session.save (Scott);
        session.save (Steve);
        session.save (Zane);
/*
        // UPDATE operation can be shown here
        updatePerson(Carol,"Raj Trivedi",session);
*/        
        //  Commit the transaction.
        txn.commit();
    }

    /***
     * CREATE operation
     * @param p Person to be created in Neo4j database
     * @param session
     */
    private void createPerson(Person p, Session session) {
        // CREATE operation can be performed by:
        //   1. Creating a new instance of Person
        //   2. Calling save() method to save that instance in Neo4j database 
    	
    	System.out.println("New Person created: " + p.getName() + " with a birth year of " + p.getBirthYear());
    	System.out.println();
    	session.save(p);
    	countPeople++;
    }
    
    /***
     * UPDATE operation by Person's name 
     * @param p Person to be updated on the basis of name in Neo4j database
     * @param session
     */
    private void updatePerson(Person p, String name, Session session) {
    	System.out.println("Person updated from: " + p.getName() + " with a birth year of " + p.getBirthYear());
    	p.setName(name);
    	System.out.println("Person updated to: " + p.getName() + " with a birth year of " + p.getBirthYear());
    	System.out.println();
    	session.save(p);
    }

    /***
     * UPDATE operation by Person's birth year
     * @param p Person to be updated on the basis of birth year in Neo4j database
     * @param session
     */
    private void updatePerson(Person p, int birthYear, Session session) {
    	System.out.println("Person updated from: " + p.getName() + " with a birth year of " + p.getBirthYear());
    	p.setBirthYear(birthYear);
    	System.out.println("Person updated to: " + p.getName() + " with a birth year of " + p.getBirthYear());
    	System.out.println();
    	session.save(p);
    }

    /***
     * UPDATE operation by Person
     * @param p Person to be updated on the basis of both name and birth year in Neo4j database
     * @param session
     */
    private void updatePerson(Person p, String name, int birthYear, Session session) {
    	System.out.println("Person updated from: " + p.getName() + " with a birth year of " + p.getBirthYear());
    	p.setName(name);
    	p.setBirthYear(birthYear);
    	System.out.println("Person updated to: " + p.getName() + " with a birth year of " + p.getBirthYear());
    	System.out.println();
    	session.save(p);
    }


    /***
     * DELETE operation
     * @param p Person to be deleted from Neo4j database
     * @param session
     */
    private void deletePerson(Person p, Session session) {
        // DELETE operation can be performed upon invocation of delete() with an entity
        // It basically destroys that node from the Neo4j database
    	
    	System.out.println("Person deleted: " + p.getName() + " with a birth year of " + p.getBirthYear());
    	System.out.println();
    	session.delete(p);
    	countPeople--;
    }

    /** READ operation without query
     *  Reads all people from Neo4j database
     * @return collection of Person from Neo4j database
     */
    private Collection<Person> readPeople (Session session) {
    	return session.loadAll(Person.class);
    }
    
    /***
     * 
     * @param people Collection of Person Objects
     */
    private void printPeople(Collection<Person> people) {
        System.out.println("Printing out existing people in Neo4j database...");
        System.out.println();
        people.forEach (one -> System.out.println (one.getName() + " with a birth year " + one.getBirthYear()));
        System.out.println();
        System.out.println("Total People: " + countPeople);
        System.out.println();
    }
    
}
