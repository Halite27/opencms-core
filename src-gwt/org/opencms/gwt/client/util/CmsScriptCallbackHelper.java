/*
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) Alkacon Software GmbH (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.gwt.client.util;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Helper class used to create a native Javascript function to call into GWT code.<p>
 */
public class CmsScriptCallbackHelper {

    /** The stored arguments. */
    protected JavaScriptObject m_arguments;

    /**
     * Creates a native JS function which, when executed, calls this object's run() method.<p>
     *
     * @return the JS function
     */
    public native JavaScriptObject createCallback() /*-{
        var self = this;
        return function() {
            self.@org.opencms.gwt.client.util.CmsScriptCallbackHelper::setArguments(Lcom/google/gwt/core/client/JavaScriptObject;)(arguments);
            self.@org.opencms.gwt.client.util.CmsScriptCallbackHelper::run()();
        }
    }-*/;

    /**
     * This method is called by the callback created by createCallback().<p>
     */
    public void run() {
        // do nothing
    }

    /**
     * Sets the function arguments so they can be accessed later in the run() method.<p>
     *
     * @param arguments the function arguments
     */
    public void setArguments(JavaScriptObject arguments) {

        m_arguments = arguments;
    }

}
