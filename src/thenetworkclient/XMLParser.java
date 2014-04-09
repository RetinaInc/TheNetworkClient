/*
 * Copyright (C) 2014 Frank Steiler <frank@steiler.eu>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package thenetworkclient;

import java.awt.TextArea;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Frank Steiler <frank@steiler.eu>
 */
class XMLParser extends DefaultHandler
{
    private boolean student = false, studentId = false, referenceNumer = false, name =false, address = false, errorCode = false, errorText = false;
    int count = 1;
    
    TextArea displayTA;

    @Override
    public void startDocument() throws SAXException
    {
        
    }

    @Override
    public void endDocument() throws SAXException
    {
        
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        if(qName.equalsIgnoreCase("student"))
        {
            student = true;
        }
        else if(qName.equalsIgnoreCase("studentid"))
        {
            studentId = true;
        }
        else if(qName.equalsIgnoreCase("referencenumber"))
        {
            referenceNumer = true;
        }
        else if(qName.equalsIgnoreCase("name"))
        {
            name = true;
        }
        else if(qName.equalsIgnoreCase("address"))
        {
            address = true;
        }
        else if(qName.equalsIgnoreCase("errorCode"))
        {
            errorCode = true;
        }
        else if(qName.equalsIgnoreCase("errorText"))
        {
            errorText = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        name = false;
        address = false;
        referenceNumer = false;
        studentId = false;
        student = false;
        errorCode = false;
        errorText = false;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException
    {

        String s = (new String(ch, start, length)).trim();

        if(student)
        {
            displayTA.append("Student " + count++ + ":\n");
        }
        else if(studentId)
        {
            displayTA.append("Student ID: " + s + "\n");
        }
        else if(referenceNumer)
        {
            displayTA.append("Reference Number: " + s + "\n");
        }
        else if(name)
        {
            displayTA.append("Name: " + s + "\n");
        }
        else if(address)
        {
            displayTA.append("Address: " + s + "\n\n");
        }
        else if(errorCode)
        {
            System.err.println(s);
        }
        else if(errorText)
        {
            displayTA.append("\n\n" + s + "\n\n");
        }
    }

    @Override
    public void error(SAXParseException spe) throws SAXException
    {
        displayTA.append("[XML parse error: " + spe.getMessage() + "]\n");
    }
}