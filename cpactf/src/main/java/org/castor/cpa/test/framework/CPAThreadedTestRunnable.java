/*
 * Copyright 2010 Ralf Joachim, Clovis Wichoski
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
package org.castor.cpa.test.framework;

/**
 * Abstract base class for Runnables of threaded test cases.
 * <br/>
 * Override runTest() to define behaviour of test.
 * <br/>
 * Based on http://www.javaworld.com/jw-12-2000/jw-1221-junit.html?page=6
 * 
 * @author <a href="mailto:clovis AT supridatta DOT com DOT br">Clovis Wichoski</a>
 * @author <a href="mailto:ralf DOT joachim AT syscon DOT eu">Ralf Joachim</a>
 * @version $Revision: 8992 $ $Date: 2011-08-02 01:22:26 +0200 (Di, 02 Aug 2011) $
 */
public abstract class CPAThreadedTestRunnable implements Runnable {
    //--------------------------------------------------------------------------
    
    /** Parent test case this runnable belongs to. */
    private CPAThreadedTestCase _parent;
    
    //--------------------------------------------------------------------------

    /**
     * Set parent test case the runnable belongs to.
     * 
     * @param parent Parent test case the runnable belongs to.
     */
    protected final void setParentTestCase(final CPAThreadedTestCase parent) {
        _parent = parent;
    }

    /**
     * Run the test in an environment where we can handle the exceptions generated by the
     * test method.
     */
    public final void run() {
       try {
           runTest();
       } catch (Throwable t) {
           // Catch all exceptions and add them to test result.
           _parent.handleException(t);
           
           // Then we interrupt the other threads.
           _parent.interruptThreads();
       }
    }

    /**
     * Override to define what needs to be done during test.
     * 
     * @throws Throwable Catch anything.
     */
    public abstract void runTest() throws Throwable;
    
    //--------------------------------------------------------------------------
}
