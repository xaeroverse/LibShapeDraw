package libshapedraw.shape;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import libshapedraw.MockMinecraftAccess;
import libshapedraw.SetupTestEnvironment;
import libshapedraw.primitive.Color;
import libshapedraw.primitive.LineStyle;
import libshapedraw.primitive.Vector3;

import org.junit.Test;

public class TestWireframeCuboid extends SetupTestEnvironment.TestCase {
    @Test
    public void testConstructors() {
        new WireframeCuboid(1.0,2.0,3.0, 4.0,5.0,6.0);
        new WireframeCuboid(new Vector3(1.0,2.0,3.0), new Vector3(4.0,5.0,6.0));

        // it is valid to have both corners be the same values
        new WireframeCuboid(0,0,0, 0,0,0);
        // or even the same instance
        Vector3 v = new Vector3(8.67, -5.3, 0.9);
        WireframeCuboid shape = new WireframeCuboid(v, v);
        assertSame(shape.getLowerCorner(), shape.getUpperCorner());
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorInvalidNullLower() {
        new WireframeCuboid(null, new Vector3(1.0, 2.0, 3.0));
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorInvalidNullUpper() {
        new WireframeCuboid(new Vector3(1.0, 2.0, 3.0), null);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorInvalidNullLowerUpper() {
        new WireframeCuboid(null, null);
    }

    @Test
    public void testGetSet() {
        WireframeCuboid shape = new WireframeCuboid(1.0,2.0,3.0, 4.0,5.0,6.0);
        assertEquals(1.0, shape.getLowerCorner().getX(), 0.0);
        assertEquals(1.0, shape.getUpperCorner().getX(), 4.0);
        assertEquals("(1.0,2.0,3.0)", shape.getLowerCorner().toString());
        assertEquals("(4.0,5.0,6.0)", shape.getUpperCorner().toString());
        assertNotSame(shape.getLowerCorner(), shape.getUpperCorner());

        shape.setLowerCorner(shape.getUpperCorner());
        assertSame(shape.getLowerCorner(), shape.getUpperCorner());
        shape.setUpperCorner(new Vector3(-1.0, -2.0, -3.0));
        assertNotSame(shape.getLowerCorner(), shape.getUpperCorner());
        // auto-normalized
        assertEquals("(-1.0,-2.0,-3.0)", shape.getLowerCorner().toString());
        assertEquals("(4.0,5.0,6.0)", shape.getUpperCorner().toString());
    }

    @Test(expected=NullPointerException.class)
    public void testSetInvalidNullLower() {
        new WireframeCuboid(1.0,2.0,3.0, 4.0,5.0,6.0).setLowerCorner(null);
    }

    @Test(expected=NullPointerException.class)
    public void testSetInvalidNullUpper() {
        new WireframeCuboid(1.0,2.0,3.0, 4.0,5.0,6.0).setUpperCorner(null);
    }

    @Test
    public void testNormalize() {
        WireframeCuboid shape;

        // constructor auto-normalizes
        shape = new WireframeCuboid(1.0,-2.0,3.0, -4.0,5.0,-6.0);
        assertEquals("(-4.0,-2.0,-6.0)(1.0,5.0,3.0)", shape.getLowerCorner().toString() + shape.getUpperCorner().toString());

        // setUpperCorner auto-normalizes
        shape = new WireframeCuboid(1.0,2.0,3.0, 4.0,5.0,6.0);
        shape.setUpperCorner(new Vector3(3.0, -1.5, 5.5));
        assertEquals("(1.0,-1.5,3.0)(3.0,2.0,5.5)", shape.getLowerCorner().toString() + shape.getUpperCorner().toString());

        // setLowerCorner auto-normalizes
        shape = new WireframeCuboid(1.0,2.0,3.0, 4.0,5.0,6.0);
        shape.setLowerCorner(new Vector3(3.0, -1.5, 5.5));
        assertEquals("(3.0,-1.5,5.5)(4.0,5.0,6.0)", shape.getLowerCorner().toString() + shape.getUpperCorner().toString());

        // changing vector components individually does NOT auto-normalize
        shape = new WireframeCuboid(1.0,2.0,3.0, 4.0,5.0,6.0);
        shape.getLowerCorner().setX(55.5);
        shape.getUpperCorner().setZ(-99.0);
        assertEquals("(55.5,2.0,3.0)(4.0,5.0,-99.0)", shape.getLowerCorner().toString() + shape.getUpperCorner().toString());
        // you can always manually normalize
        shape.normalize();
        assertEquals("(4.0,2.0,-99.0)(55.5,5.0,3.0)", shape.getLowerCorner().toString() + shape.getUpperCorner().toString());

        // rendering auto-normalizes
        shape = new WireframeCuboid(1.0,2.0,3.0, 4.0,5.0,6.0);
        shape.getLowerCorner().setX(55.5);
        shape.getUpperCorner().setZ(-99.0);
        assertEquals("(55.5,2.0,3.0)(4.0,5.0,-99.0)", shape.getLowerCorner().toString() + shape.getUpperCorner().toString());
        shape.render(new MockMinecraftAccess(), Vector3.ZEROS.copy());
        assertEquals("(4.0,2.0,-99.0)(55.5,5.0,3.0)", shape.getLowerCorner().toString() + shape.getUpperCorner().toString());
    }

    @Test
    public void testLineStyle() {
        WireframeCuboid shape = new WireframeCuboid(Vector3.ZEROS.copy(), Vector3.ZEROS.copy());
        assertNull(shape.getLineStyle());
        assertSame(LineStyle.DEFAULT, shape.getEffectiveLineStyle());

        shape.setLineStyle(Color.BISQUE.copy(), 5.0F, true);
        assertNotNull(shape.getLineStyle());
        assertEquals("(0xffe4c4ff,5.0|0xffe4c43f,5.0)", shape.getLineStyle().toString());
        assertSame(shape.getLineStyle(), shape.getEffectiveLineStyle());
    }

    @Test
    public void testGetOrigin() {
        Vector3 buf = Vector3.ZEROS.copy();
        WireframeCuboid shape = new WireframeCuboid(1.0,2.0,3.0, 4.0,5.0,6.0);
        shape.getOrigin(buf);
        assertEquals("(2.5,3.5,4.5)", buf.toString());
    }

    @Test(expected=NullPointerException.class)
    public void testGetOriginInvalidNull() {
        new WireframeCuboid(1.0,2.0,3.0, 4.0,5.0,6.0).getOrigin(null);
    }

    @Test
    public void testRenderNormal() {
        Vector3 buf = Vector3.ZEROS.copy();
        MockMinecraftAccess mc = new MockMinecraftAccess();
        mc.assertCountsEqual(0, 0);
        Shape shape = new WireframeCuboid(1.0,2.0,3.0, 4.0,5.0,6.0).setLineStyle(Color.WHITE.copy(), 1.0F, false);
        shape.render(mc, buf);
        mc.assertCountsEqual(3, 16);
        shape.render(mc, buf);
        shape.render(mc, buf);
        shape.render(mc, buf);
        mc.assertCountsEqual(12, 64);
    }

    @Test
    public void testRenderVisibleThroughTerrain() {
        Vector3 buf = Vector3.ZEROS.copy();
        MockMinecraftAccess mc = new MockMinecraftAccess();
        mc.assertCountsEqual(0, 0);
        Shape shape = new WireframeCuboid(1.0,2.0,3.0, 4.0,5.0,6.0).setLineStyle(Color.WHITE.copy(), 1.0F, true);
        shape.render(mc, buf);
        mc.assertCountsEqual(6, 32);
        shape.render(mc, buf);
        shape.render(mc, buf);
        shape.render(mc, buf);
        mc.assertCountsEqual(24, 128);
    }
}