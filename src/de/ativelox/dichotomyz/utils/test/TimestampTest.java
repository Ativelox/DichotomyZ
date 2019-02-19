/**
 * 
 */
package de.ativelox.dichotomyz.utils.test;

import org.junit.Assert;
import org.junit.Test;

import de.ativelox.dichotomyz.utils.Timestamp;

/**
 * Provides Tests for {@link Timestamp}.
 * 
 * @author Ativelox {@literal <ativelox.dev@web.de>}
 *
 */
public class TimestampTest {

    /**
     * Test method for
     * {@link de.ativelox.dichotomyz.utils.Timestamp#msToReadable(long)}.
     */
    @Test
    public void testMsToReadable() {
	final int testMs = 5323000;
	Assert.assertEquals("01:28:43", Timestamp.msToReadable(testMs));
    }

}
