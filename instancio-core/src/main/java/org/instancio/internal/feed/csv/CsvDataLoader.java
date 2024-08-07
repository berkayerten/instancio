/*
 * Copyright 2022-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.instancio.internal.feed.csv;

import org.instancio.feed.DataSource;
import org.instancio.internal.feed.DataLoader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A very basic CSV parser does not support quoted values
 * or escape characters.
 */
public final class CsvDataLoader implements DataLoader<List<String[]>> {

    private final String commentChar;

    CsvDataLoader(final InternalCsvFormatOptions formatOptions) {
        this.commentChar = formatOptions.getCommentPrefix();
    }

    @Override
    @SuppressWarnings("PMD.CyclomaticComplexity")
    public List<String[]> load(final DataSource dataSource) throws Exception {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(getInputStream(dataSource)))) {

            final List<String[]> results = new ArrayList<>();

            String line;
            while ((line = br.readLine()) != null) { // NOPMD
                if (line.isEmpty() || line.startsWith(commentChar)) {
                    continue;
                }
                final String[] tokens = parseLine(line);
                results.add(tokens);
            }
            return results;
        }
    }

    private static String[] parseLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    currentToken.append(c);
                    i++; // NOPMD
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                tokens.add(trimQuotes(currentToken.toString().trim()));
                currentToken.setLength(0);
            } else {
                if (!inQuotes && Character.isWhitespace(c)) {
                    continue;
                }
                currentToken.append(c);
            }
        }

        tokens.add(trimQuotes(currentToken.toString().trim()));
        return tokens.toArray(new String[0]);
    }

    private static String trimQuotes(String token) {
        String result = token;
        if (result.startsWith("\"") && result.endsWith("\"")) {
            result = result.substring(1, result.length() - 1);
        }
        return result.replace("\"\"", "\"");
    }
}
