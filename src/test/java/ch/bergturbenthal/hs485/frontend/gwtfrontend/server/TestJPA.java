package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDevice.Type;

public class TestJPA {
	private static EntityManager	em;

	@AfterClass
	public static void closeConnection() {
		em.close();
	}

	@BeforeClass
	public static void setupConnection() {
		final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("JUnit");
		entityManagerFactory.getPersistenceUnitUtil();
		em = entityManagerFactory.createEntityManager();

	}

	@After
	public void endTransaction() {
		em.getTransaction().commit();
	}

	@Before
	public void setUp() throws Exception {
		final EntityTransaction transaction = em.getTransaction();
		transaction.begin();
	}

	@Test
	public void test() {
		final OutputDevice device = new OutputDevice();
		device.setName("Hello");
		device.setType(Type.DIMMER);
		em.persist(device);
	}

}
