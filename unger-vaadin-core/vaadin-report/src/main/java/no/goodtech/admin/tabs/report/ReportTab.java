package no.goodtech.admin.tabs.report;

import com.vaadin.addon.charts.PointClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.themes.BaseTheme;
import no.cronus.common.jasper.JasperReportGenerator;
import no.goodtech.admin.report.*;
import no.goodtech.vaadin.UrlUtils;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.tabs.IMenuView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;

import java.io.ByteArrayInputStream;
import java.util.*;

@UIScope
@SpringView(name = ReportTab.VIEW_ID)
public class ReportTab extends VerticalLayout implements IMenuView {

	public static final String VIEW_ID = "ReportTab";
	public static final String URL_REPORT_PARAMETER = "report";
	public static final String VIEW_NAME = ApplicationResourceBundle.getInstance("vaadin-report").getString("report.viewName");

	private static final Logger logger = LoggerFactory.getLogger(ReportTab.class);
	private final ExcelExporter excelExporter = new ExcelExporter();

	@Autowired
	private volatile ReportManager reportManager;

	private volatile List<List<?>> rows;
	private volatile String reportName;
	private volatile Button queryButton;
	private volatile Button jasperLink;
	private volatile Button csvLink;
	private volatile Button excelLink;
	private final Label rowCount = new Label();
	private User user;
	private String url;

	public ReportTab() {
		addStyleName("reportTab");
		setVisible(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAuthorized(final User user, final String url) {
		this.user = user;
		this.url = url;
		reportName = new UrlUtils(url).getParameter(URL_REPORT_PARAMETER);
		return reportManager.isAuthorized(user, reportName);
	}

	@Override
	public String getViewName() {
		return VIEW_NAME;
	}

	private VerticalLayout createHeader(ReportType report) {
		Label name = new Label(report.getName());
		Label description = new Label(report.getDescription());
		name.setStyleName("reportName");
		description.setStyleName("reportDescription");

		VerticalLayout layout = new VerticalLayout(name, description);
		layout.setMargin(false);
		return layout;
	}

	private void initTab(final String urlParameters) {
		setSizeUndefined();
		final ReportQueryPanel reportQueryPanel = new ReportQueryPanel();
		addComponent(reportQueryPanel);

		// Iterate on the set of reports and create a new button per report
		final ReportType report = reportManager.getReport(reportName);
		if (report != null) {

			addComponent(createHeader(report), 0);

			// Make the vertical layout visible
			setVisible(true);

			// Add the query parameters to the Report Query Panel
			final Map<IReportParameterComponent, String> reportParameterMap = initializeReportParameterList(reportQueryPanel, report);

			setParameterValuesFromURL(urlParameters, reportParameterMap.keySet());

			queryButton = new Button(getResourceString("reportTab.button.query"));
			jasperLink = new Button(getResourceString("report.button.exportToPDF.caption"));
			csvLink = new Button(getResourceString("report.button.exportToCSV.caption"));
			excelLink = new Button(getResourceString("report.button.exportToExcel.caption"));

			// Set style for the rowcount label
			rowCount.addStyleName("rowCount");

			ExcelExporterType excelExporter = null;
			JasperExporterType jasperExporter = null;
			CsvExporterType csvExporter = null;

			if (report.getColumns() != null && report.getColumns().getColumn() != null) {
				excelExporter = initializeExcelExport(report);
				csvExporter = initializeCsvLink(report);
				jasperExporter = initializeJasperLink(report, reportParameterMap);
			}

			// Create and initiate the report table
			ReportTable reportTable = initializeReportTable(report);

			GraphTab graphTab = initializeBarChartPanel(report.getChart());

			//Create and initialize the query button
			initializeQueryButton(report, reportParameterMap, reportTable, graphTab, excelExporter, csvExporter, jasperExporter);

			// Legg til komponentene
			reportQueryPanel.addComponents(queryButton, rowCount, queryButton, csvLink, jasperLink, excelLink);
			reportQueryPanel.setComponentAlignment(rowCount, Alignment.BOTTOM_RIGHT);
			for (Button button : Arrays.asList(queryButton, csvLink, jasperLink, excelLink))
				reportQueryPanel.setComponentAlignment(button, Alignment.BOTTOM_LEFT);
		}
	}

	private void generateTableChartClickListeners(final Map<? extends IReportParameter, String> reportParameterMap, final DrillDownType drillDownType, ReportTable reportTable, GraphTab graphTab){
		// TODO should be possible to add drilldown reports for tables with some modifications.
		if (drillDownType != null && graphTab != null) {
			// Generate click listener for chart
			graphTab.addPointClickListener((PointClickListener) pointClickEvent -> {
				String selectedCategory = pointClickEvent.getCategory();
				String selectedSeries = pointClickEvent.getSeries().getName();
				navigate(reportParameterMap, drillDownType, selectedCategory, selectedSeries);
			});
		}
	}

	/**
	 * If one of the filter parameters is given with a value in the url, the default value is overridden
	 * @param parameters the url
	 * @param reportParameters the parameter components (filter components)
	 */
	private void setParameterValuesFromURL(String parameters, Set<IReportParameterComponent> reportParameters){
		// Set non-default values specified in the url
		UrlUtils utils = new UrlUtils(parameters);
		Map<String, String> paramMap = utils.getParameterMap();
		for (String paramKey : paramMap.keySet()){
			// Check if paramKey exists in report - set default value (if possible)
			for (IReportParameter reportParameter : reportParameters){
				if (Objects.equals(reportParameter.getName(), paramKey)){
					reportParameter.setValueAsString(paramMap.get(paramKey));
				}
			}
		}
	}

	/**
	 * Initialize the reportParameterList
	 *
	 * @param reportQueryPanel panel containing the different parameter selections
	 * @param report           the report containing the specified parameters
	 * @return an ordered map of reportparameters for the specified report. If the parameters have name, the name is in the value part of the map
	 */
	private Map<IReportParameterComponent, String> initializeReportParameterList(ReportQueryPanel reportQueryPanel, ReportType report) {
		final Map<IReportParameterComponent, String> parameters = new LinkedHashMap<>();
		if (report.getDataSetFetcher().getParameters() != null) {
			List<Object> reportParameters = report.getDataSetFetcher().getParameters().getTextFieldParameterOrDateParameterOrNativeSelectParameter();
			ArrayList<SqlSelectParameterType> sqlSelectParametersWithDependency = new ArrayList<>();
			if (reportParameters != null) {
				IReportParameterComponent component = null;
				for (Object reportParameter : reportParameters) {
					if (reportParameter instanceof TextFieldParameterType) {
						component = new TextFieldParameter((TextFieldParameterType) reportParameter);
					} else if (reportParameter instanceof DateParameterType) {
						component = new DateParameter((DateParameterType) reportParameter);
					} else if (reportParameter instanceof NativeSelectParameterType) {
						NativeSelectParameterType type = (NativeSelectParameterType) reportParameter;
						if (type.getList() != null) {
							List<SelectionEntry> selectionEntries = new ArrayList<>();
							for (ElementType element : type.getList().getElement())
								selectionEntries.add(new SelectionEntry(element.getKey(), element.getValue()));
							component = new NativeSelectParameter(type.getLabel(), selectionEntries, type.getName());
						}
					} else if (reportParameter instanceof SqlSelectParameterType) {
						final SqlSelectParameterType parameter = (SqlSelectParameterType) reportParameter;

						if (parameter.getDependency() != null) {
							sqlSelectParametersWithDependency.add(parameter);
						}

						final List<SelectionEntry> keyValue = generateSqlSelectParameterValues(parameter, parameters, report);

						if (ModeType.SINGLE_COLUMN_SELECT.equals(parameter.getMode())) {
							component = new NativeSelectParameter(parameter.getLabel(), keyValue, parameter.getName());
						} else {
							//TODO: Default fungerer ikke
							//  component = new TwinColumnSelectParameter(parameter.getLabel(), keyValue);
							component = new NativeSelectParameter(parameter.getLabel(), keyValue, parameter.getName());
						}
					} else if (reportParameter instanceof WeekNumberType) {
						component = new WeekNumberParameter((WeekNumberType) reportParameter);
					} else if (reportParameter instanceof CurrentUsernameParameterType) {
						component = new CurrentUsernameParameter((CurrentUsernameParameterType) reportParameter, user);
					} else {
						logger.warn("Unsupported parameter type: " + reportParameter);
					}

					if (component != null) {
						String name = component.getName();
						parameters.put(component, name);
						reportQueryPanel.addComponent(component);
						reportQueryPanel.setComponentAlignment(component, Alignment.BOTTOM_LEFT);
						if (name != null && name.trim().length() > 0) {
							String defaultValue = new UrlUtils(url).getParameter(name);
							if (defaultValue != null)
								component.setValueAsString(defaultValue);
						}
					}
				}
				Focusable firstComponent = parameters.keySet().iterator().next();
				if (firstComponent != null)
					firstComponent.focus();

				// Update SqlSelectParameters with dependencies
				for (SqlSelectParameterType sqlSelectParameterType : sqlSelectParametersWithDependency) {
					updateValueChangeListeners(sqlSelectParameterType, parameters, report);
				}
			}
		}
		return parameters;
	}

	/**
	 * Updates the valueChangeListener for an SqlSelectParameterType
	 * @param sqlSelectParameterType the SqlSelectParameterType
	 * @param parameters the list of IReportParameters that contains the dependency parameter
	 */
	private void updateValueChangeListeners(SqlSelectParameterType sqlSelectParameterType, final Map<IReportParameterComponent, String> parameters, ReportType report){
		String dependency = sqlSelectParameterType.getDependency();
		if (dependency == null) return;

		// Find dependency component
		IReportParameterComponent dependencyReportParameter = null;
		for (IReportParameterComponent comp : parameters.keySet()) {
			if (Objects.equals(parameters.get(comp), dependency)) {
				dependencyReportParameter = comp;
			}
		}

		// If dependency component is found, add a new valueChangeListener that updates the content of SqlSelectParameterType
		if (dependencyReportParameter != null) {
			dependencyReportParameter.addValueChangeListener((Property.ValueChangeListener) event -> {
				List<SelectionEntry> values = generateSqlSelectParameterValues(sqlSelectParameterType, parameters, report);

				IReportParameter reportParameter = null;
				for (IReportParameter comp : parameters.keySet()) {
					if (Objects.equals(parameters.get(comp), sqlSelectParameterType.getName())) {
						reportParameter = comp;
					}
				}
				if (reportParameter != null && reportParameter instanceof NativeSelectParameter) {
					NativeSelectParameter reportParam = (NativeSelectParameter) reportParameter;
					reportParam.refreshItems(values);
				}
			});
		}
	}

	/**
	 * Generates new parameter values for SqlSelectParameterTypes
	 * @param parameter the SqlSelectParameterType to get new data
	 * @param reportParameters list of IReportParameters tha may be the dependency of this SqlSelectParameterType
	 * @param report used to fetch the datasource
	 * @return the new list of SelectionEntry
	 */
	private List<SelectionEntry> generateSqlSelectParameterValues(SqlSelectParameterType parameter, Map<IReportParameterComponent, String> reportParameters, ReportType report) {
		Map<IReportParameter, String> parameterMap = new HashMap<>();
		if (parameter.getDependency() != null) {
			// Find the correct IReportParameter with name equals to the dependency of the SqlSelectParameter
			for (IReportParameter reportParam : reportParameters.keySet()) {
				if (Objects.equals(parameter.getDependency(), reportParam.getName())) {
					parameterMap.put(reportParam, reportParam.getName());
				}
			}
			// Add an empty value with name if no parameter with name equal to dependency is found
			if (parameterMap.size() == 0) {
				parameterMap.put(null, parameter.getDependency());
			}
		}

		// Parameters are now fetched, therefore, query for new data
		SqlDataSetFetcher sqlDataSetFetcher = new SqlDataSetFetcher(parameter.getValue(), parameterMap, report.getDataSetFetcher().getDataSource());
		try {
			return sqlDataSetFetcher.getKeyValue();
		}catch (BadSqlGrammarException bsqlge){
			logger.error("Bad grammar in one of the sqls: " + bsqlge.getMessage() + "\nSQL: " + bsqlge.getSql());
			return new ArrayList<>();
		}
	}

	/**
	 * Initialiserer barChartPanelet og komponentene det trenger, og legger de til ReportTab panelet
	 *
	 * @param chartType raport-typen, brukt til å hente ut variable satt via xml i report-definition
	 */
	private GraphTab initializeBarChartPanel(ChartType chartType) {
		if (chartType != null) {
			csvLink.setVisible(false);
			excelLink.setVisible(false);
			jasperLink.setVisible(false);

			final String xAxisTitle = chartType.getXAxisTitle();
			final String yAxisTitle = chartType.getYAxisTitle();
			final GraphTab graphTab = new GraphTab(chartType.getChartTitle(), xAxisTitle, yAxisTitle,
					chartType.getType(), chartType.getAxisType(), chartType.getShowTooltip(), chartType.getStacking(),
					chartType.getExporting());
			graphTab.setSizeFull();
			graphTab.setVisible(false);
			return graphTab;
		}
		return null;
	}

	/**
	 * Initialiserer report-table og tilhørende komponeneter
	 *
	 * @param report rapporten
	 * @return konfigurert reportTable
	 */
	private ReportTable initializeReportTable(final ReportType report) {
		// If no columns or no crosstab --> return null
		if ((report.getColumns() == null || report.getColumns().getColumn() == null) &&
				(report.getChart() == null || !report.getChart().getUseCrossTab())) {
			return null;
		}

		ReportTable reportTable;
		if (report.getChart() != null && report.getChart().getUseCrossTab()) {
			reportTable = new ReportTable();
		} else {
			reportTable = new ReportTable(report.getColumns().getColumn());
			// Set column alignment
			for (ColumnType column : report.getColumns().getColumn()) {
				Table.Align align = determineColumnAlignment(column);
				reportTable.setColumnAlignment(column.getName(), align);
			}

			if (report.getColumns().getSortDisabled()) {
				reportTable.setSortEnabled(false);
			} else {
				reportTable.setSortEnabled(true);
			}
		}

		reportTable.setSizeFull();
		reportTable.addStyleName("reportTable");
		reportTable.setImmediate(true);
		reportTable.setColumnReorderingAllowed(false);

		// Make the reportTablePanel invisible
		reportTable.setVisible(false);

		return reportTable;
	}

	private List<ColumnType> getCrossTableColumns() {
		List<ColumnType> columnTypes = new ArrayList<>();
		ColumnType emptyColumn = new ColumnType();
		emptyColumn.setAlignment(AlignmentType.LEFT);
		emptyColumn.setName("");
		columnTypes.add(emptyColumn);

		List<String> columns = new ArrayList<>();
		for (List<?> row : rows) {
			if (row != null && row.size() == 3) {
				String name = (row.get(1) != null) ? row.get(1).toString() : null;
				if (name != null && !columns.contains(name)) {
					columns.add(name);
				}
			}
		}

		for (String columnName : columns) {
			ColumnType columnType = new ColumnType();
			columnType.setAlignment(AlignmentType.RIGHT);
			columnType.setName(columnName);
			columnTypes.add(columnType);
		}
		return columnTypes;
	}

	/**
	 * Initialiserer query knappen(Søk) og setter hvilke komponenter som skal være synlige
	 */
	private void initializeQueryButton(final ReportType report, final Map<? extends IReportParameter, String> reportParameterList, final ReportTable reportTable, final GraphTab reportGraph, final ExcelExporterType excelExporter, final CsvExporterType csvExporter, final JasperExporterType jasperExporter) {
		queryButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		queryButton.addStyleName("primary");

		queryButton.addClickListener(event -> {
			setSizeFull();

			if (reportTable != null && reportGraph != null) {
				reportTable.setVisible(true);
				reportGraph.setVisible(true);
				addReportTableAndReportGraph(report.getChart(), reportTable, reportGraph);
			} else if (reportTable != null) {
				addComponent(reportTable);
				setExpandRatio(reportTable, 1.0f);
			} else if (reportGraph != null) {
				addComponent(reportGraph);
				setExpandRatio(reportGraph, 1.0f);
			}

			if (!report.getDataSetFetcher().getShowRowCount()) {
				rowCount.setVisible(false);
			}

			// Get the table content
			rows = createFetcher(report.getDataSetFetcher(), reportParameterList).getRows();

			if (reportGraph != null) {
				ArrayList<IReportParameter> parameters = new ArrayList<IReportParameter>(reportParameterList.keySet());
				reportGraph.setChartTitle(compileHeader(report.getChart().getChartTitle(), parameters));
				reportGraph.setVisible(true);
				reportGraph.buildGraph(rows);
			}

			if (reportTable != null) {
				reportTable.setVisible(true);
				buildTable(report, reportTable);
			}

			generateTableChartClickListeners(reportParameterList, report.getDrillDown(), reportTable, reportGraph);

			// Set the row count
			rowCount.setValue(rows.size() + " " + getResourceString("report.label.rowCount"));

			if (rows.size() > 0) {
				if (csvExporter != null) {
					csvLink.setVisible(true);
				}

				if (jasperExporter != null) {
					jasperLink.setVisible(true);
				}

				if (excelExporter != null) {
					excelLink.setVisible(true);
				}
			}
		});
	}

	/**
	 * Replace all parameter place holders in given header text with current parameter values
	 */
	private String compileHeader(String header, List<IReportParameter> parameters) {
		String result = header;
		for (IReportParameter parameter : parameters) {
			String name = ":" + parameter.getName();
			String value = parameter.getNiceValue();
			if (value == null)
				result = result.replaceAll(name, "");
			else
				result = result.replaceAll(name, value);
		}
		return result;
	}

	/**
	 * Populates the table. Creates a crosstab if getUseCrossTab is true.
	 */
	private void buildTable(ReportType report, ReportTable reportTable) {
		if (report.getChart() != null && report.getChart().getUseCrossTab()) {
			List<ColumnType> columns = getCrossTableColumns();
			List<List<?>> crossTableRows = getCrossTableRows(columns);
			reportTable.setColumns(columns);

			IndexedContainer newDataSource = ReportList.getContainer(columns, crossTableRows);
			reportTable.setContainerDataSource(newDataSource);
		} else if (report.getColumns() != null && report.getColumns().getColumn() != null) {
			IndexedContainer newDataSource = ReportList.getContainer(report.getColumns().getColumn(), rows);
			reportTable.setContainerDataSource(newDataSource);
			applyTableFooter(report, reportTable);
		}
	}

	private void applyTableFooter(ReportType report, ReportTable reportTable) {
		boolean setFooterVisible = false;

		//apply hard-coded summary texts from template
		for (ColumnType columnType : report.getColumns().getColumn()) {
			if (columnType.getSummaryText() != null) {
				reportTable.setColumnFooter(columnType.getName(), columnType.getSummaryText());
				setFooterVisible = true;
			}
		}

		int columnIndex = 0;
		for (ColumnType columnType : report.getColumns().getColumn()) {
			SummaryMethod summaryMethod = columnType.getSummaryMethod();
			//SummmaryMethod SUM and AVG
			if (summaryMethod != null && (summaryMethod.equals(SummaryMethod.SUM) || summaryMethod.equals(SummaryMethod.AVG))) {
				String columnName = columnType.getName();
				Double columnValueSum = null;
				int columnValueCount = 1;
				for (List<?> row : rows) {
					final Object columnObject = row.get(columnIndex);
					if (columnObject != null && columnObject instanceof Number) {
						double columnValue = ((Number) columnObject).doubleValue();
						if (columnValueSum == null) {
							columnValueSum = columnValue;
						} else {
							columnValueSum = columnValueSum + columnValue;
							if (summaryMethod.equals(SummaryMethod.AVG)) {
								columnValueCount++;
							}
						}
					}
				}
				reportTable.setColumnFooter(columnName, reportTable.format(columnValueSum == null ? null : columnValueSum / columnValueCount, columnName));
				setFooterVisible = true;
			}
			columnIndex++;
		}
		reportTable.setFooterVisible(setFooterVisible);
	}

	/**
	 * Adds reportTable and reportGraph in the right position
	 */
	private void addReportTableAndReportGraph(ChartType chartType, ReportTable reportTable, GraphTab reportGraph) {
		if (reportTable != null && reportGraph != null) {
			// Place table and graph
			AlignComponent alignComponent = chartType.getAlignTable();
			if (alignComponent.equals(AlignComponent.TOP)) {
				addComponents(reportTable, reportGraph);
				setExpandRatio(reportTable, 1.0f);
				setExpandRatio(reportGraph, 1.0f);
			} else if (alignComponent.equals(AlignComponent.BOTTOM)) {
				addComponents(reportGraph, reportTable);
				setExpandRatio(reportTable, 1.0f);
				setExpandRatio(reportGraph, 1.0f);
			} else {
				HorizontalLayout mainHorizontalLayout = new HorizontalLayout();
				mainHorizontalLayout.setSizeFull();
				mainHorizontalLayout.setMargin(false);
				if (alignComponent.equals(AlignComponent.LEFT)) {
					mainHorizontalLayout.addComponents(reportTable, reportGraph);
				} else {
					mainHorizontalLayout.addComponents(reportGraph, reportTable);
				}
				addComponent(mainHorizontalLayout);
				mainHorizontalLayout.setExpandRatio(reportTable, 1.0f);
				mainHorizontalLayout.setExpandRatio(reportGraph, 1.0f);
				setExpandRatio(mainHorizontalLayout, 1.0f);
			}
		}
	}

	/**
	 * Creates a list of rows with rowId for the crosstable based on the columns
	 * row.get(0) = x-axis in table (rowId)
	 * row.get(1) = category (columns)
	 * row.get(2) = value
	 *
	 * @param columns list of all columns in table (first empty column included)
	 * @return list of all rows (data in each row is represented by a list)
	 */
	private List<List<?>> getCrossTableRows(List<ColumnType> columns) {
		HashMap<String, List<Object>> hashMapRows = new HashMap<>();
		// Initialize map with correct number of null-values
		for (List<?> row : rows) {
			if (row != null && row.get(0) != null && row.get(0).toString().length() > 0) {
				String rowId = row.get(0).toString();
				if (hashMapRows.get(rowId) == null) {
					List<Object> values = new ArrayList<>();
					values.add(rowId);
					for (int i = 1; i < columns.size(); i++) {
						values.add(null);
					}
					hashMapRows.put(rowId, values);
				}
			}
		}

		// Updates all values
		for (String rowId : hashMapRows.keySet()) {
			for (List<?> row : rows) {
				if (row != null && row.size() == 3 && row.get(0) != null && row.get(1) != null && row.get(0).toString().equals(rowId)) {
					int columnIndex = indexOfColumn(columns, row.get(1).toString());
					if (columnIndex != -1) {
						hashMapRows.get(rowId).remove(columnIndex);
						hashMapRows.get(rowId).add(columnIndex, row.get(2));
					}
				}
			}
		}

		// Create correct data for crosstable rows
		List<List<?>> crossTableRows = new ArrayList<>();
		for (String key : hashMapRows.keySet()) {
			crossTableRows.add(new ArrayList<>(hashMapRows.get(key)));
		}
		return crossTableRows;
	}

	/**
	 * Return the index of the column based on the name of the ColumnType
	 *
	 * @param columns all columns in a list (ColumnType)
	 * @param value   the name of the column
	 * @return the index. Returns -1 if item is not found
	 */
	private int indexOfColumn(List<ColumnType> columns, String value) {
		for (ColumnType column : columns) {
			if (column.getName().equals(value))
				return columns.indexOf(column);
		}
		return -1;
	}

	/**
	 * Initialiserer jasper Exporten og binder clicklistener til knappen
	 *
	 * @param report              rapporten
	 * @param reportParameterList parametere brukt når knappen klikkes
	 * @return Jasper-Eksporteren
	 */
	private JasperExporterType initializeJasperLink(final ReportType report, final Map<? extends IReportParameter, String> reportParameterList) {
		jasperLink.setStyleName(BaseTheme.BUTTON_LINK);
		jasperLink.setDescription(getResourceString("report.button.exportToPDF.description"));
		jasperLink.setVisible(false);
		final JasperExporterType jasperExporter = report.getJasperExporter();
		if (jasperExporter != null) {
			StreamResource.StreamSource source = () -> {
				// Add all data for the report
				List<Map<String, ?>> maps = new ArrayList<>();
				List<ColumnType> columnList = report.getColumns().getColumn();
				List<List<?>> rowList = createFetcher(report.getDataSetFetcher(), reportParameterList).getRows();
				for (List<?> list : rowList) {
					Map<String, Object> map = new HashMap<>();
					for (ColumnType parameter : columnList) {
						int index = columnList.indexOf(parameter);
						if (list.size() > index) {
							map.put(parameter.getName(), list.get(index));
						}
					}
					maps.add(map);
				}
				return new ByteArrayInputStream(new JasperReportGenerator().create(maps, jasperExporter.getFile()));
			};

			// Return the content as a PDF file
			StreamResource resource = new StreamResource(source, "data.pdf");
			resource.setCacheTime(0);
			resource.setMIMEType("application/pdf");

			// Create the FileDownloader and attach to 'jasperLink'
			FileDownloader fileDownloader = new FileDownloader(resource);
			fileDownloader.extend(jasperLink);
		}
		return jasperExporter;
	}

	/**
	 * Initialiserer CSV eksporten og binder knappen til en clickevent
	 *
	 * @param report rapporten
	 * @return csv eksporteren
	 */
	private CsvExporterType initializeCsvLink(final ReportType report) {
		csvLink.setStyleName(BaseTheme.BUTTON_LINK);
		csvLink.setDescription(getResourceString("report.button.exportToCSV.description"));
		csvLink.setVisible(false);
		final CsvExporterType csvExporter = report.getCsvExporter();
		if (csvExporter != null) {
			StreamResource.StreamSource source = () -> new ByteArrayInputStream(CSVFile.create(report.getColumns().getColumn(), rows));

			// Return the content as a CSV file
			StreamResource resource = new StreamResource(source, "data.csv");
			resource.setCacheTime(0);
			resource.setMIMEType(csvExporter.getMimeType());

			// Create the FileDownloader and attach to 'csvLink'
			FileDownloader fileDownloader = new FileDownloader(resource);
			fileDownloader.extend(csvLink);
		}
		return csvExporter;
	}

	/**
	 * Initialiserer ExcelExporteren
	 *
	 * @param report rapporten
	 * @return excel exporter
	 */
	private ExcelExporterType initializeExcelExport(final ReportType report) {
		excelLink.setStyleName(BaseTheme.BUTTON_LINK);
		excelLink.setDescription(getResourceString("report.button.exportToExcel.description"));
		excelLink.setVisible(false);
		final ExcelExporterType excelExporterType = report.getExcelExporter();
		if (excelExporterType != null) {
			StreamResource.StreamSource source = () -> new ByteArrayInputStream(excelExporter.create(report.getColumns().getColumn(), rows));

			// Return the content as a Excel file
			StreamResource resource = new StreamResource(source, "data.xlsx");
			resource.setCacheTime(0);
			resource.setMIMEType("application/vnd.ms-excel");

			// Create the FileDownloader and attach to 'excelLink'
			FileDownloader fileDownloader = new FileDownloader(resource);
			fileDownloader.extend(excelLink);
		}
		return excelExporterType;
	}

	private static String getResourceString(String resourceName) {
		return ApplicationResourceBundle.getInstance("vaadin-report").getString(resourceName);
	}

	private Table.Align determineColumnAlignment(final ColumnType column) {
		// Default alignment
		Table.Align alignment = Table.Align.LEFT;

		// Default per column type
		TypeType type = column.getType();
		if (type != null) {
			if (type.equals(TypeType.JAVA_LANG_SHORT) ||
					type.equals(TypeType.JAVA_LANG_INTEGER) ||
					type.equals(TypeType.JAVA_LANG_LONG) ||
					type.equals(TypeType.JAVA_LANG_NUMBER) ||
					type.equals(TypeType.JAVA_LANG_FLOAT) ||
					type.equals(TypeType.JAVA_LANG_DOUBLE)) {
				alignment = Table.Align.RIGHT;
			}
		}

		// Configuration override
		AlignmentType alignmentType = column.getAlignment();
		if (alignmentType != null) {
			if (alignmentType.equals(AlignmentType.LEFT)) {
				alignment = Table.Align.LEFT;
			} else if (alignmentType.equals(AlignmentType.RIGHT)) {
				alignment = Table.Align.RIGHT;
			} else if (alignmentType.equals(AlignmentType.CENTER)) {
				alignment = Table.Align.CENTER;
			}
		}

		return alignment;
	}

	private boolean isRunAutomatic(final String urlParameters) {
		final ReportType report = reportManager.getReport(reportName);
		if (report == null)
			return false;
		if (report.getDataSetFetcher().getParameters() == null) //run report immediately if report has no parameters to fill in
			return true;
		String immediate = new UrlUtils(urlParameters).getParameter("run");
		return immediate != null && "true".equals(immediate);
	}

	@Override
	public void enter(final ViewChangeListener.ViewChangeEvent event) {
		removeAllComponents();
		String parameters = event.getParameters();
		reportName = new UrlUtils(parameters).getParameter(URL_REPORT_PARAMETER);
		initTab(parameters);
		if (isRunAutomatic(parameters))
			queryButton.click();
	}

	private SqlDataSetFetcher createFetcher(DataSetFetcherType dataSetFetcherType, final Map<? extends IReportParameter, String> reportParameters) {
		return new SqlDataSetFetcher(dataSetFetcherType.getQuery(), reportParameters, dataSetFetcherType.getDataSource());
	}

	/**
	 * Navigates to another view with url parameters
	 */
	private void navigate(final Map<? extends IReportParameter, String> reportParameterMap, final DrillDownType drillDownType, String ... selectedValues){
		String url = drillDownType.getUrl();

		for (int i = 0; i < selectedValues.length; i++) {
			String regCheck = "#" + i;
			if (url.contains(regCheck)){
				url = url.replaceAll(regCheck, selectedValues[i]);
			}
		}

		// Add selected filter values
		for (FilterParameterType filterParameterType : drillDownType.getFilterParameter()) {
			String filterParameterId = filterParameterType.getUrlParameterId();
			String filterComponentName = (filterParameterType.getFilterParameterName() == null) ? filterParameterId : filterParameterType.getFilterParameterName();
			String filterParameterValue = "";
			for (IReportParameter iReportParameter : reportParameterMap.keySet()) {
				if (Objects.equals(iReportParameter.getName(), filterComponentName)){
					filterParameterValue = iReportParameter.getValueAsString();
					break;
				}
			}
			url += filterParameterId + UrlUtils.DEFAULT_PARAMETER_KEY_VALUE_DELIMITER + filterParameterValue + UrlUtils.DEFAULT_PARAMETER_DELIMITER;
		}

		UI.getCurrent().getNavigator().navigateTo(url);
	}
}
