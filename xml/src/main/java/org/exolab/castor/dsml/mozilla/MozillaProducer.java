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
 * Copyright 1999 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id: MozillaProducer.java 8145 2009-03-29 22:07:00Z rjoachim $
 */


package org.exolab.castor.dsml.mozilla;


import java.util.Enumeration;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributeListImpl;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPAttribute;
import netscape.ldap.LDAPAttributeSet;
import netscape.ldap.LDAPSchema;
import netscape.ldap.LDAPAttributeSchema;
import netscape.ldap.LDAPObjectClassSchema;
import netscape.ldap.LDAPSearchResults;

import org.castor.core.util.Base64Encoder;
import org.exolab.castor.dsml.Producer;
import org.exolab.castor.dsml.XML;


/**
 *
 *
 * @author <a href="mailto:arkin@intalio.com">Assaf Arkin</a>
 * @version $Revision: 8145 $ $Date: 2005-08-05 13:58:36 -0600 (Fri, 05 Aug 2005) $
 */
public class MozillaProducer
    extends Producer
{


    public MozillaProducer( DocumentHandler docHandler, boolean namespace  )
    {
	super( docHandler, namespace );
    }


    public void produce( LDAPEntry entry )
	throws SAXException
    {
	AttributeListImpl attrList;
	LDAPAttributeSet  attrSet;
	LDAPAttribute     attr;
	Enumeration       enumeration;
	Enumeration       values;
	byte[]            value;

	leaveSchema();
	enterDirectory();

	// dsml:entry dn
	attrList = new AttributeListImpl();
	attrList.addAttribute( XML.Entries.Attributes.DN, "CDATA", entry.getDN() );
	// dsml:entry
	_docHandler.startElement( prefix( XML.Entries.Elements.ENTRY ), attrList );
	
	attrSet = entry.getAttributeSet();
	if ( attrSet != null ) {
	    
	    attr = attrSet.getAttribute( "objectclass" );
	    if ( attr != null ) {
		// dsml:objectclass
		attrList = new AttributeListImpl();
		_docHandler.startElement( prefix( XML.Entries.Elements.OBJECT_CLASS ), attrList );
		values = attr.getStringValues();
		while ( values.hasMoreElements() ) {
		    char[] chars;
		    
		    // dsml:oc-value
		    chars = ( (String) values.nextElement() ).toCharArray();
		    attrList = new AttributeListImpl();
		    _docHandler.startElement( prefix( XML.Entries.Elements.OBJECT_CLASS_VALUE ), attrList );
		    _docHandler.characters( chars, 0, chars.length );
		    _docHandler.endElement( prefix( XML.Entries.Elements.OBJECT_CLASS_VALUE ) );
		}
		_docHandler.endElement( prefix( XML.Entries.Elements.OBJECT_CLASS ) );
	    }
	    
	    enumeration = attrSet.getAttributes();
	    while ( enumeration.hasMoreElements() ) {
		// dsml:attr
		attr = (LDAPAttribute) enumeration.nextElement();
		if ( attr.getName().equals( "objectclass" ) )
		    continue;
		attrList = new AttributeListImpl();
		attrList.addAttribute( XML.Entries.Attributes.NAME, "CDATA", attr.getName() );
		_docHandler.startElement( prefix( XML.Entries.Elements.ATTRIBUTE ), attrList );
		
		values = attr.getByteValues();
		while ( values.hasMoreElements() ) {
		    char[] chars;
		    int    i;

		    // dsml:value
		    value = (byte[]) values.nextElement();
		    attrList = new AttributeListImpl();
		    if ( value == null ) {
			chars = new char[ 0 ];
		    } else {
			// XXX We have no way of knowing if the attribute is
			//     string or binary, so we do this stupid check
			//     to determine and print it as ASCII text or
			//     base 64 encoding.
			//     (note: OpenLDAP does not provide the attributes
			//     schema as one would hope)
			for ( i = 0 ; i < value.length ; ++i ) {
			    if ( value[ i ] < 0x20 || value[ i ] == 0x7f )
				break;
			}
			if ( i == value.length ) {
			    chars = new char[ value.length ];
			    for ( i = 0 ; i < value.length ; ++i )
				chars[ i ] = (char) value[ i ];
			} else {
                chars = Base64Encoder.encode(value);
			    attrList.addAttribute( XML.Entries.Attributes.ENCODING, "NMTOKEN",
						   XML.Entries.Attributes.Encodings.BASE64 );
			}
		    }

		    _docHandler.startElement( prefix( XML.Entries.Elements.VALUE ), attrList );
		    _docHandler.characters( chars, 0, chars.length );
		    _docHandler.endElement( prefix( XML.Entries.Elements.VALUE ) );
		}
		_docHandler.endElement( prefix( XML.Entries.Elements.ATTRIBUTE ) );
	    }
	}
	_docHandler.endElement( prefix( XML.Entries.Elements.ENTRY ) );
    }
    

    public void produce( Enumeration entries )
	throws SAXException
    {
	while ( entries.hasMoreElements() ) {
	    produce( (LDAPEntry) entries.nextElement() );
	}
    }


    public void produce( LDAPSearchResults entries )
	throws SAXException
    {
	while ( entries.hasMoreElements() ) {
	    produce( (LDAPEntry) entries.nextElement() );
	}
    }


    public void produce( LDAPSchema schema )
	throws SAXException
    {
	Enumeration       enumeration;

	enumeration = schema.getObjectClasses();
	while ( enumeration.hasMoreElements() ) {
	    produce( (LDAPObjectClassSchema) enumeration.nextElement() );
	}
	enumeration = schema.getAttributes();
	while ( enumeration.hasMoreElements() ) {
	    produce( (LDAPAttributeSchema) enumeration.nextElement() );
	}
    }


    public void produce( LDAPObjectClassSchema schema )
	throws SAXException
    {
	AttributeListImpl attrList;
	String            superiors[];
	String            superior;
	int               i;
	Enumeration       enumeration;

	leaveDirectory();
	enterSchema();
	
	attrList = new AttributeListImpl();
	// dsml:class id
	attrList.addAttribute( XML.Schema.Attributes.ID, "ID", schema.getName() );
	// dsml:class superior
	superiors = schema.getSuperiors();
	superior = null;
	for ( i = 0 ; i < superiors.length ; ++i ) {
	    if ( i == 0 )
		superior = superiors[ i ];
	    else
		superior = superior + "," + superiors[ i ];
	}
	if ( i > 0 )
	    attrList.addAttribute( XML.Schema.Attributes.SUPERIOR, "CDATA", superior );
	// dsml:class obsolete
	attrList.addAttribute( XML.Schema.Attributes.OBSOLETE, null,
			       schema.isObsolete() ? "true" : "false" );
	// dsml:class type
	switch ( schema.getType() ) {
	case LDAPObjectClassSchema.STRUCTURAL:
	    attrList.addAttribute( XML.Schema.Attributes.TYPE, null,
				   XML.Schema.Attributes.Types.STRUCTURAL );
	    break;
	case LDAPObjectClassSchema.ABSTRACT:
	    attrList.addAttribute( XML.Schema.Attributes.TYPE, null,
				   XML.Schema.Attributes.Types.ABSTRACT );
	    break;
	case LDAPObjectClassSchema.AUXILIARY:
	    attrList.addAttribute( XML.Schema.Attributes.TYPE, null,
				   XML.Schema.Attributes.Types.AUXILIARY );
	    break;
	}

	// dsml:class
	_docHandler.startElement( prefix( XML.Schema.Elements.CLASS ), attrList );

	// dsml:class name
	if ( schema.getName() != null ) {
	    attrList = new AttributeListImpl();
	    _docHandler.startElement( prefix( XML.Schema.Elements.NAME ), attrList );
	    _docHandler.characters( schema.getName().toCharArray(), 0,
				    schema.getName().length() );
	    _docHandler.endElement( prefix( XML.Schema.Elements.NAME ) );
	}
	// dsml:class description
	if ( schema.getDescription() != null ) {
	    attrList = new AttributeListImpl();
	    _docHandler.startElement( prefix( XML.Schema.Elements.DESCRIPTION ), attrList );
	    _docHandler.characters( schema.getDescription().toCharArray(), 0,
				    schema.getDescription().length() );
	    _docHandler.endElement( prefix( XML.Schema.Elements.DESCRIPTION ) );
	}
	// dsml:class object-identifier
	if ( schema.getID() != null ) {
	    attrList = new AttributeListImpl();
	    _docHandler.startElement( prefix( XML.Schema.Elements.OID ), attrList );
	    _docHandler.characters( schema.getID().toCharArray(), 0,
				    schema.getID().length() );
	    _docHandler.endElement( prefix( XML.Schema.Elements.OID ) );
	}

	// dsml:class attribute required=false
	enumeration = schema.getOptionalAttributes();
	while ( enumeration.hasMoreElements() ) {
	    attrList = new AttributeListImpl();
	    attrList.addAttribute( XML.Schema.Attributes.REF, "CDATA",
				   "#" + (String) enumeration.nextElement() );
	    attrList.addAttribute( XML.Schema.Attributes.REQUIRED, null, "false" );
	    _docHandler.startElement( prefix( XML.Schema.Elements.ATTRIBUTE) , attrList );
	    _docHandler.endElement( prefix( XML.Schema.Elements.ATTRIBUTE ) );
	}
	// dsml:class attribute required=true
	enumeration = schema.getRequiredAttributes();
	while ( enumeration.hasMoreElements() ) {
	    attrList = new AttributeListImpl();
	    attrList.addAttribute( XML.Schema.Attributes.REF, "CDATA",
				   "#" + (String) enumeration.nextElement() );
	    attrList.addAttribute( XML.Schema.Attributes.REQUIRED, null, "true" );
	    _docHandler.startElement( prefix( XML.Schema.Elements.ATTRIBUTE) , attrList );
	    _docHandler.endElement( prefix( XML.Schema.Elements.ATTRIBUTE ) );
	}

	_docHandler.endElement( prefix( XML.Schema.Elements.CLASS ) );
    }


    public void produce( LDAPAttributeSchema schema )
	throws SAXException
    {
	AttributeListImpl attrList;

	leaveDirectory();
	enterSchema();
 
	attrList = new AttributeListImpl();
	// dsml:attribute id
	attrList.addAttribute( XML.Schema.Attributes.ID, "ID", schema.getName() );
	// dsml:attribute superior
	if ( schema.getSuperior() != null ) {
	    attrList.addAttribute( XML.Schema.Attributes.SUPERIOR, "CDATA", "#" + schema.getSuperior() );
	}
	// dsml:attribute obsolete
	attrList.addAttribute( XML.Schema.Attributes.OBSOLETE, null,
			       schema.isObsolete() ? "true" : "false" );
	// dsml:attribute single-value
	attrList.addAttribute( XML.Schema.Attributes.SINGLE_VALUE, null,
			       schema.isSingleValued() ? "true" : "false" );
	// dsml:attribute user-modification
	// XXX

	// dsml:attribute
	_docHandler.startElement( prefix( XML.Schema.Elements.ATTRIBUTE_TYPE) , attrList );

	// dsml:attribute name
	if ( schema.getName() != null ) {
	    attrList = new AttributeListImpl();
	    _docHandler.startElement( prefix( XML.Schema.Elements.NAME ), attrList );
	    _docHandler.characters( schema.getName().toCharArray(), 0,
				    schema.getName().length() );
	    _docHandler.endElement( prefix( XML.Schema.Elements.NAME ) );
	}
	// dsml:attribute description
	if ( schema.getDescription() != null ) {
	    attrList = new AttributeListImpl();
	    _docHandler.startElement( prefix( XML.Schema.Elements.DESCRIPTION ), attrList );
	    _docHandler.characters( schema.getDescription().toCharArray(), 0,
				    schema.getDescription().length() );
	    _docHandler.endElement( prefix( XML.Schema.Elements.DESCRIPTION ) );
	}
	// dsml:attribute object-identifier
	if ( schema.getID() != null ) {
	    attrList = new AttributeListImpl();
	    _docHandler.startElement( prefix( XML.Schema.Elements.OID ), attrList );
	    _docHandler.characters( schema.getID().toCharArray(), 0,
				    schema.getID().length() );
	    _docHandler.endElement( prefix( XML.Schema.Elements.OID ) );
	}
	// dsml:attribute syntax
	if ( schema.getSyntaxString() != null ) {
	    attrList = new AttributeListImpl();
	    _docHandler.startElement( prefix( XML.Schema.Elements.SYNTAX ), attrList );
	    _docHandler.characters( schema.getSyntaxString().toCharArray(), 0,
				    schema.getSyntaxString().length() );
	    _docHandler.endElement( prefix( XML.Schema.Elements.SYNTAX ) );
	}

	// dsml:attribute equality
	// XXX
	// dsml:attribute ordering
	// XXX
	// dsml:attribute substring
	// XXX

	_docHandler.endElement( prefix( XML.Schema.Elements.ATTRIBUTE_TYPE ) );
    }


}
