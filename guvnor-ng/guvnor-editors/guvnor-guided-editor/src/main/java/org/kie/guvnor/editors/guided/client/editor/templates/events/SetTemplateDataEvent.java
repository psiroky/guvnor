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
package org.kie.guvnor.editors.guided.client.editor.templates.events;

import com.google.gwt.event.shared.GwtEvent;
import org.kie.guvnor.widgets.decoratedgrid.client.widget.events.SetModelEvent;
import org.kie.guvnor.editors.guided.model.templates.TemplateModel;

/**
 * An event to set the underlying Template Data in the table
 */
public class SetTemplateDataEvent extends SetModelEvent<TemplateModel> {

    public SetTemplateDataEvent( TemplateModel model ) {
        super( model );
    }

    public static GwtEvent.Type<SetModelEvent.Handler<TemplateModel>> TYPE = new GwtEvent.Type<SetModelEvent.Handler<TemplateModel>>();

    @Override
    public GwtEvent.Type<SetModelEvent.Handler<TemplateModel>> getAssociatedType() {
        return TYPE;
    }

}