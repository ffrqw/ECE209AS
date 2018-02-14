package org.springframework.web.util;

import java.io.Serializable;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public final class UriTemplate implements Serializable {
    private static final Pattern NAMES_PATTERN = Pattern.compile("\\{([^/]+?)\\}");
    private final Pattern matchPattern;
    private final UriComponents uriComponents;
    private final String uriTemplate;
    private final List<String> variableNames;

    private static class Parser {
        private final StringBuilder patternBuilder;
        private final List<String> variableNames;

        private Parser(String uriTemplate) {
            this.variableNames = new LinkedList();
            this.patternBuilder = new StringBuilder();
            String str = "'uriTemplate' must not be null";
            if (StringUtils.hasText(uriTemplate)) {
                Matcher matcher = UriTemplate.NAMES_PATTERN.matcher(uriTemplate);
                int end = 0;
                while (matcher.find()) {
                    this.patternBuilder.append(quote(uriTemplate, end, matcher.start()));
                    String match = matcher.group(1);
                    int colonIdx = match.indexOf(58);
                    if (colonIdx == -1) {
                        this.patternBuilder.append("(.*)");
                        this.variableNames.add(match);
                    } else if (colonIdx + 1 == match.length()) {
                        throw new IllegalArgumentException("No custom regular expression specified after ':' in \"" + match + "\"");
                    } else {
                        String variablePattern = match.substring(colonIdx + 1, match.length());
                        this.patternBuilder.append('(');
                        this.patternBuilder.append(variablePattern);
                        this.patternBuilder.append(')');
                        this.variableNames.add(match.substring(0, colonIdx));
                    }
                    end = matcher.end();
                }
                this.patternBuilder.append(quote(uriTemplate, end, uriTemplate.length()));
                int lastIdx = this.patternBuilder.length() - 1;
                if (lastIdx >= 0 && this.patternBuilder.charAt(lastIdx) == '/') {
                    this.patternBuilder.deleteCharAt(lastIdx);
                    return;
                }
                return;
            }
            throw new IllegalArgumentException(str);
        }

        private static String quote(String fullPath, int start, int end) {
            if (start == end) {
                return "";
            }
            return Pattern.quote(fullPath.substring(start, end));
        }
    }

    public UriTemplate(String uriTemplate) {
        Parser parser = new Parser(uriTemplate);
        this.uriTemplate = uriTemplate;
        this.variableNames = Collections.unmodifiableList(parser.variableNames);
        this.matchPattern = Pattern.compile(parser.patternBuilder.toString());
        this.uriComponents = UriComponentsBuilder.fromUriString(uriTemplate).build();
    }

    public final URI expand(Object... uriVariableValues) {
        UriComponents uriComponents = this.uriComponents;
        Assert.notNull(uriVariableValues, "'uriVariableValues' must not be null");
        return uriComponents.expandInternal(new VarArgsTemplateVariables(uriVariableValues)).encode().toUri();
    }

    public final String toString() {
        return this.uriTemplate;
    }
}
