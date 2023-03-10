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
package org.castor.cpaptf.rel1toN;

import java.text.DecimalFormat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.JDOManager;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;

/**
 * @author <a href="mailto:ralf DOT joachim AT syscon DOT eu">Ralf Joachim</a>
 * @version $Revision:6817 $ $Date: 2011-08-02 01:05:37 +0200 (Di, 02 Aug 2011) $
 */
public final class TestLoadUniNto1 extends TestCase {
    private static final String JDO_CONF_FILE = "uni-jdo-conf.xml";
    private static final String DATABASE_NAME = "rel1toN_uni";
    
    private static final DecimalFormat DF = new DecimalFormat("#,##0");
    
    private static final Log LOG = LogFactory.getLog(TestLoadUniNto1.class);
    private static boolean _logHeader = false;
    
    private JDOManager _jdo = null;
    
    public static Test suite() throws Exception {
        String config = TestLoadUniNto1.class.getResource(JDO_CONF_FILE).toString();
        JDOManager.loadConfiguration(config, TestLoadUniNto1.class.getClassLoader());
        
        TestSuite suite = new TestSuite("Test load n:1 with unidirectional mapping");

        suite.addTest(new TestLoadUniNto1("testReadWriteEmpty"));
        suite.addTest(new TestLoadUniNto1("testReadWriteCached"));
        suite.addTest(new TestLoadUniNto1("testReadWriteOidEmpty"));
        suite.addTest(new TestLoadUniNto1("testReadWriteOidCached"));

        suite.addTest(new TestLoadUniNto1("testReadOnlyEmpty"));
        suite.addTest(new TestLoadUniNto1("testReadOnlyCached"));
        suite.addTest(new TestLoadUniNto1("testReadOnlyOidEmpty"));
        suite.addTest(new TestLoadUniNto1("testReadOnlyOidCached"));

        suite.addTest(new TestLoadUniNto1("testReadOnlyOidOnly"));

        return suite;
    }

    public TestLoadUniNto1() {
        super();
    }
    
    public TestLoadUniNto1(final String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();

        if (!_logHeader) {
            LOG.info("");
            LOG.info("");
            LOG.info("TestLoadUniNto1 (" + (int) (10000 * TestCreate.FACTOR) + ")");
            LOG.info("");
            LOG.info(format("", "begin", "result", "iterate", "commit", "close"));
            _logHeader = true;
        }

        _jdo = JDOManager.createInstance(DATABASE_NAME);
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testReadWriteEmpty() throws Exception {
        long start = System.currentTimeMillis();
        
        Database db =  _jdo.getDatabase();
        db.getCacheManager().expireCache();
        db.begin();
        
        long begin = System.currentTimeMillis();
        
        OQLQuery query = db.getOQLQuery(
                "SELECT o FROM " + Service.class.getName() + " o order by o.id");
        QueryResults results = query.execute();
        
        long result = System.currentTimeMillis();
        
        int count = 0;
        while (results.hasMore()) {
            results.next();
            count++;
        }
        
        long iterate = System.currentTimeMillis();
        
        db.commit();
        
        long commit = System.currentTimeMillis();
        
        db.close();

        long close = System.currentTimeMillis();
        
        LOG.info(format("ReadWriteEmpty",
                         DF.format(begin - start),
                         DF.format(result - begin),
                         DF.format(iterate - result),
                         DF.format(commit - iterate),
                         DF.format(close - commit)));
    }
    
    public void testReadWriteCached() throws Exception {
        long start = System.currentTimeMillis();
        
        Database db =  _jdo.getDatabase();
        db.begin();
        
        long begin = System.currentTimeMillis();
        
        OQLQuery query = db.getOQLQuery(
                "SELECT o FROM " + Service.class.getName() + " o order by o.id");
        QueryResults results = query.execute();
        
        long result = System.currentTimeMillis();
        
        int count = 0;
        while (results.hasMore()) {
            results.next();
            count++;
        }
        
        long iterate = System.currentTimeMillis();
        
        db.commit();
        
        long commit = System.currentTimeMillis();
        
        db.close();

        long close = System.currentTimeMillis();
        
        LOG.info(format("ReadWriteCached",
                         DF.format(begin - start),
                         DF.format(result - begin),
                         DF.format(iterate - result),
                         DF.format(commit - iterate),
                         DF.format(close - commit)));
    }
    
    public void testReadOnlyEmpty() throws Exception {
        long start = System.currentTimeMillis();
        
        Database db =  _jdo.getDatabase();
        db.getCacheManager().expireCache();
        db.begin();
        
        long begin = System.currentTimeMillis();
        
        OQLQuery query = db.getOQLQuery(
                "SELECT o FROM " + Service.class.getName() + " o order by o.id");
        QueryResults results = query.execute(Database.READONLY);
        
        long result = System.currentTimeMillis();
        
        int count = 0;
        while (results.hasMore()) {
            results.next();
            count++;
        }
        
        long iterate = System.currentTimeMillis();
        
        db.commit();
        
        long commit = System.currentTimeMillis();
        
        db.close();

        long close = System.currentTimeMillis();
        
        LOG.info(format("ReadOnlyEmpty",
                         DF.format(begin - start),
                         DF.format(result - begin),
                         DF.format(iterate - result),
                         DF.format(commit - iterate),
                         DF.format(close - commit)));
    }
    
    public void testReadOnlyCached() throws Exception {
        long start = System.currentTimeMillis();
        
        Database db =  _jdo.getDatabase();
        db.begin();
        
        long begin = System.currentTimeMillis();
        
        OQLQuery query = db.getOQLQuery(
                "SELECT o FROM " + Service.class.getName() + " o order by o.id");
        QueryResults results = query.execute(Database.READONLY);
        
        long result = System.currentTimeMillis();
        
        int count = 0;
        while (results.hasMore()) {
            results.next();
            count++;
        }
        
        long iterate = System.currentTimeMillis();
        
        db.commit();
        
        long commit = System.currentTimeMillis();
        
        db.close();

        long close = System.currentTimeMillis();
        
        LOG.info(format("ReadOnlyCached",
                         DF.format(begin - start),
                         DF.format(result - begin),
                         DF.format(iterate - result),
                         DF.format(commit - iterate),
                         DF.format(close - commit)));
    }

    public void testReadWriteOidEmpty() throws Exception {
        long start = System.currentTimeMillis();
        
        Database db =  _jdo.getDatabase();
        db.getCacheManager().expireCache();
        db.begin();
        
        long begin = System.currentTimeMillis();
        
        OQLQuery query = db.getOQLQuery(
                "CALL SQL select PTF_SERVICE.ID as ID "
              + "from PTF_SERVICE order by PTF_SERVICE.ID "
              + "AS " + OID.class.getName());
        QueryResults results = query.execute(Database.READONLY);
        
        long result = System.currentTimeMillis();
        
        int count = 0;
        while (results.hasMore()) {
            OID oid = (OID) results.next();
            db.load(Service.class, oid.getId());
            count++;
        }
        
        long iterate = System.currentTimeMillis();
        
        db.commit();
        
        long commit = System.currentTimeMillis();
        
        db.close();

        long close = System.currentTimeMillis();
        
        LOG.info(format("ReadWriteOidEmpty",
                         DF.format(begin - start),
                         DF.format(result - begin),
                         DF.format(iterate - result),
                         DF.format(commit - iterate),
                         DF.format(close - commit)));
    }
    
    public void testReadWriteOidCached() throws Exception {
        long start = System.currentTimeMillis();
        
        Database db =  _jdo.getDatabase();
        db.begin();
        
        long begin = System.currentTimeMillis();
        
        OQLQuery query = db.getOQLQuery(
                "CALL SQL select PTF_SERVICE.ID as ID "
              + "from PTF_SERVICE order by PTF_SERVICE.ID "
              + "AS " + OID.class.getName());
        QueryResults results = query.execute(Database.READONLY);
        
        long result = System.currentTimeMillis();
        
        int count = 0;
        while (results.hasMore()) {
            OID oid = (OID) results.next();
            db.load(Service.class, oid.getId());
            count++;
        }
        
        long iterate = System.currentTimeMillis();
        
        db.commit();
        
        long commit = System.currentTimeMillis();
        
        db.close();

        long close = System.currentTimeMillis();
        
        LOG.info(format("ReadWriteOidCached",
                         DF.format(begin - start),
                         DF.format(result - begin),
                         DF.format(iterate - result),
                         DF.format(commit - iterate),
                         DF.format(close - commit)));
    }
    
    public void testReadOnlyOidEmpty() throws Exception {
        long start = System.currentTimeMillis();
        
        Database db =  _jdo.getDatabase();
        db.getCacheManager().expireCache();
        db.begin();
        
        long begin = System.currentTimeMillis();
        
        OQLQuery query = db.getOQLQuery(
                "CALL SQL select PTF_SERVICE.ID as ID "
              + "from PTF_SERVICE order by PTF_SERVICE.ID "
              + "AS " + OID.class.getName());
        QueryResults results = query.execute(Database.READONLY);
        
        long result = System.currentTimeMillis();
        
        int count = 0;
        while (results.hasMore()) {
            OID oid = (OID) results.next();
            db.load(Service.class, oid.getId(), Database.READONLY);
            count++;
        }
        
        long iterate = System.currentTimeMillis();
        
        db.commit();
        
        long commit = System.currentTimeMillis();
        
        db.close();

        long close = System.currentTimeMillis();
        
        LOG.info(format("ReadOnlyOidEmpty",
                         DF.format(begin - start),
                         DF.format(result - begin),
                         DF.format(iterate - result),
                         DF.format(commit - iterate),
                         DF.format(close - commit)));
    }
    
    public void testReadOnlyOidCached() throws Exception {
        long start = System.currentTimeMillis();
        
        Database db =  _jdo.getDatabase();
        db.begin();
        
        long begin = System.currentTimeMillis();
        
        OQLQuery query = db.getOQLQuery(
                "CALL SQL select PTF_SERVICE.ID as ID "
              + "from PTF_SERVICE order by PTF_SERVICE.ID "
              + "AS " + OID.class.getName());
        QueryResults results = query.execute(Database.READONLY);
        
        long result = System.currentTimeMillis();
        
        int count = 0;
        while (results.hasMore()) {
            OID oid = (OID) results.next();
            db.load(Service.class, oid.getId(), Database.READONLY);
            count++;
        }
        
        long iterate = System.currentTimeMillis();
        
        db.commit();
        
        long commit = System.currentTimeMillis();
        
        db.close();

        long close = System.currentTimeMillis();
        
        LOG.info(format("ReadOnlyOidCached",
                         DF.format(begin - start),
                         DF.format(result - begin),
                         DF.format(iterate - result),
                         DF.format(commit - iterate),
                         DF.format(close - commit)));
    }
    
    public void testReadOnlyOidOnly() throws Exception {
        long start = System.currentTimeMillis();
        
        Database db =  _jdo.getDatabase();
        db.begin();
        
        long begin = System.currentTimeMillis();
        
        OQLQuery query = db.getOQLQuery(
                "CALL SQL select PTF_SERVICE.ID as ID "
              + "from PTF_SERVICE order by PTF_SERVICE.ID "
              + "AS " + OID.class.getName());
        QueryResults results = query.execute(Database.READONLY);
        
        long result = System.currentTimeMillis();
        
        int count = 0;
        while (results.hasMore()) {
            results.next();
            count++;
        }
        
        long iterate = System.currentTimeMillis();
        
        db.commit();
        
        long commit = System.currentTimeMillis();
        
        db.close();

        long close = System.currentTimeMillis();
        
        LOG.info(format("ReadOnlyOidOnly",
                         DF.format(begin - start),
                         DF.format(result - begin),
                         DF.format(iterate - result),
                         DF.format(commit - iterate),
                         DF.format(close - commit)));
    }
    
    private static String format(final String head, final String begin,
                                 final String result, final String iterate,
                                 final String commit, final String close) {
        
        StringBuffer sb = new StringBuffer();
        sb.append(format(head, 20, true));
        sb.append(format(begin, 10, false));
        sb.append(format(result, 10, false));
        sb.append(format(iterate, 10, false));
        sb.append(format(commit, 10, false));
        sb.append(format(close, 10, false));
        return sb.toString();
    }
    
    private static String format(final String str, final int len, final boolean after) {
        StringBuffer sb = new StringBuffer();
        if (str != null) {
            sb.append(str);
            for (int i = str.length(); i < len; i++) {
                if (after) {
                    sb.append(' ');
                } else {
                    sb.insert(0, ' ');
                }
            }
        } else {
            for (int i = 0; i < len; i++) {
                if (after) {
                    sb.append(' ');
                } else {
                    sb.insert(0, ' ');
                }
            }
        }
        return sb.toString();
    }
}
