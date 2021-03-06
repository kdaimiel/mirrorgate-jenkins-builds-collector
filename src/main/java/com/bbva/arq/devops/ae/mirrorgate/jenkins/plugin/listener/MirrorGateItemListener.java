/*
 * Copyright 2017 Banco Bilbao Vizcaya Argentaria, S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bbva.arq.devops.ae.mirrorgate.jenkins.plugin.listener;

import hudson.Extension;
import hudson.model.Item;
import hudson.model.listeners.ItemListener;
import java.util.logging.Logger;

@Extension
public class MirrorGateItemListener extends ItemListener {

    private static final Logger LOG = Logger.getLogger(MirrorGateItemListener.class.getName());

    private final MirrorGateListenerHelper helper;

    public MirrorGateItemListener() {
        this.helper = new MirrorGateListenerHelper();

        LOG.fine(">>> MirrorGateItemListener Initialised");
    }

    @Override
    public void onDeleted(final Item item) {
        LOG.fine("onDeletedItem starts");

        helper.sendBuildFromItem(item);

        LOG.fine("onDeletedItem ends");
    }

}
