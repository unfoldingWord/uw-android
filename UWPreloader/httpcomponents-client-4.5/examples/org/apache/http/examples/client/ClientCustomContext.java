/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.http.examples.client;

import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.List;

/**
 * This example demonstrates the use of a local HTTP context populated with
 * custom attributes.
 */
public class ClientCustomContext {

    public final static void main(String[] args) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            // Create a local instance of cookie store
            CookieStore cookieStore = new BasicCookieStore();

            // Create local HTTP context
            HttpClientContext localContext = HttpClientContext.create();
            // Bind custom cookie store to the local context
            localContext.setCookieStore(cookieStore);

            HttpGet httpget = new HttpGet("http://localhost/");
            System.out.println("Executing request " + httpget.getRequestLine());

            // Pass local context as a parameter
            CloseableHttpResponse response = httpclient.execute(httpget, localContext);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                List<Cookie> cookies = cookieStore.getCookies();
                for (int i = 0; i < cookies.size(); i++) {
                    System.out.println("Local cookie: " + cookies.get(i));
                }
                EntityUtils.consume(response.getEntity());
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }

}

