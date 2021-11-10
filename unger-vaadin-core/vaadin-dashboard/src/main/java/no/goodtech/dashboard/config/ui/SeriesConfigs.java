package no.goodtech.dashboard.config.ui;

import java.util.*;

/**
 * A registry of {@link SeriesConfig}
 */
public class SeriesConfigs implements Iterable<SeriesConfig> {

	final Set<SeriesConfig> seriesConfigs;
	final Map<String, SeriesConfig> seriesConfigPerId;

	public SeriesConfigs(Collection<SeriesConfig> seriesConfigs) {
		this.seriesConfigPerId = new LinkedHashMap<>();
		this.seriesConfigs = new LinkedHashSet<>();
		for (SeriesConfig seriesConfig : seriesConfigs) {
			add(seriesConfig);
		}
	}

	public void add(SeriesConfig seriesConfig) {
		seriesConfigPerId.put(seriesConfig.getId(), seriesConfig);
		seriesConfigs.add(seriesConfig);
	}

	public Set<String> getIds() {
		return seriesConfigPerId.keySet();
	}

	public SeriesConfig findById(String id) {
		return seriesConfigPerId.get(id);
	}

	public SeriesConfig findById(Integer id) {
		if (id != null) {
			return seriesConfigPerId.get(String.valueOf(id.intValue()));
		}
		return null;
	}

	public Set<SeriesConfig> getSeriesConfigs(PanelConfig panelConfig) {
		Set<SeriesConfig> seriesConfigsForThisPanel = new LinkedHashSet<>();
		for (SeriesConfig seriesConfig : seriesConfigs) {
			if (seriesConfig.getPanelConfig() != null && seriesConfig.getPanelConfig().equals(panelConfig)) {
				seriesConfigsForThisPanel.add(seriesConfig);
			}
		}
		return seriesConfigsForThisPanel;
	}

	@Override
	public Iterator<SeriesConfig> iterator() {
		return seriesConfigs.iterator();
	}

	public int size() {
		return seriesConfigs.size();
	}

	public Map<Integer, SeriesConfig> mapByPk() {
		Map<Integer, SeriesConfig> seriesConfigPerPk = new HashMap<>();
		for (SeriesConfig seriesConfig : seriesConfigs) {
			if (seriesConfig.getPk() != null) {
				//we ignore auto-generated series configs (they don't have pk)
				seriesConfigPerPk.put(seriesConfig.getPk().intValue(), seriesConfig);
			}
		}
		return seriesConfigPerPk;
	}

	public SeriesConfig findByPk(int pk) {
		return mapByPk().get(pk);
	}

	public Set<ChartConfig> getPanelConfigs() {
		Set<ChartConfig> panelConfigs = new LinkedHashSet<>();
		for (SeriesConfig seriesConfig : seriesConfigs) {
			if (seriesConfig.getPanelConfig() != null) {
				panelConfigs.add(seriesConfig.getPanelConfig());
			}
		}
		return panelConfigs;
	}
}
