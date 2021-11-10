package no.goodtech.vaadin.search;

import org.junit.Assert;
import org.junit.Test;

import no.goodtech.vaadin.search.FilterPanel.IMaxRowsAware;

public class FilterPanelTest {

	@Test
	public void testUrlIncludeDisabled() {
		final MyFilterPanel panel = new MyFilterPanel();
		Assert.assertEquals(false, panel.getFinder().includeDisabled);
		panel.refresh(null);
		panel.triggerSearch();

		panel.refresh("disabled=");
		panel.triggerSearch();

		panel.refresh("disabled=false");
		panel.triggerSearch();

		panel.refresh("disabled=true");
		panel.triggerSearch();
	}
	
	@Test
	public void testUrlMaxRows() {
		final MyFilterPanel panel = new MyFilterPanel();
		Assert.assertNull(panel.getFinder().maxResults);
		panel.refresh(null);
		panel.triggerSearch();

		panel.refresh("maxRows=");
		panel.triggerSearch();

		panel.refresh("maxRows=1000");
		panel.triggerSearch();

		panel.refresh("maxRows=10001");
		panel.triggerSearch();
	}

	class Finder implements IMaxRowsAware {

		Integer maxResults;
		boolean includeDisabled = false;
		
		public void setMaxResults(Integer maxResults) {
			this.maxResults = maxResults;
		}

		public IMaxRowsAware setIncludeDisabled() {
			includeDisabled = true;
			return this;
		}
	}
	
	class MyFilterPanel extends FilterPanel<Finder> {

		public MyFilterPanel() {
			super(new FilterActionListener<Finder>() {
				@Override
				public Integer pleaseSearch(Finder finder) {
					return 0;
				}
			});
		}
		
		public Finder getFinder() {
			return new Finder();
		}
	}

}
