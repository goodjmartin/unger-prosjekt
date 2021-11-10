package no.goodtech.vaadin;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TreeElement;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.LocalServerPort;

import java.util.List;
import java.util.NoSuchElementException;

public class AutomaticUITestBase extends TestBenchTestCase {

	protected static Logger LOGGER = LoggerFactory.getLogger(AutomaticUITestBase.class);

	public enum DriverTypes {
		CHROME,
		FIREFOX,
		EDGE,
		PHANTOMJS;
	}

	protected static Actions actions;
	protected static WebDriver driver;
	protected static DriverTypes selectedDriver = DriverTypes.PHANTOMJS;

	protected static String CONTEXT_PATH = "/gmi/";
	protected static String BASE_URL = "http://localhost:";

	protected static String MENU_ID = "menuTree";
	protected static String REFRESH_BUTTON_ID = "refreshButton";
	protected static String TAB_ITEM_CLASS = ".v-tabsheet-tabitemcell";
	protected static String TREE_NODE_LEAF_CLASS = "v-tree-node-leaf";
	protected static String TREE_NODE_ROOT_CLASS = "v-tree-node-root";
	protected static String TREE_NODE_EXPANDED_CLASS = "v-tree-node-expanded";
	protected static String MONKEY_SEARCH_STRING = "errorWindow";

	@LocalServerPort
	protected int port;

	protected String currentView = "";

	@Before
	public void setUp() throws Exception {
		if (selectedDriver == DriverTypes.CHROME) {
			// Install ChromeDriver at: https://sites.google.com/a/chromium.org/chromedriver/downloads
			// This is buggy
			driver = TestBench.createDriver(new ChromeDriver());
		}else if (selectedDriver == DriverTypes.FIREFOX){
			// Install FirefoxDriver at: https://github.com/mozilla/geckodriver/releases
			// This is buggy
			driver = TestBench.createDriver(new FirefoxDriver());
		}else if (selectedDriver == DriverTypes.EDGE){
			// Install EdgeDriver at: https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/
			// With visualization
			driver = TestBench.createDriver(new EdgeDriver());
		}else {
			// Install PhantomJSDriver at: http://phantomjs.org/download.html
			// Runs with no visual cues
			driver = TestBench.createDriver(new PhantomJSDriver());
		}

		setDriver(driver);
		actions = new Actions(driver);
		TestBenchTestCase.testBench(driver).waitForVaadin();
		LOGGER.info("Running server on port: " + port);
	}

	@AfterClass
	static public void tearDown() throws Exception {
		driver.quit();
	}

	/**
	 * Navigates through all the views in the menu and searches for the monkey
	 * @param exclusions - exclude certain views with matching menu caption
	 */
	protected void navigateToAllMenuViews(String... exclusions) {
		// Load main view
		driver.get(BASE_URL + port + CONTEXT_PATH);

		// Count number of menu items
		List<WebElement> selectedElements = fetchTreeLeaves(MENU_ID);
		int nbMenuItems = selectedElements.size();
		LOGGER.info("Checking " + nbMenuItems + " menu items...");

		// Iterating through menu tree leaves
		for (int i = 0; i < nbMenuItems; i++) {
			selectedElements = fetchTreeLeaves(MENU_ID);
			Assert.assertEquals(nbMenuItems, selectedElements.size());
			WebElement webElement = selectedElements.get(i);
			currentView = webElement.getText();
			currentView = currentView.trim().replaceAll("\n", "");

			boolean checkThisView = true;
			for (String exclusion : exclusions) {
				if (currentView.equals(exclusion)) {
					checkThisView = false;
					break;
				}
			}

			if (checkThisView) {
				LOGGER.info("Checking: " + currentView);
				actions.moveToElement(webElement).build().perform();
				actions.click(webElement).perform();
				testEachView();
			} else {
				LOGGER.info("Skipping UI test for " + currentView);
			}
		}
	}

	protected void navigateToASingleMenuView(String view){
		// Count number of menu items
		List<WebElement> selectedElements = fetchTreeLeaves(MENU_ID);
		int nbMenuItems = selectedElements.size();
		LOGGER.info("Checking " + nbMenuItems + " menu items...");

		// Iterating through menu tree leaves
		for (int i = 0; i < nbMenuItems; i++) {
			selectedElements = fetchTreeLeaves(MENU_ID);
			Assert.assertEquals(nbMenuItems, selectedElements.size());
			WebElement webElement = selectedElements.get(i);
			currentView = webElement.getText();
			currentView = currentView.trim().replaceAll("\n", "");

			// Testing the selected view
			if (currentView.equals(view)) {
				LOGGER.info("Checking: " + currentView);
				actions.moveToElement(webElement).build().perform();
				actions.click(webElement).perform();
				testEachView();
			}
		}
	}

	protected void testUrls(List<String> urls){
		for (String url : urls){
			// Load main view
			driver.get(BASE_URL + port + CONTEXT_PATH + UrlUtils.VIEW_PREFIX + url);
			testEachView();
		}
	}

	/**
	 * Test each individual view. Checks for monkey, tries the refresh button and navigates through tabs
	 */
	protected void testEachView() {
		checkForMonkey();

		testRefreshButton();

		testTabs();
	}

	/**
	 * Navigates through each tab in the view. Each tab is tested for monkey and refresh button
	 */
	protected void testTabs() {
		List<WebElement> tabs = driver.findElements(By.cssSelector(TAB_ITEM_CLASS));
		int nbTabs = tabs.size();
		if (nbTabs > 0)
			LOGGER.debug("Checking " + nbTabs + " tabs...");

		for (int i = 0; i < nbTabs; i++) {
			tabs = driver.findElements(By.cssSelector(TAB_ITEM_CLASS));
			WebElement selectedTab = tabs.get(i);
			actions.moveToElement(selectedTab).build().perform();
			actions.click(selectedTab).perform();
			checkForMonkey();
			testRefreshButton();
		}
	}

	/**
	 * Searches for a refresh button and clicks it if found
	 */
	protected void testRefreshButton() throws NoSuchElementException {
		if (driver.getPageSource().contains(REFRESH_BUTTON_ID)) {
			ButtonElement refreshButton = $(ButtonElement.class).id(REFRESH_BUTTON_ID);
			actions.moveToElement(refreshButton).build().perform();
			actions.click(refreshButton).perform();
			checkForMonkey();
		}
	}

	/**
	 * Check the current view for any monkey
	 */
	protected void checkForMonkey() {
		String htmlSource = driver.getPageSource();
		if (htmlSource.contains(MONKEY_SEARCH_STRING)) {
			LOGGER.error("Error (monkey) in: " + currentView);
		}
		Assert.assertEquals(false, htmlSource.contains(MONKEY_SEARCH_STRING));
	}

	/**
	 * Open groups and fetch all tree leaves in a tree with treeId
	 */
	protected List<WebElement> fetchTreeLeaves(final String treeId) {
		expandGroups(treeId);
		TreeElement menuTree = $(TreeElement.class).id(treeId);
		return menuTree.findElements(By.className(TREE_NODE_LEAF_CLASS));
	}

	/**
	 * Open groups in a tree based on a tree id
	 */
	protected void expandGroups(String treeId) {
		TreeElement menuTree = $(TreeElement.class).id(treeId);
		List<WebElement> leaves = menuTree.findElements(By.className(TREE_NODE_LEAF_CLASS));
		List<WebElement> groupElements = menuTree.findElements(By.className(TREE_NODE_ROOT_CLASS));
		groupElements.removeAll(leaves);
		int nbGroups = groupElements.size();

		for (int i = 0; i < nbGroups; i++) {
			menuTree = $(TreeElement.class).id(treeId);
			leaves = menuTree.findElements(By.className(TREE_NODE_LEAF_CLASS));
			groupElements = menuTree.findElements(By.className(TREE_NODE_ROOT_CLASS));
			List<WebElement> expandedGroups = menuTree.findElements(By.className(TREE_NODE_EXPANDED_CLASS));
			groupElements.removeAll(leaves);

			WebElement currentGroup = groupElements.get(nbGroups - 1 - i);
			if (!expandedGroups.contains(currentGroup)) {
				LOGGER.debug("Opening group: " + currentGroup.getText());
				actions.moveToElement(currentGroup).build().perform();
				actions.click(currentGroup).perform();
			}
		}
	}
}
