package no.goodtech.vaadin;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import no.goodtech.vaadin.utils.Utils;

import org.junit.Assert;
import org.junit.Test;

public class UrlUtilsTest {

	@Test
	public void testCreateUrl() {
		final UrlUtils utils = new UrlUtils();
		Assert.assertEquals("view/color", utils.createUrl("view", "color"));
		Assert.assertEquals("view/color=green", utils.createUrl("view", "color", "green"));
		Assert.assertEquals("view/color=green/size", utils.createUrl("view", "color", "green", "size"));
		Assert.assertEquals("view/color=green/size=small", utils.createUrl("view", "color", "green", "size", "small"));
		Assert.assertEquals("view/null=green", utils.createUrl("view", null, "green"));
		
		Assert.assertEquals("view/answer=42", utils.createUrl("view", "answer", 42));
		Assert.assertEquals("view/empty", utils.createUrl("view", "empty"));
	}
	
	@Test
	public void testGetParameter() {
		Assert.assertNull(new UrlUtils("").getParameter("pk"));
		Assert.assertNull(new UrlUtils("pk=").getParameter("pk"));
		Assert.assertNull(new UrlUtils("pk=    ").getParameter("pk"));
		Assert.assertNull(new UrlUtils("pk=null").getParameter("pk"));
		Assert.assertNull(new UrlUtils("").getParameter(""));
		Assert.assertNull(new UrlUtils("color").getParameter("size"));
		Assert.assertNull(new UrlUtils("color").getParameter("color"));
		Assert.assertEquals("green", new UrlUtils("color=green").getParameter("color"));
		Assert.assertEquals("green", new UrlUtils("color=green/size=small").getParameter("color"));
		Assert.assertEquals("small", new UrlUtils("color=green/size=small").getParameter("size"));
	}

	@Test
	public void testGetDateParameter() throws ParseException {
		Assert.assertEquals(createDate("2016-03-31"), new UrlUtils("date=2016-03-31").getDateParameter("date", Utils.DATE_FORMAT));
		Assert.assertEquals(createTime("2016-03-31 08:15"), new UrlUtils("date=2016-03-31 08:15").getDateParameter("date", Utils.DATETIME_FORMAT));
		try {
			new UrlUtils("date=31.03.16").getDateParameter("date", Utils.DATE_FORMAT); //illegal format	
		} catch (NullPointerException e) {
			//because a unit test is not allow to show vaadin notifications
		}
		
	}

	@Test
	public void testGetParameterMap(){
		String param1 = "color";
		String param1_val = "green";
		String param2 = "date";
		String param2_val = "2016-02-25";
		UrlUtils urlUtils = new UrlUtils(param1 + "=" + param1_val +"/" + param2 + "=" + param2_val);

		Map<String, String> parameterMap = urlUtils.getParameterMap();
		Assert.assertEquals(2, parameterMap.size());
		Assert.assertEquals(param1_val, parameterMap.get(param1));
		Assert.assertEquals(param2_val, parameterMap.get(param2));
	}

	private Date createDate(String string) throws ParseException {
		return Utils.DATE_FORMATTER.parse(string);
	}

	private Date createTime(String string) throws ParseException {
		return Utils.DATETIME_FORMATTER.parse(string);
	}

	@Test
	public void testGetParameters() {
		final UrlUtils utils = new UrlUtils();

		//empty list if parameter not found
		Assert.assertEquals(0, utils.getParameters("pk", "").size()); 
		Assert.assertEquals(0, utils.getParameters("pk", "pk").size()); //empty list if parameter not found
		Assert.assertEquals(0, utils.getParameters("pk", "pk=").size()); //empty list if parameter not found
		final List<String> result = utils.getParameters("pk", "pk=[]");
		Assert.assertEquals(0, result.size()); //empty list if parameter not found
		
		Assert.assertEquals(0, result.size()); //empty list if parameter not found
		
		Assert.assertEquals(1, utils.getParameters("pk", "pk=[a]").size()); 
		Assert.assertEquals(Arrays.asList("a"), utils.getParameters("pk", "pk=[a]")); 
		Assert.assertEquals(2, utils.getParameters("pk", "pk=[a,b]").size()); 
		Assert.assertEquals(Arrays.asList("a", "b"), utils.getParameters("pk", "pk=[a,b]")); 		
	}
	
	@Test
	public void testIsParameterPresent() {
		Assert.assertFalse(new UrlUtils().isParameterPresent("pk", null));
		Assert.assertFalse(new UrlUtils("").isParameterPresent("pk"));
		Assert.assertFalse(new UrlUtils("notPk").isParameterPresent("pk"));
		Assert.assertFalse(new UrlUtils("notPk=pk").isParameterPresent("pk"));
		Assert.assertTrue(new UrlUtils("pk").isParameterPresent("pk"));
		Assert.assertTrue(new UrlUtils("pk=").isParameterPresent("pk"));
		Assert.assertTrue(new UrlUtils("pk=null").isParameterPresent("pk"));
		Assert.assertTrue(new UrlUtils("pk=present").isParameterPresent("pk"));
	}

}
