/*
 * spring-mvc-logger logs requests/responses
 *
 * Copyright (c) 2013. Israel Zalmanov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.umessage.letsgo.core.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicLong;

public class LoggingFilter extends OncePerRequestFilter {

    protected static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    private static final String REQUEST_PREFIX = "Request: ";
    private static final String RESPONSE_PREFIX = "Response: ";
    private AtomicLong id = new AtomicLong(1);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            long requestId = id.incrementAndGet();
            request = new RequestWrapper(requestId, request);
            response = new com.umessage.letsgo.core.logging.ResponseWrapper(requestId, response);
        }
        try {
            filterChain.doFilter(request, response);
//            response.flushBuffer();
        } finally {
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (logger.isDebugEnabled()) {
                logRequest(request);
                logResponse(elapsedTime, (com.umessage.letsgo.core.logging.ResponseWrapper) response);
            }
        }

    }

    private void logRequest(HttpServletRequest request) {
        StringBuilder msg = new StringBuilder();
        msg.append(REQUEST_PREFIX);
        if (request instanceof RequestWrapper) {
            msg.append("request id=").append(((RequestWrapper) request).getId()).append("; ");
        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            msg.append("session id=").append(session.getId()).append("; ");
        }
        msg.append("Content-Type=").append(request.getContentType()).append("; ");
        msg.append("Accept=").append(request.getHeader("Accept")).append("; ");
        if (request.getHeader("Authorization") != null) {
            msg.append("Authorization=").append(request.getHeader("Authorization")).append("; ");
        }
        msg.append("uri=").append(request.getRequestURI());
        msg.append('?').append(request.getQueryString());

        if (request instanceof RequestWrapper && !isMultipart(request)) {
            RequestWrapper requestWrapper = (RequestWrapper) request;
            try {
                String charEncoding = requestWrapper.getCharacterEncoding() != null ? requestWrapper.getCharacterEncoding() :
                        "UTF-8";
                msg.append("; payload=").append(new String(requestWrapper.toByteArray(), charEncoding));
            } catch (UnsupportedEncodingException e) {
                logger.warn("Failed to parse request payload", e);
            }

        }
        logger.debug(msg.toString());
    }

    private boolean isMultipart(HttpServletRequest request) {
        return request.getContentType() != null && request.getContentType().startsWith("multipart/form-data");
    }

    private void logResponse(long elapsedTime, com.umessage.letsgo.core.logging.ResponseWrapper response) {
        StringBuilder msg = new StringBuilder();
        msg.append(RESPONSE_PREFIX);
        msg.append("request id=").append((response.getId()));
        msg.append(" finished in ").append(elapsedTime).append("ms");
        msg.append("; content-type=").append((response.getContentType()));
        try {
            msg.append("; payload=").append(new String(response.toByteArray(), response.getCharacterEncoding()));
        } catch (UnsupportedEncodingException e) {
            logger.warn("Failed to parse response payload", e);
        }
        logger.debug(msg.toString());
    }

}
