package com.ofg.infrastructure.web.resttemplate.custom

import com.google.common.base.Charsets
import com.google.common.io.ByteStreams
import org.apache.commons.lang.StringUtils


class InputStreamPrinter {

    static String abbreviate(InputStream inputStream, int maxBytes) {
        final InputStream truncated = ByteStreams.limit(inputStream, maxBytes + 1)
        final byte[] responseBytes = ByteStreams.toByteArray(truncated)
        final String responseAsString = new String(responseBytes, Charsets.UTF_8)
        return StringUtils.abbreviate(responseAsString, Math.max(maxBytes, 4))
    }

}
