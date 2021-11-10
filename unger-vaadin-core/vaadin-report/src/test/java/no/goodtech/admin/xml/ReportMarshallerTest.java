package no.goodtech.admin.xml;

import no.cronus.common.file.FileReader;
import no.cronus.common.file.IFileReader;
import no.cronus.common.logging.LogIt;
import no.goodtech.admin.report.*;
import org.junit.Assert;
import org.junit.Test;

public class ReportMarshallerTest {

	@Test
	public void testFromXMLAndToXML() {
		// Les test fila
		IFileReader fileReader = new FileReader("classpath://report-definition.xml");

		// Un-marshal test XML
		ReportMarshaller reportMarshaller = new ReportMarshaller();
		Reports reports = reportMarshaller.fromXML(fileReader.read());

		// Valider resultatet
		Assert.assertNotNull(reports);

		// Det er 2 rapporter
		Assert.assertEquals(2, reports.getReport().size());

		// Første rapport
		Assert.assertEquals("Report_1", reports.getReport().get(0).getName());
		Assert.assertEquals("Description_1", reports.getReport().get(0).getDescription());
		Assert.assertEquals(2, reports.getReport().get(0).getColumns().getColumn().size());
		Assert.assertEquals("Column_1_1", reports.getReport().get(0).getColumns().getColumn().get(0).getName());
        Assert.assertEquals(TypeType.JAVA_UTIL_DATE, reports.getReport().get(0).getColumns().getColumn().get(0).getType());
        Assert.assertEquals("Some format", reports.getReport().get(0).getColumns().getColumn().get(0).getFormat());
		Assert.assertEquals("Column_1_2", reports.getReport().get(0).getColumns().getColumn().get(1).getName());
        Assert.assertEquals(TypeType.JAVA_LANG_INTEGER, reports.getReport().get(0).getColumns().getColumn().get(1).getType());
        Assert.assertEquals("Another format", reports.getReport().get(0).getColumns().getColumn().get(1).getFormat());
		Assert.assertEquals("sql", reports.getReport().get(0).getDataSetFetcher().getType());
		Assert.assertEquals("select * from Test_1", reports.getReport().get(0).getDataSetFetcher().getQuery());
		Assert.assertEquals(3, reports.getReport().get(0).getDataSetFetcher().getParameters().getTextFieldParameterOrDateParameterOrNativeSelectParameter().size());
		Assert.assertTrue(reports.getReport().get(0).getDataSetFetcher().getParameters().getTextFieldParameterOrDateParameterOrNativeSelectParameter().get(0) instanceof TextFieldParameterType);
		Assert.assertEquals("TextField_1", ((TextFieldParameterType)reports.getReport().get(0).getDataSetFetcher().getParameters().getTextFieldParameterOrDateParameterOrNativeSelectParameter().get(0)).getLabel());
		Assert.assertTrue(reports.getReport().get(0).getDataSetFetcher().getParameters().getTextFieldParameterOrDateParameterOrNativeSelectParameter().get(1) instanceof DateParameterType);
		Assert.assertEquals("Date_1", ((DateParameterType) reports.getReport().get(0).getDataSetFetcher().getParameters().getTextFieldParameterOrDateParameterOrNativeSelectParameter().get(1)).getLabel());
		Assert.assertTrue(reports.getReport().get(0).getDataSetFetcher().getParameters().getTextFieldParameterOrDateParameterOrNativeSelectParameter().get(2) instanceof NativeSelectParameterType);
		Assert.assertEquals("NativeSelect_1", ((NativeSelectParameterType) reports.getReport().get(0).getDataSetFetcher().getParameters().getTextFieldParameterOrDateParameterOrNativeSelectParameter().get(2)).getLabel());
		Assert.assertEquals(2, ((NativeSelectParameterType) reports.getReport().get(0).getDataSetFetcher().getParameters().getTextFieldParameterOrDateParameterOrNativeSelectParameter().get(2)).getList().getElement().size());
		Assert.assertEquals("Key_1_1", ((NativeSelectParameterType) reports.getReport().get(0).getDataSetFetcher().getParameters().getTextFieldParameterOrDateParameterOrNativeSelectParameter().get(2)).getList().getElement().get(0).getKey());
		Assert.assertEquals("Value_1_1", ((NativeSelectParameterType) reports.getReport().get(0).getDataSetFetcher().getParameters().getTextFieldParameterOrDateParameterOrNativeSelectParameter().get(2)).getList().getElement().get(0).getValue());
		Assert.assertEquals("Key_1_2", ((NativeSelectParameterType) reports.getReport().get(0).getDataSetFetcher().getParameters().getTextFieldParameterOrDateParameterOrNativeSelectParameter().get(2)).getList().getElement().get(1).getKey());
		Assert.assertEquals("Value_1_2", ((NativeSelectParameterType)reports.getReport().get(0).getDataSetFetcher().getParameters().getTextFieldParameterOrDateParameterOrNativeSelectParameter().get(2)).getList().getElement().get(1).getValue());

		// Andre rapport
		Assert.assertEquals("Report_2", reports.getReport().get(1).getName());
		Assert.assertEquals("Description_2", reports.getReport().get(1).getDescription());
		Assert.assertEquals(2, reports.getReport().get(1).getColumns().getColumn().size());
		Assert.assertEquals("Column_2_1", reports.getReport().get(1).getColumns().getColumn().get(0).getName());
		Assert.assertEquals("Column_2_2", reports.getReport().get(1).getColumns().getColumn().get(1).getName());
		Assert.assertEquals("sql", reports.getReport().get(1).getDataSetFetcher().getType());
		Assert.assertEquals("select * from Test_2", reports.getReport().get(1).getDataSetFetcher().getQuery());
		Assert.assertEquals(3, reports.getReport().get(1).getDataSetFetcher().getParameters().getTextFieldParameterOrDateParameterOrNativeSelectParameter().size());
		Assert.assertTrue(reports.getReport().get(1).getDataSetFetcher().getParameters().getTextFieldParameterOrDateParameterOrNativeSelectParameter().get(0) instanceof TextFieldParameterType);
		Assert.assertEquals("TextField_2", ((TextFieldParameterType)reports.getReport().get(1).getDataSetFetcher().getParameters().getTextFieldParameterOrDateParameterOrNativeSelectParameter().get(0)).getLabel());
		Assert.assertTrue(reports.getReport().get(1).getDataSetFetcher().getParameters().getTextFieldParameterOrDateParameterOrNativeSelectParameter().get(1) instanceof DateParameterType);
		Assert.assertEquals("Date_2", ((DateParameterType)reports.getReport().get(1).getDataSetFetcher().getParameters().getTextFieldParameterOrDateParameterOrNativeSelectParameter().get(1)).getLabel());
		Assert.assertTrue(reports.getReport().get(1).getDataSetFetcher().getParameters().getTextFieldParameterOrDateParameterOrNativeSelectParameter().get(2) instanceof NativeSelectParameterType);
		Assert.assertEquals("NativeSelect_2", ((NativeSelectParameterType) reports.getReport().get(1).getDataSetFetcher().getParameters().getTextFieldParameterOrDateParameterOrNativeSelectParameter().get(2)).getLabel());
		Assert.assertEquals(2, ((NativeSelectParameterType)reports.getReport().get(0).getDataSetFetcher().getParameters().getTextFieldParameterOrDateParameterOrNativeSelectParameter().get(2)).getList().getElement().size());
		Assert.assertEquals("Key_2_1", ((NativeSelectParameterType) reports.getReport().get(1).getDataSetFetcher().getParameters().getTextFieldParameterOrDateParameterOrNativeSelectParameter().get(2)).getList().getElement().get(0).getKey());
		Assert.assertEquals("Value_2_1", ((NativeSelectParameterType)reports.getReport().get(1).getDataSetFetcher().getParameters().getTextFieldParameterOrDateParameterOrNativeSelectParameter().get(2)).getList().getElement().get(0).getValue());
		Assert.assertEquals("Key_2_2", ((NativeSelectParameterType) reports.getReport().get(1).getDataSetFetcher().getParameters().getTextFieldParameterOrDateParameterOrNativeSelectParameter().get(2)).getList().getElement().get(1).getKey());
		Assert.assertEquals("Value_2_2", ((NativeSelectParameterType)reports.getReport().get(1).getDataSetFetcher().getParameters().getTextFieldParameterOrDateParameterOrNativeSelectParameter().get(2)).getList().getElement().get(1).getValue());

		// Marshal tilbake til XML
		String xml = reportMarshaller.toXML(reports);
		LogIt.log(xml);
	}
}
