package org.ojbc.util.osgi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.ServiceReference;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class ActivationWithTwoServicesTest extends AbstractActivationTest {

	private static final Log log = LogFactory.getLog(ActivationWithTwoServicesTest.class);

	@Test
	public void test() throws Exception {

		ServiceReference[] serviceReferences = bundleContext.getAllServiceReferences(null, null);

		ServiceReference foundService1 = null;
		ServiceReference foundService2 = null;

		for (ServiceReference ref : serviceReferences) {
			String beanName = (String) ref.getProperty("org.springframework.osgi.bean.name");
			if (beanName != null) {
				if ("service1".equals(beanName)) {
					foundService1 = ref;
				} else if ("service2".equals(beanName)) {
					foundService2 = ref;
				}
			}
		}

		assertNotNull(foundService1);
		assertTrue(OjbcContext.class.isAssignableFrom(bundleContext.getService(foundService1).getClass()));
		assertNotNull(foundService2);
		assertTrue(OjbcContext.class.isAssignableFrom(bundleContext.getService(foundService2).getClass()));

	}

	@Override
	protected File getConfigFile() {
		return new File("src/test/config/ojb-osgi-utils-two-services.cfg");
	}

}
