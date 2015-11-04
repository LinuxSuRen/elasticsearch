/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.payloads.*;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettingsService;

/**
 *
 */
public class DelimitedPayloadTokenFilterFactory extends AbstractTokenFilterFactory {

    public static final char DEFAULT_DELIMITER = '|';
    public static final PayloadEncoder DEFAULT_ENCODER = new FloatEncoder();
    
    static final String ENCODING = "encoding";
    static final String DELIMITER = "delimiter";
    
    char delimiter;
    PayloadEncoder encoder;

    @Inject
    public DelimitedPayloadTokenFilterFactory(Index index, IndexSettingsService indexSettingsService, Environment env, @Assisted String name,
            @Assisted Settings settings) {
        super(index, indexSettingsService.getSettings(), name, settings);
        String delimiterConf = settings.get(DELIMITER);
        if (delimiterConf != null) {
            delimiter = delimiterConf.charAt(0);
        } else {
            delimiter = DEFAULT_DELIMITER;
        }
            
        if (settings.get(ENCODING) != null) {
            if (settings.get(ENCODING).equals("float")) {
                encoder = new FloatEncoder();
            } else if (settings.get(ENCODING).equals("int")) {
                encoder = new IntegerEncoder();
            } else if (settings.get(ENCODING).equals("identity")) {
                encoder = new IdentityEncoder();
            } 
        } else {
            encoder = DEFAULT_ENCODER;
        }
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        DelimitedPayloadTokenFilter filter = new DelimitedPayloadTokenFilter(tokenStream, delimiter, encoder);
        return filter;
    }
    
}
