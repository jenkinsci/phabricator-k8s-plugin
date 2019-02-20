/*
 * The MIT License
 *
 * Copyright 2019 Bitnami
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bitnami.jenkins.phabk8s;

import com.uber.jenkins.phabricator.credentials.ConduitCredentialsImpl;
import hudson.Extension;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.utils.Serialization;
import org.junit.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests for {@link ConduitCredentialConvertor}.
 */
@Extension
public class ConduitCredentialConvertorTest {
    @Test
    public void canConvert() throws Exception {
        ConduitCredentialConvertor convertor = new ConduitCredentialConvertor();
        assertThat("correct registration of valid type", convertor.canConvert("phabricatorConduit"), is(true));
        assertThat("incorrect type is rejected", convertor.canConvert("something"), is(false));
    }

    @Test
    public void canConvertAValidSecret() throws Exception {
        ConduitCredentialConvertor convertor = new ConduitCredentialConvertor();

        try (InputStream is = get("valid.yaml")) {
            Secret secret = Serialization.unmarshal(is, Secret.class);
            assertThat("The Secret was loaded correctly from disk", notNullValue());
            ConduitCredentialsImpl credential = convertor.convert(secret);
            assertThat(credential, notNullValue());
            assertThat("credential id is mapped correctly", credential.getId(), is("a-test-secret"));
            assertThat("credential description is mapped correctly", credential.getDescription(), is("secret conduit credential from Kubernetes"));
            assertThat("credential token is mapped correctly", credential.getToken().getPlainText(), is("mySecret!"));
            assertThat("credential url is mapped correctly", credential.getUrl(), is("https://phab.mydomain.com"));
            assertThat("credential gateway mapped to the url", credential.getGateway(), is("https://phab.mydomain.com"));
        }
    }

    private static InputStream get(String resource) {
        InputStream is = ConduitCredentialConvertorTest.class.getResourceAsStream(resource);
        if (is == null) {
            fail("failed to load resource " + resource);
        }
        return is;
    }
}