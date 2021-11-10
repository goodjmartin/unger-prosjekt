package no.goodtech.vaadin.tabs;

import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;

/**
 * Tab-gruppe som egner seg til å vise forskjellige flipper for samme domene-objekt
 * Løselig basert på {@link ConfigurableTabSheetLayout}, men her må du legge til flippene i Java-koden
 *
 * @param <LISTENER> callback-interface hvis du vil få beskjed hvis det skjer noe inni en flipp
 * @param <DTO> domene-klasse
 */
public class DetailObjectTabSheet<LISTENER, DTO> extends TabSheet {

    private volatile DTO dto;

    /**
     * Opprett tabgruppe. Tab'er kan legges til senere vha addTab
     */
    public DetailObjectTabSheet() {	
        setSizeFull();

        // Catch the tab selections
        addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(SelectedTabChangeEvent event) {
                if (dto != null) {
                    refresh(dto);
                }
            }
        });
    }

    /**
     * Oppdater tab'er med friske data
     * Kun valgt tab oppfriskes
     * @param dto objektet som skal vises
     */
    public void refresh(final DTO dto) {
        this.dto = dto;

		final Component selectedTab = getSelectedTab();
		if (selectedTab != null) {
			@SuppressWarnings("unchecked")
			IConfigurablePanel<LISTENER, DTO> refreshableTab = (IConfigurablePanel<LISTENER, DTO>) selectedTab;
			refreshableTab.refresh(dto);
		}
    }

}
