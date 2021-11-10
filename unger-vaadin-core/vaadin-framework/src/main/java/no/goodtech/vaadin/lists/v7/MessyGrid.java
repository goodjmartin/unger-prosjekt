package no.goodtech.vaadin.lists.v7;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.ui.Grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class was needed since Vaadin Grid do not have support for cancel handlers (custom actions on cancel)
 */
@Deprecated
public class MessyGrid<T> extends Grid {

    private final IMessyGrid<T> messyGrid;

    public MessyGrid(final IMessyGrid<T> messyGrid) {
        this.messyGrid = messyGrid;

        addItemClickListener(itemClickEvent -> {
            if (getSelectionModel().getSelectedRows().size() == 1)
                messyGrid.objectSelected(null);
        });

        // TODO put in panel?
        addShortcutListener(new ShortcutListener("ENTER", ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                Object targetParent = ((AbstractComponent) target).getParent();
                if ((targetParent != null) && (targetParent instanceof Grid)) {

                    MessyGrid targetGrid = (MessyGrid) targetParent;

                    if (targetGrid.isEditorActive()) {
                        try {
                            targetGrid.saveEditor();
                            targetGrid.cancelEditor();
                        } catch (FieldGroup.CommitException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        addSelectionListener(selectionEvent -> {
			if(getSelectionModel().getSelectedRows().size()==1)
				messyGrid.objectSelected((T) selectionEvent.getSelected().iterator().next());
			else
				messyGrid.objectsSelected((Set<T>) selectionEvent.getSelected());
		});
    }

    public void refresh(List<T> objects){
        getContainerDataSource().removeAllItems();
        for (T object : objects) {
            getContainerDataSource().addItem(object);
        }

        ArrayList<Object> rightAlignedColumns = new ArrayList<>();
        for (Object propertyId : getContainerDataSource().getContainerPropertyIds()) {
            final Class<?> type = getContainerDataSource().getType(propertyId);
            if (Number.class.isAssignableFrom(type))
                rightAlignedColumns.add(propertyId);
        }
        setRightAlignedColumns(rightAlignedColumns);
    }

    /**
     * Specify which columns to be right aligned
     * Add to css:
     * .rightAligned {
     *   text-align: right;
     * }
     */
    public void setRightAlignedColumns(List<Object> propertyIds){
        setCellStyleGenerator(cellReference -> {
            Object propertyId = cellReference.getPropertyId();
            String rightAligned = "rightAligned";

            for (Object columnId : propertyIds) {
                if (columnId.equals(propertyId)) {
                    return rightAligned;
                }
            }
            return null;
        });
    }

    @Override
    protected void doCancelEditor() {
        super.doCancelEditor();
        messyGrid.editingCanceled();
    }

}
