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

package org.kie.projecteditor.shared.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class KSessionModel
        implements HasName {

    private String name;
    private String type;
    private ClockTypeOption clockType = ClockTypeOption.REALTIME;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public ClockTypeOption getClockType() {
        return clockType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setClockType(ClockTypeOption clockTypeEnum) {
        this.clockType = clockTypeEnum;
    }
}
