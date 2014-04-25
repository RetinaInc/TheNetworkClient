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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * This class represents a post.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class XMLPost 
{
    private int postID;
    private String content;
    private Timestamp timestamp;
    private String publishingUser;

    /**
     * @return the postID
     */
    public int getPostID() {
        return postID;
    }

    /**
     * @param postID the postID to set
     */
    public void setPostID(int postID) {
        this.postID = postID;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the timestamp
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the publishingUser
     */
    public String getPublishingUser() {
        return publishingUser;
    }

    /**
     * @param publishingUser the publishingUser to set
     */
    public void setPublishingUser(String publishingUser) {
        this.publishingUser = publishingUser;
    }
    
    public String toString()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String spaces;
        if(publishingUser.length() <5)
        {
            spaces = "\t\t";
        }
        else
        {
            spaces = "\t";
        }
        return sdf.format(timestamp) + ": " + publishingUser + " wrote:" + spaces + content;
    }
    
}
