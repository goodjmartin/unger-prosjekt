package no.goodtech.dashboard.config.ui;

import no.goodtech.persistence.entity.AbstractSimpleEntityImpl;
import no.goodtech.vaadin.lists.ICopyable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
//@Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
public abstract class PanelConfig extends AbstractSimpleEntityImpl implements ICopyable<PanelConfig> {

	@ManyToOne
	@NotNull
	protected DashboardConfig dashboardConfig;

	protected String title;

	@NotNull
	@Min(1)
	private int startRow = 1;

	@NotNull
	@Min(1)
	private int endRow = 1;

	@NotNull
	@Min(1)
	private int startColumn = 1;

	@NotNull
	@Min(1)
	private int endColumn = 1;

	private int periodLengthInMinutes = 4 * 60;

	private Boolean timeShift;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public int getEndRow() {
		return endRow;
	}

	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}

	public int getStartColumn() {
		return startColumn;
	}

	public void setStartColumn(int startColumn) {
		this.startColumn = startColumn;
	}

	public int getEndColumn() {
		return endColumn;
	}

	public void setEndColumn(int endColumn) {
		this.endColumn = endColumn;
	}

	/**
	 * @return true if row and column coordinates are inside dashboard grid
	 */
	public boolean isCoordinatesValid() {
		if (dashboardConfig == null)
			return false;
		if (getStartRow() < 1 || getEndRow() > dashboardConfig.getNumRows() || getStartRow() > getEndRow())
			return false;
		if (getStartColumn() < 1 || getEndColumn() > dashboardConfig.getNumColumns() || getStartColumn() > getEndColumn())
			return false;
		return true;
	}

	public String formatCoordinates() {
		return String.format("x=%d-%d, y=%d-%d", getStartColumn(), getEndColumn(), getStartRow(), getEndRow());
	}

	public void setCoordinates(int startRow, int startColumn, int endRow, int endColumn) {
		setStartRow(startRow);
		setStartColumn(startColumn);
		setEndRow(endRow);
		setEndColumn(endColumn);
	}

	public DashboardConfig getDashboardConfig() {
		return dashboardConfig;
	}

	public void setDashboardConfig(DashboardConfig dashboardConfig) {
		this.dashboardConfig = dashboardConfig;
	}

	public int getPeriodLengthInMinutes() {
		return periodLengthInMinutes;
	}

	public void setPeriodLengthInMinutes(int periodLengthInMinutes) {
		this.periodLengthInMinutes = periodLengthInMinutes;
	}

	/**
	 * @return true if we want to change timestamps in this panel to align to other panels in the same dashboard
	 */
	public boolean isTimeShift() {
		return timeShift != null && timeShift;
	}

	/**
	 * Provide if we want to change timestamps in this panel to align to other panels in the same dashboard
	 * @param timeShift true = shift/align time, false = use original timestamp from data
	 */
	public void setTimeShift(boolean timeShift) {
		this.timeShift = timeShift;
	}

	@Override
	protected String toString(Object key) {
		if (title != null) {
			return title;
		}
		return super.toString();
	}
}
