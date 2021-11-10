package no.goodtech.vaadin.validation;

import com.vaadin.v7.data.Validator;
import com.vaadin.v7.ui.TextField;
import org.junit.Assert;
import org.junit.Test;

public class CompareNumericFieldsValidatorTest {

	private TextField fieldToValidate = new TextField("fieldToValidate");
	private TextField otherField = new TextField("otherField");
	private CompareNumericFieldsValidator biggerThanValidator = new CompareNumericFieldsValidator(otherField, CompareNumericFieldsValidator.Rule.BIGGER_THAN);
	private CompareNumericFieldsValidator biggerThanOrEqualValidator = new CompareNumericFieldsValidator(otherField, CompareNumericFieldsValidator.Rule.BIGGER_THAN_OR_EQUAL);
	private CompareNumericFieldsValidator lessThanValidator = new CompareNumericFieldsValidator(otherField, CompareNumericFieldsValidator.Rule.LESS_THAN);
	private CompareNumericFieldsValidator lessThanOrEqualValidator = new CompareNumericFieldsValidator(otherField, CompareNumericFieldsValidator.Rule.LESS_THAN_OR_EQUAL);

	@Test
	public void testBiggerThan() {
		fieldToValidate.setValue("42");
		otherField.setValue("2");
		fieldToValidate.addValidator(biggerThanValidator);
		fieldToValidate.validate(); //should be ok

		fieldToValidate.setValue("2");
		assertValidationFail();

		fieldToValidate.setValue("1");
		assertValidationFail();
	}

	@Test
	public void testBiggerThanOrEqual() {
		fieldToValidate.setValue("42");
		otherField.setValue("2");
		fieldToValidate.addValidator(biggerThanOrEqualValidator);
		fieldToValidate.validate(); //should be ok

		fieldToValidate.setValue("2");
		fieldToValidate.validate(); //should be ok

		fieldToValidate.setValue("1.9");
		assertValidationFail();
	}

	@Test
	public void testLessThan() {
		fieldToValidate.setValue("2");
		otherField.setValue("42");
		fieldToValidate.addValidator(lessThanValidator);
		fieldToValidate.validate(); //should be ok

		fieldToValidate.setValue("42");
		assertValidationFail();

		fieldToValidate.setValue("42.1");
		assertValidationFail();
	}

	@Test
	public void testLessThanOrEqual() {
		fieldToValidate.setValue("2");
		otherField.setValue("42");
		fieldToValidate.addValidator(lessThanOrEqualValidator);
		fieldToValidate.validate(); //should be ok

		fieldToValidate.setValue("42");
		fieldToValidate.validate(); //should be ok

		fieldToValidate.setValue("42.1");
		assertValidationFail();
	}

	private void assertValidationFail() {
		try {
			fieldToValidate.validate();
			Assert.fail(); //should fail
		} catch (Validator.InvalidValueException e) {
		}
	}
}
