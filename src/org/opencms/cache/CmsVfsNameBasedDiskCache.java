/*
 * File   : $Source: /alkacon/cvs/opencms/src/org/opencms/cache/CmsVfsNameBasedDiskCache.java,v $
 * Date   : $Date: 2005/11/12 08:51:34 $
 * Version: $Revision: 1.1.2.1 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Mananagement System
 *
 * Copyright (C) 2002 - 2005 Alkacon Software (http://www.alkacon.com)
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

package org.opencms.cache;

import org.opencms.file.CmsResource;
import org.opencms.util.CmsFileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Implements a name based RFS file based disk cache, that handles parameter based versions of VFS files, 
 * providing a cache for the "online" and another for the "offline" project.<p>
 * 
 * This RFS cache operates only on the file names, not on the RFS "date last modified" information. 
 * 
 * @author Alexander Kandzior 
 * 
 * @version $Revision: 1.1.2.1 $
 * 
 * @since 6.2.0
 */
public class CmsVfsNameBasedDiskCache {

    /** The name of the cache base repository folder in the RFS. */
    private String m_rfsRepository;

    /**
     * Creates a new disk cache.<p>
     * 
     * @param basepath the base path for the cache in the RFS
     * @param foldername the folder name for this cache, to be used a subfolder for the base folder
     */
    public CmsVfsNameBasedDiskCache(String basepath, String foldername) {

        // normalize the given folder name 
        m_rfsRepository = CmsFileUtil.normalizePath(basepath + foldername + File.separatorChar);
    }

    /**
     * Returns the content of the requested file in the disk cache, or <code>null</code> if the
     * file is not found in the cache, or is found but outdated.<p>
     * 
     * @param rfsName the file RFS name to look up in the cache 
     * 
     * @return the content of the requested file in the VFS disk cache, or <code>null</code> 
     */
    public byte[] getCacheContent(String rfsName) {

        try {
            File f = new File(rfsName);
            if (f.exists()) {
                synchronized (this) {
                    // thouch the file with the current date
                    f.setLastModified(System.currentTimeMillis());
                }
                return CmsFileUtil.readFile(f);
            }
        } catch (IOException e) {
            // unable to read content
        }
        return null;
    }

    /**
     * Returns the RFS name to use for caching the given VFS resource with parameters in the disk cache.<p>  
     * 
     * @param online if true, the online disk cache is used, the offline disk cache otherwise
     * @param parameters the parameters of the request to the VFS resource
     * @param resource the VFS resource to generate the cache name for
     * 
     * @return the RFS name to use for caching the given VFS resource with parameters 
     */
    public String getCacheName(CmsResource resource, boolean online, String parameters) {

        // calculate the base cache path for the resource
        String rfsName = CmsFileUtil.getRepositoryName(m_rfsRepository, resource.getRootPath(), online);
        String extension = CmsFileUtil.getFileExtension(rfsName);

        // create a StringBuffer for the result
        StringBuffer buf = new StringBuffer(rfsName.length() + 24);
        buf.append(rfsName.substring(0, rfsName.length() - extension.length()));

        // calculate a hash code that contains the resource DateLastModified, DateCreated and Length    
        StringBuffer ext = new StringBuffer(48);
        ext.append(resource.getDateLastModified());
        ext.append(';');
        ext.append(resource.getDateCreated());
        if (resource.getLength() > 0) {
            ext.append(';');
            ext.append(resource.getLength());
        }
        // append hash code to the result buffer 
        buf.append(ext.toString().hashCode());

        // check if parameters are provided, if so add them as well
        if (parameters != null) {
            buf.append('_');
            buf.append(parameters.hashCode());
        }

        // finally append the extension from the original file name
        buf.append(extension);
        return buf.toString();
    }

    /**
     * Returns the absolute path of the cache repository in the RFS.<p>
     * 
     * @return the absolute path of the cache repository in the RFS
     */
    public String getRepositoryPath() {

        return m_rfsRepository;
    }

    /**
     * Saves the given file content in the disk cache.<p> 
     * 
     * @param rfsName the RFS name of the file to save the content in
     * @param content the content of the file to save
     * 
     * @throws IOException in case of disk access errors
     */
    public void saveCacheFile(String rfsName, byte[] content) throws IOException {

        File f = new File(rfsName);
        File p = f.getParentFile();
        synchronized (this) {
            if (!p.exists()) {
                // create parent folders
                p.mkdirs();
            }
            // write file contents
            FileOutputStream fs = new FileOutputStream(f);
            fs.write(content);
            fs.close();
        }
    }
}