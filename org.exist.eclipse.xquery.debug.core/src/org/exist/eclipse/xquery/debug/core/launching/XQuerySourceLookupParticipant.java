/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2001-2009 The eXist Project
 *  http://exist-db.org
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *  
 *  $Id:$
 */
package org.exist.eclipse.xquery.debug.core.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;
import org.exist.eclipse.xquery.debug.core.model.XQueryStackFrame;

/**
 * @author <a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>
 *
 */
public class XQuerySourceLookupParticipant extends AbstractSourceLookupParticipant {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant#getSourceName(
	 * java.lang.Object)
	 */
	@Override
	public String getSourceName(Object object) throws CoreException {
		if (object instanceof XQueryStackFrame) {
			return ((XQueryStackFrame) object).getSourceName();
		}
		return null;
	}

}
