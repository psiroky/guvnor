/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.m2repo.backend.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.drools.kproject.ReleaseIdImpl;
import org.kie.builder.ReleaseId;
import org.kie.guvnor.m2repo.service.M2RepoService;


/**
 * This is for dealing with assets that have an attachment (ie assets that are really an attachment).
 */
//TODO: Basic authentication
public class FileServlet extends HttpServlet {

    private static final long serialVersionUID = 510l;

    @Inject
    private M2RepoService m2RepoService;
    /**
     * Posting accepts content of various types -
     * may be an attachement for an asset, or perhaps a repository import to process.
     */
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException,
                                                       IOException {

        response.setContentType( "text/html" );
        FormData uploadItem = getFormData(request);

        if ( uploadItem.getFile() != null /*&& uploadItem.getUuid() != null*/ ) {
            response.getWriter().write( processUpload( uploadItem ) );

            return;
        }
        response.getWriter().write( "NO-SCRIPT-DATA" );

    }

    private String processUpload(FormData uploadItem) throws IOException {

        // If the file it doesn't exist.
        if ( "".equals( uploadItem.getFile().getName() ) ) {
            throw new IOException( "No file selected." );
        }

        uploadFile( uploadItem );
        uploadItem.getFile().getInputStream().close();

        return "OK";
    }
    
    /**
     * Get the form data from the inbound request.
     */
    @SuppressWarnings("rawtypes")
    public static FormData getFormData(HttpServletRequest request) {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload( factory );
        upload.setHeaderEncoding( "UTF-8" );

        FormData data = new FormData();
        try {
            List items = upload.parseRequest( request );
            Iterator it = items.iterator();
            while ( it.hasNext() ) {
                FileItem item = (FileItem) it.next();
                if ( !item.isFormField() ) {
                    data.setFile( item );
                }
/*                if ( item.isFormField() && item.getFieldName().equals( HTMLFileManagerFields.FORM_FIELD_UUID ) ) {
                    data.setUuid( item.getString() );
                } else if ( !item.isFormField() ) {
                    data.setFile( item );
                }*/
            }
            return data;
        } catch ( FileUploadException e ) {
            //TODO
            //throw new RulesRepositoryException( e );
        }
        
        return null;
    }
    
    /**
     * This attach a file to an asset.
     */
    public void uploadFile(FormData uploadItem) throws IOException {
        InputStream fileData = uploadItem.getFile().getInputStream();
        String fileName = uploadItem.getFile().getName();
        
        //TODO: Get GAV from client
        ReleaseId gav = new ReleaseIdImpl("", "", "");

        m2RepoService.addJar(fileData, gav);
        uploadItem.getFile().getInputStream().close();
    }    
}