/*
 * Copyright 2005 Ralf Joachim
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.castor.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Run all tests of the org.castor.util package.
 * 
 * @author <a href="mailto:ralf DOT joachim AT syscon DOT eu">Ralf Joachim</a>
 * @version $Revision: 6907 $ $Date: 2006-04-29 03:57:35 -0600 (Sat, 29 Apr 2006) $
 */
public final class TestAll extends TestCase {
    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite("All org.castor.util tests");

        suite.addTest(TestIdentitySet.suite());
        suite.addTest(TestIdentityMap.suite());
        suite.addTest(TestBase64Encoder.suite());
        suite.addTest(TestBase64Decoder.suite());

        return suite;
    }

    public TestAll(final String name) { super(name); }
}
