package no.goodtech.vaadin.chart;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import org.jfree.chart.ChartColor;
import org.jfree.io.SerialUtilities;
import org.jfree.util.ShapeUtilities;
import org.jfree.chart.plot.DefaultDrawingSupplier;

/**
 * A high-contrast implementation of the DrawingSupplier interface.
 */
public class HighContrastDrawingSupplier extends DefaultDrawingSupplier {

	/**
	 * For serialization.
	 */
	private static final long serialVersionUID = 3211543518274706195L;

	/**
	 * The default fill paint sequence.
	 */
	public static final Paint[] HIGH_CONTRAST_PAINT_SEQUENCE
			= createHighContrastPaintArray();

	/**
	 * Convenience method to return an array of <code>Paint</code> objects that
	 * represent the pre-defined colors in the <code>Color<code> and
	 * <code>ChartColor</code> objects.
	 *
	 * @return An array of objects with the <code>Paint</code> interface.
	 */
	public static Paint[] createHighContrastPaintArray() {

		return new Paint[]{
				new Color(0x55, 0x55, 0xFF),
				ChartColor.DARK_RED,
				ChartColor.DARK_BLUE,
				ChartColor.DARK_GREEN,
				ChartColor.DARK_YELLOW,
				ChartColor.DARK_MAGENTA,
				ChartColor.DARK_CYAN,
				Color.darkGray,
		};
	}

	/**
	 * The paint sequence.
	 */
	private transient Paint[] paintSequence;
	/**
	 * The outline paint sequence.
	 */
	private transient Paint[] outlinePaintSequence;

	/**
	 * The stroke sequence.
	 */
	private transient Stroke[] strokeSequence;

	/**
	 * The outline stroke sequence.
	 */
	private transient Stroke[] outlineStrokeSequence;
	/**
	 * The current outline stroke index.
	 */

	/**
	 * The shape sequence.
	 */
	private transient Shape[] shapeSequence;

	/**
	 * Creates a new supplier, with default sequences for fill paint, outline
	 * paint, stroke and shapes.
	 */
	public HighContrastDrawingSupplier() {
		super(HIGH_CONTRAST_PAINT_SEQUENCE,
				DEFAULT_OUTLINE_PAINT_SEQUENCE,
				DEFAULT_STROKE_SEQUENCE,
				DEFAULT_OUTLINE_STROKE_SEQUENCE,
				DEFAULT_SHAPE_SEQUENCE);
	}

	/**
	 * Tests this object for equality with another object.
	 *
	 * @param obj the object (<code>null</code> permitted).
	 * @return A boolean.
	 */
	public boolean equals(Object obj) {

		if (obj == this) {
			return true;
		}

		if (!(obj instanceof HighContrastDrawingSupplier)) {
			return false;
		}

		HighContrastDrawingSupplier that = (HighContrastDrawingSupplier) obj;

		if (!Arrays.equals(this.paintSequence, that.paintSequence)) {
			return false;
		}
		if (!Arrays.equals(this.outlinePaintSequence,
				that.outlinePaintSequence)) {
			return false;
		}
		if (!Arrays.equals(this.strokeSequence, that.strokeSequence)) {
			return false;
		}
		if (!Arrays.equals(this.outlineStrokeSequence,
				that.outlineStrokeSequence)) {
			return false;
		}
		return !equalShapes(this.shapeSequence, that.shapeSequence);
	}

	/**
	 * A utility method for testing the equality of two arrays of shapes.
	 *
	 * @param s1 the first array (<code>null</code> permitted).
	 * @param s2 the second array (<code>null</code> permitted).
	 * @return A boolean.
	 */
	private boolean equalShapes(Shape[] s1, Shape[] s2) {
		if (s1 == null) {
			return s2 == null;
		}
		if (s2 == null) {
			return false;
		}
		if (s1.length != s2.length) {
			return false;
		}
		for (int i = 0; i < s1.length; i++) {
			if (!ShapeUtilities.equal(s1[i], s2[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Handles serialization.
	 *
	 * @param stream the output stream.
	 * @throws IOException if there is an I/O problem.
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.defaultWriteObject();

		int paintCount = this.paintSequence.length;
		stream.writeInt(paintCount);
		for (Paint aPaintSequence : this.paintSequence) {
			SerialUtilities.writePaint(aPaintSequence, stream);
		}

		int outlinePaintCount = this.outlinePaintSequence.length;
		stream.writeInt(outlinePaintCount);
		for (Paint anOutlinePaintSequence : this.outlinePaintSequence) {
			SerialUtilities.writePaint(anOutlinePaintSequence, stream);
		}

		int strokeCount = this.strokeSequence.length;
		stream.writeInt(strokeCount);
		for (Stroke aStrokeSequence : this.strokeSequence) {
			SerialUtilities.writeStroke(aStrokeSequence, stream);
		}

		int outlineStrokeCount = this.outlineStrokeSequence.length;
		stream.writeInt(outlineStrokeCount);
		for (Stroke anOutlineStrokeSequence : this.outlineStrokeSequence) {
			SerialUtilities.writeStroke(anOutlineStrokeSequence, stream);
		}

		int shapeCount = this.shapeSequence.length;
		stream.writeInt(shapeCount);
		for (Shape aShapeSequence : this.shapeSequence) {
			SerialUtilities.writeShape(aShapeSequence, stream);
		}

	}

	/**
	 * Restores a serialized object.
	 *
	 * @param stream the input stream.
	 * @throws IOException            if there is an I/O problem.
	 * @throws ClassNotFoundException if there is a problem loading a class.
	 */
	private void readObject(ObjectInputStream stream)
			throws IOException, ClassNotFoundException {
		stream.defaultReadObject();

		int paintCount = stream.readInt();
		this.paintSequence = new Paint[paintCount];
		for (int i = 0; i < paintCount; i++) {
			this.paintSequence[i] = SerialUtilities.readPaint(stream);
		}

		int outlinePaintCount = stream.readInt();
		this.outlinePaintSequence = new Paint[outlinePaintCount];
		for (int i = 0; i < outlinePaintCount; i++) {
			this.outlinePaintSequence[i] = SerialUtilities.readPaint(stream);
		}

		int strokeCount = stream.readInt();
		this.strokeSequence = new Stroke[strokeCount];
		for (int i = 0; i < strokeCount; i++) {
			this.strokeSequence[i] = SerialUtilities.readStroke(stream);
		}

		int outlineStrokeCount = stream.readInt();
		this.outlineStrokeSequence = new Stroke[outlineStrokeCount];
		for (int i = 0; i < outlineStrokeCount; i++) {
			this.outlineStrokeSequence[i] = SerialUtilities.readStroke(stream);
		}

		int shapeCount = stream.readInt();
		this.shapeSequence = new Shape[shapeCount];
		for (int i = 0; i < shapeCount; i++) {
			this.shapeSequence[i] = SerialUtilities.readShape(stream);
		}

	}

	/**
	 * Returns a clone.
	 *
	 * @return A clone.
	 * @throws CloneNotSupportedException if a component of the supplier does
	 *                                    not support cloning.
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
