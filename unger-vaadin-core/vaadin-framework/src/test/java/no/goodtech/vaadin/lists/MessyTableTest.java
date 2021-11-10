package no.goodtech.vaadin.lists;

import com.vaadin.v7.data.util.BeanItemContainer;
import no.goodtech.vaadin.lists.v7.MessyTable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class MessyTableTest {

    private final BeanItemContainer<Bean> container = new BeanItemContainer<>(Bean.class);
    private final Bean bean1 = new Bean(1, 1);
    private final Bean bean2 = new Bean(2, 1);
    private final Bean bean3 = new Bean(3, 1);
    private final Bean newVersionOfBean1 = new Bean(1, 2);
    private final Bean newVersionOfBean2 = new Bean(2, 2);
    private final Bean newVersionOfBean3 = new Bean(3, 2);
    private volatile MessyTable<Bean> table;

    @Before
    public void setUp() {
        table = new MessyTable<>(container, null);
    }

    @Test
    public void singleSelectModeSelectFirst() {
        // Refresh table
        table.refresh(Arrays.asList(bean1, bean2, bean3));

        // Validate selection (version 1)
        Assert.assertEquals(1, table.getSelectedObject().pk.intValue());
        Assert.assertEquals(1, table.getSelectedObject().version);

        // Create variable to capture value change event
        AtomicInteger atomicInteger = new AtomicInteger(0);

        // Register value change listener
        table.addValueChangeListener(valueChangeEvent -> atomicInteger.incrementAndGet());

        // Refresh table again
        table.refresh(Arrays.asList(newVersionOfBean1, newVersionOfBean2, newVersionOfBean3));

        // Validate listener triggered once
        Assert.assertEquals(1, atomicInteger.get());

        // Validate selection (version 2)
        Assert.assertEquals(1, table.getSelectedObject().pk.intValue());
        Assert.assertEquals(2, table.getSelectedObject().version);
    }

    @Test
    public void multiSelectModeSelectFirst() {
        // Set multi selection mode
        table.setMultiSelect(true);

        // Create variable to capture value change event
        AtomicInteger atomicInteger = new AtomicInteger(0);

        // Register value change listener
        table.addValueChangeListener(valueChangeEvent -> atomicInteger.incrementAndGet());

        // Refresh table
        table.refresh(Arrays.asList(bean1, bean2, bean3));

        // Validate listener triggered once
        Assert.assertEquals(1, atomicInteger.get());

        // Validate selection (version 1)
        Assert.assertEquals(1, ((Set) table.getSelectedObject()).size());
        Bean selectedBean = (Bean)((Set)table.getSelectedObject()).iterator().next();
        Assert.assertEquals(1, selectedBean.pk.intValue());
        Assert.assertEquals(1, selectedBean.version);
    }

    @Test
    public void singleSelectMode() {
        // Refresh table
        table.refresh(Arrays.asList(bean1, bean2, bean3));

        // Select bean (version 1)
        table.select(bean2);

        // Validate selection (version 1)
        Assert.assertEquals(2, table.getSelectedObject().pk.intValue());
        Assert.assertEquals(1, table.getSelectedObject().version);

        // Create variable to capture value change event
        AtomicInteger atomicInteger = new AtomicInteger(0);

        // Register value change listener
        table.addValueChangeListener(valueChangeEvent -> atomicInteger.incrementAndGet());

        // Refresh table again
        table.refresh(Arrays.asList(newVersionOfBean1, newVersionOfBean2, newVersionOfBean3));

        // Validate listener triggered once
        Assert.assertEquals(1, atomicInteger.get());

        // Validate selection (version 2)
        Assert.assertEquals(2, table.getSelectedObject().pk.intValue());
        Assert.assertEquals(2, table.getSelectedObject().version);
    }

	@Test
	public void multiSelectModeSingleSelection() {
        // Set multi selection mode
        table.setMultiSelect(true);

        // Refresh table
        table.refresh(Arrays.asList(bean1, bean2, bean3));

        // Select bean (version 1)
        table.setSelectedObject(bean2);

        // Validate selection (version 1)
        Assert.assertEquals(1, ((Set) table.getSelectedObject()).size());
        Bean selectedBean = (Bean)((Set)table.getSelectedObject()).iterator().next();
        Assert.assertEquals(2, selectedBean.pk.intValue());
        Assert.assertEquals(1, selectedBean.version);

        // Create variable to capture value change event
        AtomicInteger atomicInteger = new AtomicInteger(0);

        // Register value change listener
        table.addValueChangeListener(valueChangeEvent -> atomicInteger.incrementAndGet());

        // Refresh table again
        table.refresh(Arrays.asList(newVersionOfBean1, newVersionOfBean2, newVersionOfBean3));

        // Validate listener triggered once
        Assert.assertEquals(1, atomicInteger.get());

        // Validate selection (version 2)
        Assert.assertEquals(1, ((Set) table.getSelectedObject()).size());
        selectedBean = (Bean)((Set)table.getSelectedObject()).iterator().next();
        Assert.assertEquals(2, selectedBean.pk.intValue());
        Assert.assertEquals(2, selectedBean.version);
	}

    @Test
    public void multiSelectModeMultipleSelections() {
        // Set multi selection mode
        table.setMultiSelect(true);

        // Refresh table
        table.refresh(Arrays.asList(bean1, bean2, bean3));

        // Select beans (version 1)
        table.setSelectedObject(bean2);
        table.select(bean3);

        // Validate selections (version 1)
        Iterator iterator = ((Set)table.getSelectedObject()).iterator();
        Assert.assertEquals(2, ((Set) table.getSelectedObject()).size());
        Bean selectedBean = (Bean)iterator.next();   // First selection
        Assert.assertEquals(2, selectedBean.pk.intValue());
        Assert.assertEquals(1, selectedBean.version);
        selectedBean = (Bean)iterator.next();       // Second selection
        Assert.assertEquals(3, selectedBean.pk.intValue());
        Assert.assertEquals(1, selectedBean.version);

        // Create variable to capture value change event
        AtomicInteger atomicInteger = new AtomicInteger(0);

        // Register value change listener
        table.addValueChangeListener(valueChangeEvent -> atomicInteger.incrementAndGet());

        // Refresh table again
        table.refresh(Arrays.asList(newVersionOfBean1, newVersionOfBean2, newVersionOfBean3));

        // Validate listener triggered once
        Assert.assertEquals(1, atomicInteger.get());

        // Validate selections (version 2)
        iterator = ((Set)table.getSelectedObject()).iterator();
        Assert.assertEquals(2, ((Set) table.getSelectedObject()).size());
        selectedBean = (Bean)iterator.next();   // First selection
        Assert.assertEquals(2, selectedBean.pk.intValue());
        Assert.assertEquals(2, selectedBean.version);
        selectedBean = (Bean)iterator.next();   // Second selection
        Assert.assertEquals(3, selectedBean.pk.intValue());
        Assert.assertEquals(2, selectedBean.version);
    }

    @Test
    public void singleSelectModeNoEntries() {
        // Create variable to capture value change event
        AtomicInteger atomicInteger = new AtomicInteger(0);

        // Register value change listener
        table.addValueChangeListener(valueChangeEvent -> atomicInteger.incrementAndGet());

        // Refresh table
        table.refresh(Collections.emptyList());

        // Validate listener triggered once
        Assert.assertEquals(1, atomicInteger.get());

        // Validate no selections
        Assert.assertNull(table.getSelectedObject());
    }

    @Test
    public void multiSelectModeNoEntries() {
        // Set multi selection mode
        table.setMultiSelect(true);

        // Create variable to capture value change event
        AtomicInteger atomicInteger = new AtomicInteger(0);

        // Register value change listener
        table.addValueChangeListener(valueChangeEvent -> atomicInteger.incrementAndGet());

        // Refresh table
        table.refresh(Collections.emptyList());

        // Validate listener triggered once
        Assert.assertEquals(1, atomicInteger.get());

        // Validate no selections
        Assert.assertEquals(0, table.getSelectedObjects().size());
    }

}
