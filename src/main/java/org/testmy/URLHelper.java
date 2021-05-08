package org.testmy;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.testmy.error.TestRuntimeException;

public class URLHelper {
    public static String parseObjectId(String objectUrl) {
        final String[] elements = objectUrl.split("/");
        return elements[elements.length - 2];
    }

    public static String parseObjectType(String objectUrl) {
        final String[] elements = objectUrl.split("/");
        return elements[elements.length - 3];
    }

    public static String extractMainUrl(final String completeUrl) {

        try {
            final URL url = new URL(completeUrl);
            return String.format("%s://%s", url.getProtocol(), url.getHost());
        }
        catch (final MalformedURLException e) {
            throw new TestRuntimeException(e);
        }
    }

    public static String extractDomain(String completeUrl) {

        try {
            final URL url = new URL(completeUrl);
            return url.getHost();
        }
        catch (final MalformedURLException e) {
            throw new TestRuntimeException(e);
        }
    }

    public static String encode(final String input) {

        try {
            return URLEncoder.encode(input, StandardCharsets.UTF_8.toString());
        }
        catch (final UnsupportedEncodingException e) {
            throw new TestRuntimeException(e);
        }
    }
}
