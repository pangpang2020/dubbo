/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.config.metadata;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.metadata.WritableMetadataService;
import org.apache.dubbo.registry.client.DefaultServiceInstance;
import org.apache.dubbo.registry.client.ServiceInstance;
import org.apache.dubbo.registry.client.ServiceInstanceCustomizer;
import org.apache.dubbo.rpc.model.ApplicationModel;

import java.util.Set;

/**
 * The {@link ServiceInstanceCustomizer} to customize the {@link ServiceInstance#getPort() port} of service instance.
 *
 * @since 2.7.5
 */
public class ServiceInstanceHostPortCustomizer implements ServiceInstanceCustomizer {

    @Override
    public void customize(ServiceInstance serviceInstance) {

        if (serviceInstance.getPort() != null) {
            return;
        }

        WritableMetadataService writableMetadataService = WritableMetadataService.getDefaultExtension();

        String host = null;
        Integer port = null;
        Set<URL> urls = writableMetadataService.getExportedServiceURLs();
        if (CollectionUtils.isNotEmpty(urls)) {
            String preferredProtocol = ApplicationModel.getApplicationConfig().getProtocol();
            if (preferredProtocol != null) {
                for (URL exportedURL : urls) {
                    if (preferredProtocol.equals(exportedURL.getProtocol())) {
                        host = exportedURL.getHost();
                        port = exportedURL.getPort();
                        break;
                    }
                }
            } else {
                URL url = urls.iterator().next();
                host = url.getHost();
                port = url.getPort();
            }
            if (serviceInstance instanceof DefaultServiceInstance) {
                DefaultServiceInstance instance = (DefaultServiceInstance) serviceInstance;
                instance.setHost(host);
                instance.setPort(port);
                instance.setId(host + ":" + port);
            }
        }
    }
}
