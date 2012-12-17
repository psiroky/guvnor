/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.jcr2vfsmigration;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.server.repository.GuvnorBootstrapConfiguration;
import org.jboss.weld.context.bound.BoundRequestContext;
import org.jboss.weld.context.bound.BoundSessionContext;
import org.kie.guvnor.jcr2vfsmigration.config.MigrationConfig;
import org.kie.guvnor.jcr2vfsmigration.migrater.AssetMigrater;
import org.kie.guvnor.jcr2vfsmigration.migrater.CategoryMigrater;
import org.kie.guvnor.jcr2vfsmigration.migrater.ModuleMigrater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class Jcr2VfsMigrater {

    protected static final Logger logger = LoggerFactory.getLogger(Jcr2VfsMigrater.class);

    @Inject
    protected MigrationConfig migrationConfig;

    @Inject
    protected GuvnorBootstrapConfiguration guvnorBootstrapConfiguration;

    @Inject
    protected ModuleMigrater moduleMigrater;
    @Inject
    protected AssetMigrater assetMigrater;
    @Inject
    protected CategoryMigrater categoryMigrater;

    @Inject
    protected BoundSessionContext sessionContext;
    protected Map<String, Object> sessionDataStore;

    @Inject
    protected BoundRequestContext requestContext;
    protected Map<String, Object> requestDataStore;

    public void parseArgs(String[] args) {
        migrationConfig.parseArgs(args);
    }

    private void setupDirectories() {
        guvnorBootstrapConfiguration.getProperties().put("repository.root.directory",
                migrationConfig.getInputJcrRepository().getAbsolutePath());
        System.setProperty("org.kie.nio.git.dir", migrationConfig.getOutputVfsRepository().getAbsolutePath());
    }

    public void migrateAll() {
        logger.info("Migration started: Reading from inputJcrRepository ({}).",
                migrationConfig.getInputJcrRepository().getAbsolutePath());
        setupDirectories();
        startContexts();
//    //TO-DO-LIST:
//    //1. How to migrate the globalArea (moduleServiceJCR.listModules() wont return globalArea)
//    //2. How to handle asset imported from globalArea. assetServiceJCR.findAssetPage will return assets imported from globalArea
//    //(like a symbol link). Use Asset.getMetaData().getModuleName()=="globalArea" to determine if the asset is actually from globalArea.
//    //3. Do we want to migrate archived assets and archived packages? probably not...
//    //4. Do we want to migrate package snapshot? probably not...As long as we migrate package history correctly, users can always build a package
//    //with the specified version by themselves.
        moduleMigrater.migrateAll();
        assetMigrater.migrateAll();
        categoryMigrater.migrateAll();
        //  TODO: migratePackagePermissions, migrateRolesAndPermissionsMetaData
        endContexts();
        logger.info("Migration ended: Written into outputVfsRepository ({}).",
                migrationConfig.getOutputVfsRepository().getAbsolutePath());
    }

    private void startContexts() {
        sessionDataStore = new HashMap<String, Object>();
        sessionContext.associate(sessionDataStore);
        sessionContext.activate();
        requestDataStore = new HashMap<String, Object>();
        requestContext.associate(requestDataStore);
        requestContext.activate();
    }

    private void endContexts() {
        try {
            requestContext.invalidate();
            requestContext.deactivate();
        } finally {
            requestContext.dissociate(requestDataStore);
        }
        try {
            sessionContext.invalidate();
            sessionContext.deactivate();
        } finally {
            sessionContext.dissociate(sessionDataStore);
        }
    }

}