package no.goodtech.vaadin.search;

import com.vaadin.ui.Label;
import no.goodtech.vaadin.ui.Texts;

public class CountLabel extends Label {

	public void refresh(Integer count) {
		refresh(count, null);
	}

	public void refresh(Integer count, Integer fullCount) {
		if (count == null && fullCount == null) {
			// No counts are given
			setVisible(false);
		}else if (count == null || fullCount == null) {
			// Either count or full count is null - show simple label
			int singleCount = (count != null) ? count : fullCount;
			if (singleCount == 0) {
				setValue(Texts.get("filterPanel.count.value.none"));
			} else {
				setValue(String.format(Texts.get("filterPanel.count.value"), singleCount));
			}
		} else {
			// Both counts are given - show extended label
			setVisible(true);
			if (count == 0) {
				setValue(Texts.get("filterPanel.count.value.none"));
			} else {
				setValue(String.format(Texts.get("filterPanel.count.valueAndFound"), count, fullCount));
			}
		}
	}
}
