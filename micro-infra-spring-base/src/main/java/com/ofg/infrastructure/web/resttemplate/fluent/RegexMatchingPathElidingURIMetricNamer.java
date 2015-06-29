package com.ofg.infrastructure.web.resttemplate.fluent;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.util.StringUtils.*;

/**
 * A {@link URIMetricNamer} that elides all the URI path parts except
 * the first one, using an underscore character. It also allows for specifying predefined metric names for URI-s with
 * a path matching a regular expression, using the {@code pathPatternReplacements} constructor parameter.
 * (Note the replacements are subject to further processing. See below for details.)
 * <p/>
 * The returned metric name has the format of: ${uri.host}.${uri.port}.${uri.path}, where:
 * <ul>
 * <li>the ${uri.host} is the URI's host with dots replaced with underscores,
 * <li>the ${uri.path} is the URI's path <b>without</b> the parameters and the hash, post-processed as described below.
 * </ul>
 * The ${uri.path} is post-processed as follows: in case any of the path regex patterns matches, the corresponding
 * replacement is used as the ${uri.path}. Otherwise, the metric name will be the uri path with all its parts
 * (substrings between backslashes) except the first one elided with an underscore. Finally, in both cases, all the dots
 * in the resulting path are replaced with underscores and (afterwards) all the backslashes are replaced with dots.
 */
public class RegexMatchingPathElidingURIMetricNamer implements URIMetricNamer {

    private LinkedHashMap<Pattern, String> pathPatternReplacements = new LinkedHashMap<>();

    public RegexMatchingPathElidingURIMetricNamer() {
        this(new LinkedHashMap<String, String>());
    }

    public RegexMatchingPathElidingURIMetricNamer(LinkedHashMap<String, String> pathPatternReplacements) {
        for (Map.Entry<String, String> patternReplacement : pathPatternReplacements.entrySet()) {
            String pattern = patternReplacement.getKey();
            checkArgument(hasText(pattern), "Replacements must not be null nor empty nor all-whitespace");
            this.pathPatternReplacements.put(Pattern.compile(pattern), patternReplacement.getValue());
        }
    }

    @Override
    public String metricNameFor(URI uri) {
        final String pathPart = getPathPart(uri);
        final int port = (uri.getPort() > 0) ? uri.getPort() : 80;
        final String host = uri.getHost()
                               .replaceAll("[\\[\\]]", "")
                               .replaceAll("\\.", "_")
                               .replaceAll(":+", "_");
        return trimCharacter(host + "." + port + "." + pathPart, '.');
    }

    private String getPathPart(URI uri) {
        final String originalPathTrimmed = trimCharacter(uri.getPath(), '/');
        String matchingPatternReplacement = findMatchingPatternReplacement(originalPathTrimmed);
        String path = matchingPatternReplacement != null ?
            matchingPatternReplacement :
            elideSubsequentPathParts(originalPathTrimmed);
        return fixSpecialCharacters(path);
    }

    private String findMatchingPatternReplacement(String uriPathTrimmed) {
        String matchingPatternReplacement = null;
        for (Map.Entry<Pattern, String> patternReplacement : pathPatternReplacements.entrySet()) {
            if (patternReplacement.getKey().matcher(uriPathTrimmed).matches()) {
                matchingPatternReplacement = patternReplacement.getValue();
                break;
            }
        }
        return matchingPatternReplacement;
    }

    private String elideSubsequentPathParts(String path) {
        int firstSlash = path.indexOf("/");
        boolean pathIsMultipart = firstSlash != -1;
        if (pathIsMultipart) {
            String firstPart = path.substring(0, firstSlash);
            String subsequentParts = path.substring(firstSlash);
            String subsequentPartsElided = subsequentParts.replaceAll("[^/]+", "_");
            path = firstPart + subsequentPartsElided;
        }
        return path;
    }

    private static String fixSpecialCharacters(String value) {
        final String result = value.replaceAll("\\.", "_").replaceAll("/", ".");
        return trimCharacter(result, '.');
    }

    private static String trimCharacter(String string, Character character) {
        return trimTrailingCharacter(trimLeadingCharacter(string, character), character);
    }

}
