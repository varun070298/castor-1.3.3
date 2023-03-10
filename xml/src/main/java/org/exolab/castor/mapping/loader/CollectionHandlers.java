/**
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright
 *    statements and notices.  Redistributions must also contain a
 *    copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the
 *    above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. The name "Exolab" must not be used to endorse or promote
 *    products derived from this Software without prior written
 *    permission of Intalio, Inc.  For written permission,
 *    please contact info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Intalio, Inc. Exolab is a registered
 *    trademark of Intalio, Inc.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY INTALIO, INC. AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * INTALIO, INC. OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 1999-2003 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id: CollectionHandlers.java 9061 2011-09-23 19:39:09Z wguttmn $
 */

package org.exolab.castor.mapping.loader;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.castor.core.util.AbstractProperties;
import org.castor.xml.XMLProperties;
import org.exolab.castor.mapping.CollectionHandler;
import org.exolab.castor.mapping.MappingException;

/**
 * Utility class for obtaining collection handlers. Based on the configuration
 * and supported classes it will return collections suitable for Java 1.1 and
 * Java 1.2 run times.
 * 
 * @author <a href="arkin@intalio.com">Assaf Arkin</a>
 * @version $Revision: 9061 $ $Date: 2005-05-02 14:58:59 -0600 (Mon, 02 May
 *          2005) $
 * @see CollectionHandler
 */
public final class CollectionHandlers {

   private static Info[] _info;

   // For JDK 1.1 compatilibity
   private static Class _collectionClass = null;
   private static boolean _loadedCollectionClass = false;

   static {
      Vector<Info> allInfo = new Vector<Info>();
      AbstractProperties properties = XMLProperties.newInstance();
      StringTokenizer tokenizer = new StringTokenizer(properties.getString(
            XMLProperties.COLLECTION_HANDLERS_FOR_JAVA_11_OR_12, ""), ", ");
      while (tokenizer.hasMoreTokens()) {
         try {
            Class<?> infoClass;
            if (CollectionHandlers.class.getClassLoader() != null)
               infoClass = CollectionHandlers.class.getClassLoader().loadClass(tokenizer.nextToken());
            else
               infoClass = Class.forName(tokenizer.nextToken());
            Method method = infoClass.getMethod("getCollectionHandlersInfo", (Class[]) null);
            Info[] info = (Info[]) method.invoke(null, (Object[]) null);
            for (int i = 0; i < info.length; ++i)
               allInfo.addElement(info[i]);
         } catch (Exception except) {
            // System.err.println( "CollectionHandlers: " + except.toString()
            // );
         }
      }
      _info = new Info[allInfo.size()];
      allInfo.copyInto(_info);
   }

   /**
    * Returns the collection's Java class from the collection name. The
    * collection name may be a short name (e.g. <tt>vector</tt>) or the
    * collection Java class name (e.g. <tt>java.util.Vector</tt>). If the
    * collection is not supported, an exception is thrown.
    * 
    * @param name
    *           The collection name
    * @return The collection Java class
    * @throws MappingException
    *            The named collection is not supported
    */
   public static Class<?> getCollectionType(String name) throws MappingException {
      for (Info info : _info) {
         if (info.getShortName().equalsIgnoreCase(name) || info.getJavaClass().getName().equals(name)) {
            return info.getJavaClass();
         }
      }
         
//      for (int i = 0; i < _info.length; ++i) {
//         if (_info[i].getShortName().equalsIgnoreCase(name) || _info[i].getJavaClass().getName().equals(name)) {
//            return _info[i].getJavaClass();
//            // throw new MappingException( "mapping.noCollectionHandler", name);
//         }
//      }

      // -- Fix for JDK 1.1 compatibility
      // old code: return Collection.class;
      if (!_loadedCollectionClass) {
         _loadedCollectionClass = true;
         try {
            _collectionClass = Class.forName("java.util.Collection");
         } catch (ClassNotFoundException cnfe) {
            // Do nothing we are just here for JDK 1.1
            // compatibility
         }
      }
      return _collectionClass;
   }

   /**
    * Returns true if the given class has an associated CollectionHandler.
    * 
    * @param javaClass
    *           the class to search collection handlers for
    * @return true if the given class has an associated CollectionHandler,
    *         otherwise false.
    */
   public static boolean hasHandler(Class<?> javaClass) {
      // -- Adjust javaClass for arrays, needed for arrays of
      // -- primitives, except for byte[] which shouldn't
      // -- use a collection handler
      if (javaClass.isArray()) {
         if (javaClass.getComponentType() != Byte.TYPE)
            javaClass = Object[].class;
      }

      for (Info info : _info) {
         if (info.getJavaClass().isAssignableFrom(javaClass)) {
            return true;
         }
      }
//      for (int i = 0; i < _info.length; ++i)
//         if (_info[i].getJavaClass().isAssignableFrom(javaClass))
//            return true;

      return false;

   }

   /**
    * Returns the associated string name for a given collection.
    * 
    * @param javaClass
    *           the class to search collection handlers for
    * @return the string name for the given collection type or null if no
    *         association has been defined.
    */
   public static String getCollectionName(Class<?> javaClass) {
      // -- Adjust javaClass for arrays, needed for arrays of
      // -- primitives, except for byte[] which shouldn't
      // -- use a collection handler
      if (javaClass.isArray()) {
         if (javaClass.getComponentType() != Byte.TYPE)
            javaClass = Object[].class;
      }

      // -- First check direct class equality, to provide a better match
      // -- (for example in JDK 1.2 a Vector is also a Collection)
      for (int i = 0; i < _info.length; ++i)
         if (_info[i].getJavaClass().equals(javaClass))
            return _info[i].getShortName();

      // -- handle Possible inheritance
      for (int i = 0; i < _info.length; ++i)
         if (_info[i].getJavaClass().isAssignableFrom(javaClass))
            return _info[i].getShortName();

      return null;

   } // -- hasHandler

   /**
    * Returns the collection's handler based on the Java class.
    * 
    * @param javaClass
    *           The collection's Java class
    * @return The collection handler
    * @throws MappingException
    *            The collection class is not supported
    */
   public static CollectionHandler getHandler(Class<?> javaClass) throws MappingException {
      // -- Adjust javaClass for arrays, needed for arrays of
      // -- primitives, except for byte[] which shouldn't
      // -- use a collection handler
      if (javaClass.isArray()) {
         if (javaClass.getComponentType() != Byte.TYPE)
            javaClass = Object[].class;
      }

      // -- First check direct class equality, to provide a better match
      // -- (for example in JDK 1.2 a Vector is also a Collection)
      for (int i = 0; i < _info.length; ++i)
         if (_info[i].getJavaClass().equals(javaClass))
            return _info[i].handler;

      // -- handle Possible inheritence
      for (int i = 0; i < _info.length; ++i)
         if (_info[i].getJavaClass().isAssignableFrom(javaClass))
            return _info[i].handler;

      throw new MappingException("mapping.noCollectionHandler", javaClass.getName());
   }

   /**
    * Returns true if the collection requires get/set methods.
    * <tt>java.util</tt> collections only require a get method, but an array
    * collection required both get and set methods.
    * 
    * @param javaClass
    *           The collection's java class
    * @return True if collection requires get/set methods, false if collection
    *         requires only get method
    * @throws MappingException
    *            The collection class is not supported
    */
   public static boolean isGetSetCollection(Class<?> javaClass) throws MappingException {
      for (int i = 0; i < _info.length; ++i)
         if (_info[i].getJavaClass().equals(javaClass))
            return _info[i].getSetCollection;
      throw new MappingException("mapping.noCollectionHandler", javaClass.getName());
   }

   static class Info {

      /**
       * The short name of the collection (e.g. <tt>vector</tt>).
       */
      private final String shortName;

      /**
       * The Java class of the collection (e.g. <tt>java.util.Vector</tt>).
       */
      private final Class<?> javaClass;

      /**
       * The collection handler instance.
       */
      private final CollectionHandler handler;

      /**
       * True for collections that require both get and set methods.
       */
      final boolean getSetCollection;

      Info(String shortName, Class<?> javaClass, boolean getSetCollection, CollectionHandler handler) {
         this.shortName = shortName;
         this.javaClass = javaClass;
         this.handler = handler;
         this.getSetCollection = getSetCollection;
      }

      String getShortName() {
         return shortName;
      }

      Class<?> getJavaClass() {
         return javaClass;
      }

      CollectionHandler getHandler() {
         return handler;
      }

   }

   /**
    * Enumerator for a null collection.
    */
   public static final class EmptyEnumerator<T> implements Enumeration<T>, Serializable {

      public boolean hasMoreElements() {
         return false;
      }

      public T nextElement() {
         throw new NoSuchElementException();
      }
   }

}
