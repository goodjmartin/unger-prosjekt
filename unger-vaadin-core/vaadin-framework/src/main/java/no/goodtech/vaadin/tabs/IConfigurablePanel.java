package no.goodtech.vaadin.tabs;

/**
 * This class defines the interface to be implemented by any panel configured using the 'ConfigurableTabSheetLayout'
 * implementation.
 *
 * @param <LISTENER> The listener provided to the underlying tab sheet panels
 * @param <DTO> The data transfer object provided to the underlying tab sheet panels
 */
public interface IConfigurablePanel<LISTENER, DTO> {

    /**
     * This method should be called to refresh the data displayed in the tab
     *
     * @param dto The data transfer object used by the concrete implementation
     */
    public void refresh(final DTO dto);

}
