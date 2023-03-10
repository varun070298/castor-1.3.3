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
 */
package org.exolab.javasource;

import java.io.Writer;

/**
 * The writer used by the javasource classes.
 *
 * @author <a href="mailto:keith AT kvisco DOT com">Keith Visco</a>
 * @version $Revision: 8011 $ $Date: 2005-03-30 03:29:24 -0700 (Wed, 30 Mar 2005) $
 */
public final class JSourceWriter extends Writer {
    //--------------------------------------------------------------------------

    /** The default character to use for indentation. */
    public static final char DEFAULT_CHAR = ' ';
    
    /** The default indentation size. */
    public static final short DEFAULT_SIZE = 4;

    //--------------------------------------------------------------------------

    /** The line separator to use for the writeln methods. */
    private String _lineSeparator = System.getProperty("line.separator");
    
    /** Flag for indicating whether we need to add the whitespace to beginning
     *  of next write call. */
    private boolean _addIndentation = true;
    
    /** A flag indicating whether this JSourceWriter should perform autoflush at
     *  the end of a new line. */
    private boolean _autoflush = false;
    
    /** The tab (indentation) size. */
    private short _tabSize = DEFAULT_SIZE;
    
    /** The tab representation. */
    private char[] _tab;
    
    /** The character to use for indentation. */
    private char _tabChar = DEFAULT_CHAR;
    
    /** The current tab level. */
    private short _tabLevel = 0;
    
    /** The writer to send all output to. */
    private Writer _out = null;

    //--------------------------------------------------------------------------

    /**
     * Creates a new JSourceWriter.
     *
     * @param out The Writer to write the actual output to.
     */
    public JSourceWriter(final Writer out) {
        this(out, DEFAULT_SIZE, DEFAULT_CHAR, false);
    }

    /**
     * Creates a new JSourceWriter.
     *
     * @param out The Writer to write the actual output to.
     * @param autoflush A boolean indicating whether or not to perform automatic
     *        flush at the end of a line.
     */
    public JSourceWriter(final Writer out, final boolean autoflush) {
        this(out, DEFAULT_SIZE, DEFAULT_CHAR, autoflush);
    }

    /**
     * Creates a new JSourceWriter.
     *
     * @param out The Writer to write the actual output to.
     * @param tabSize The size of each indentation.
     * @param autoflush A boolean indicating whether or not to perform automatic
     *        flush at the end of a line.
     */
    public JSourceWriter(final Writer out, final short tabSize, final boolean autoflush) {
        this(out, tabSize, DEFAULT_CHAR, autoflush);
    }

    /**
     * Creates a new JSourceWriter.
     *
     * @param out The Writer to write the actual output to.
     * @param tabSize The size of each indentation.
     * @param tabChar The character to use for indentation.
     * @param autoflush A boolean indicating whether or not to perform an automatic
     *        flush at the end of each line.
     */
    public JSourceWriter(final Writer out, final short tabSize, final char tabChar,
            final boolean autoflush) {
        _out = out;
        _autoflush = autoflush;
        _tabChar = tabChar;
        _tabSize = tabSize;
        createTab();
    }

    //--------------------------------------------------------------------------

    /**
     * Returns the line separator being used by this JSourceWriter.
     *
     * @return The line separator being used by this JSourceWriter.
     */
    public String getLineSeparator() {
        return _lineSeparator;
    }

    /**
     * Increases the indentation level by 1.
     */
    public void indent() {
        ++_tabLevel;
    }

    /**
     * Checks to see if the cursor is positioned on a new line.
     *
     * @return True if the cursor is at the start of a new line, otherwise false.
     */
    public boolean isNewline() {
        //-- if we need to add indentation, we are on a new line
        return _addIndentation;
    }

    /**
     * Sets the line separator to use at the end of each line. Typically a line
     * separator will be one of the following:
     * <ul>
     *   <li>"\r\n" for MS Windows</li>
     *   <li>"\n" for UNIX</li>
     *   <li>"\r" for Macintosh</li>
     * </ul>
     *
     * @param lineSeparator The String to use as a line separator.
     */
    public void setLineSeparator(final String lineSeparator) {
        _lineSeparator = lineSeparator;
    }

    /**
     * Decreases the indentation level by 1.
     */
    public void unindent() {
        if (_tabLevel > 0) { --_tabLevel; }
    }

    /**
     * Returns the current indentation level.
     *
     * @return The current indentation level.
     */
    protected short getIndentLevel() {
        return _tabLevel;
    }

    /**
     * Returns the current indent size (getIndentLevel()*tabSize).
     *
     * @return The current indent size.
     */
    protected short getIndentSize() {
        return (short) (_tabLevel * _tabSize);
    }

    /**
     * Returns the current character used for indentation.
     * 
     * @return The current character used for indentation.
     */
    protected char getIndentChar() {
        return _tabChar;
    }

    /**
     * Always applies the current indentation.
     */
    protected void writeIndent() {
        try {
            for (int i = 0; i < _tabLevel; i++) { _out.write(_tab); }
        } catch (java.io.IOException ioe) {
            // ignore
        }
    }

    /**
     * If indentation has not already been applied, then applies it.
     */
    private void ensureIndent() {
        if (_addIndentation) {
            writeIndent();
            _addIndentation = false;
        }
    }

    /**
     * Writes the line separator character to the writer.
     */
    private void linefeed() {
        try {
            _out.write(_lineSeparator);
        } catch (java.io.IOException ioe) {
            // ignore
        }
    }

    /**
     * Creates the tab from the tabSize and the tabChar.
     */
    private void createTab() {
        _tab = new char[_tabSize];
        for (int i = 0; i < _tabSize; i++) {
            _tab[i] = _tabChar;
        }
    }

    //--------------------------------------------------------------------------

    public void write(final float f) {
        write(String.valueOf(f));
    }

    public void write(final long l) {
        write(String.valueOf(l));
    }

    public void write(final double d) {
        write(String.valueOf(d));
    }

    public void write(final Object obj) {
        write(obj.toString());
    }

    public void write(final boolean b) {
        write(String.valueOf(b));
    }

    //--------------------------------------------------------------------------

    public void writeln() {
        synchronized (lock) {
            linefeed();
            _addIndentation = true;
        }
    }

    public void writeln(final float f) {
        synchronized (lock) {
            ensureIndent();
            try {
                _out.write(String.valueOf(f));
            } catch (java.io.IOException ioe) {
                // ignore
            }
            linefeed();
            _addIndentation = true;
        }
    }

    public void writeln(final long l) {
        synchronized (lock) {
            ensureIndent();
            try {
                _out.write(String.valueOf(l));
            } catch (java.io.IOException ioe) {
                // ignore
            }
            linefeed();
            _addIndentation = true;
        }
    }

    public void writeln(final int i) {
        synchronized (lock) {
            ensureIndent();
            try {
                _out.write(String.valueOf(i));
            } catch (java.io.IOException ioe) {
                // ignore
            }
            linefeed();
            _addIndentation = true;
        }
    }

    public void writeln(final double d) {
        synchronized (lock) {
            ensureIndent();
            try {
                _out.write(String.valueOf(d));
            } catch (java.io.IOException ioe) {
                // ignore
            }
            linefeed();
            _addIndentation = true;
        }
    }

    public void writeln(final Object obj) {
        synchronized (lock) {
            ensureIndent();
            try {
                _out.write(obj.toString());
            } catch (java.io.IOException ioe) {
                // ignore
            }
            linefeed();
            _addIndentation = true;
        }
    }

    public void writeln(final String string) {
        synchronized (lock) {
            if (string.length() > 0) {
                ensureIndent();
                try {
                    _out.write(string);
                } catch (java.io.IOException ioe) {
                    // ignore
                }
            }
            linefeed();
            _addIndentation = true;
        }
    }

    public void writeln(final char[] chars) {
        synchronized (lock) {
            ensureIndent();
            try {
                _out.write(chars);
            } catch (java.io.IOException ioe) {
                // ignore
            }
            linefeed();
            _addIndentation = true;
        }
    }

    public void writeln(final boolean b) {
        synchronized (lock) {
            ensureIndent();
            try {
                _out.write(String.valueOf(b));
            } catch (java.io.IOException ioe) {
                // ignore
            }
            linefeed();
            _addIndentation = true;
        }
    }

    public void writeln(final char c) {
        synchronized (lock) {
            ensureIndent();
            try {
                _out.write(c);
            } catch (java.io.IOException ioe) {
                // ignore
            }
            linefeed();
            _addIndentation = true;
        }
    }

    //--------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void close() {
        try {
            _out.close();
        } catch (java.io.IOException ioe) {
            // ignore
        }
    }

    /**
     * {@inheritDoc}
     */
    public void flush() {
        try {
            _out.flush();
        } catch (java.io.IOException ioe) {
            // ignore
        }
    }

    /**
     * {@inheritDoc}
     */
    public void write(final String s, final int off, final int len) {
        synchronized (lock) {
            ensureIndent();
            try {
                _out.write(s, off, len);
            } catch (java.io.IOException ioe) {
                // ignore
            }
            if (_autoflush) { flush(); }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void write(final String s) {
        synchronized (lock) {
            ensureIndent();
            try {
                _out.write(s);
            } catch (java.io.IOException ioe) {
                // ignore
            }
            if (_autoflush) { flush(); }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void write(final char[] buf) {
        synchronized (lock) {
            ensureIndent();
            try {
                _out.write(buf);
            } catch (java.io.IOException ioe) {
                // ignore
            }
            if (_autoflush) { flush(); }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void write(final int c) {
        synchronized (lock) {
            ensureIndent();
            try {
                _out.write(c);
            } catch (java.io.IOException ioe) {
                // ignore
            }
            if (_autoflush) { flush(); }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void write(final char[] buf, final int off, final int len) {
        synchronized (lock) {
            ensureIndent();
            try {
                _out.write(buf, off, len);
            } catch (java.io.IOException ioe) {
                // ignore
            }
            if (_autoflush) { flush(); }
        }
    }

    //--------------------------------------------------------------------------
}
