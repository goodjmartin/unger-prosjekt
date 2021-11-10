package no.goodtech.vaadin.remotecontrol.opc;

import java.util.Collections;
import java.util.Map;

import no.goodtech.opc.Globals;
import no.goodtech.opc.MockOpcRepository;
import no.goodtech.opc.NodeValue;
import no.goodtech.persistence.MainConfig;
import no.goodtech.vaadin.remotecontrol.metamodel.Widget;
import no.gooodtech.vaadin.remotecontrol.data.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test av {@link DataSource}
 */
@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@Transactional
@ContextConfiguration(classes = MainConfig.class, locations={"classpath*:goodtech-server.xml"})
public class OpcUtilsTest {

	/**
	 * Oppsett av testdata
	 */
	@Before
	public void before() {
		Globals.setOpcRepository(new MockOpcRepository());
		Globals.getOpcRepository().setNodeValue("tag1", "hjalla");
	}

	/**
	 * Test av {@link DataSource#readValues(java.util.List)}
	 */
	@Test
	public void test() {
		Widget w1 = createWidget("tag1");
		
		final Map<Widget, NodeValue> result = DataSource.readValues(Collections.singletonList(w1));
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(result.get(w1).toString(), "hjalla");
	}
	private Widget createWidget(String tag) {
		Widget widget = new Widget();
		widget.setTag(tag);
		return widget;
	}

}
