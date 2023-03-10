/*
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
 * Copyright 2001 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id: AnyNode2SAX.java 6671 2007-01-02 05:57:46Z ekuns $
 * Date         Author              Changes
 * 04/06/2001   Arnaud Blandin      Created
 */
package org.exolab.castor.xml.util;

import java.util.HashSet;

import org.exolab.castor.types.AnyNode;
import org.exolab.castor.xml.EventProducer;
import org.exolab.castor.xml.Namespaces;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributeListImpl;

/**
 * A class for converting an AnyNode to SAX events
 * @author <a href="mailto:blandin@intalio.com">Arnaud Blandin</a>
 * @version $Revision: 6671 $ $Date: 2006-04-25 15:08:23 -0600 (Tue, 25 Apr 2006) $
 */
public class AnyNode2SAX implements EventProducer {

    /** The AnyNode we are firing events for. */
    private AnyNode _node;
    /** The Document Handler. */
    private DocumentHandler _handler;
    /** The stack to store the elements. */
    private HashSet _elements;
    /** The namespace context. */
    private Namespaces _context;

    /**
     * No-arg constructor.
     */
    public AnyNode2SAX() {
        this(null, null);
    }

    /**
     * Creates a AnyNode2SAX for the given node.
     * @param node the AnyNode to create AnyNode2SAX for.
     */
    public AnyNode2SAX(final AnyNode node) {
        this(node, null);
    }

    /**
     * Creates a AnyNode2SAX for the given node and the namespace context.
     * @param node the AnyNode to create AnyNode2SAX for.
     * @param context a namespace context
     */
    public AnyNode2SAX(final AnyNode node, final Namespaces context) {
        _elements = new HashSet();
        _node     = node;
        _context  = (context == null) ? new Namespaces() : context;
    }

    /**
     * Set the Document Handler
     * @param handler the document handler to set
     */
    public void setDocumentHandler(final DocumentHandler handler) {
        if (handler == null) {
           throw new IllegalArgumentException("AnyNode2SAX#setDocumentHandler 'null' value for handler");
        }
        _handler = handler;
    }

    public static void fireEvents(final AnyNode node, final DocumentHandler handler) throws SAXException {
        fireEvents(node, handler, null);
    }

    public static void fireEvents(final AnyNode node,
            final DocumentHandler handler, final Namespaces context) throws SAXException {
        AnyNode2SAX eventProducer = new AnyNode2SAX(node, context);
        eventProducer.setDocumentHandler(handler);
        eventProducer.start();
    }

    public void start() throws org.xml.sax.SAXException {
        if (_node == null || _handler == null) {
           return;
        }
        processAnyNode(_node, _handler);
    }

    private void processAnyNode(final AnyNode node, final DocumentHandler handler)
         throws SAXException {

        if (node == null || handler == null) {
            throw new IllegalArgumentException();
        }

        //-- so we don't potentially get into an endlessloop
        if (!_elements.add(node)) {
            return;
        }

        if (node.getNodeType() == AnyNode.ELEMENT) {
            String name = node.getLocalName();

            //-- retrieve the attributes and handle them
            AttributeListImpl atts = new AttributeListImpl();
            AnyNode tempNode = node.getFirstAttribute();
            String xmlName = null;
            String value = null;
            String attUri = null;
            String attPrefix = null;

            while (tempNode != null) {
                xmlName = tempNode.getLocalName();
                //--retrieve a prefix?
                attUri = tempNode.getNamespaceURI();
                if (attUri != null) {
                    attPrefix = _context.getNamespacePrefix(attUri);
                }
                if (attPrefix != null && attPrefix.length() > 0) {
                    xmlName = attPrefix + ':' + xmlName;
                }
                value = tempNode.getStringValue();
                atts.addAttribute(xmlName, "CDATA", value);
                tempNode = tempNode.getNextSibling();
            } //attributes

            //-- namespace management
            _context = _context.createNamespaces();
            String nsPrefix = node.getNamespacePrefix();
            String nsURI = node.getNamespaceURI();

             //-- retrieve the namespaces declaration and handle them
            tempNode = node.getFirstNamespace();
            String prefix = null;
            while (tempNode != null) {
                prefix = tempNode.getNamespacePrefix();
                value = tempNode.getNamespaceURI();
                if (value != null && value.length() > 0) {
                    _context.addNamespace(prefix, value);
                }
                tempNode = tempNode.getNextSibling();
            } //namespaceNode

            String qName = null;
            //maybe the namespace is already bound to a prefix in the
            //namespace context
            if (nsURI != null && nsURI.length() > 0) {
                String tempPrefix = _context.getNamespacePrefix(nsURI);
                if (tempPrefix != null) {
                    nsPrefix = tempPrefix;
                } else {
                    _context.addNamespace(nsPrefix, nsURI);
                }
            }

            if (nsPrefix != null) {
                int len = nsPrefix.length();
                if (len > 0) {
                    StringBuffer sb = new StringBuffer(len+name.length()+1);
                    sb.append(nsPrefix);
                    sb.append(':');
                    sb.append(name);
                    qName = sb.toString();
                } else {
                    qName = name;
                }
            } else {
                qName = name;
            }

            try {
                _context.declareAsAttributes(atts,true);
                handler.startElement(qName, atts);
            } catch (SAXException sx) {
                throw new SAXException(sx);
            }

            //-- handle child&daughter elements
            tempNode = node.getFirstChild();
            while (tempNode != null) {
                processAnyNode(tempNode, handler);
                tempNode = tempNode.getNextSibling();
            }

            //-- finish element
            try {
                handler.endElement(qName);
                _context = _context.getParent();
            } catch(org.xml.sax.SAXException sx) {
                throw new SAXException(sx);
            }
        } else {
            // ELEMENTS
            if (node.getNodeType() == AnyNode.TEXT) {
                String value = node.getStringValue();
                if (value != null && value.length() > 0) {
                    char[] chars = value.toCharArray();
                    try {
                        handler.characters(chars, 0, chars.length);
                    } catch(org.xml.sax.SAXException sx) {
                        throw new SAXException(sx);
                    }
                }
            }
        }
    }

}
