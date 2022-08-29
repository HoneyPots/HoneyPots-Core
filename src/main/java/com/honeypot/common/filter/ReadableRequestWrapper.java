package com.honeypot.common.filter;

import com.amazonaws.util.IOUtils;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ReadableRequestWrapper extends HttpServletRequestWrapper {

    private final Charset encoding;

    private byte[] rawData;

    private Map<String, String[]> params = new HashMap<>();

    public ReadableRequestWrapper(HttpServletRequest request) {
        super(request);
        this.params.putAll(request.getParameterMap());

        String charEncoding = request.getCharacterEncoding();
        this.encoding = StringUtils.isBlank(charEncoding) ? StandardCharsets.UTF_8 : Charset.forName(charEncoding);

        try {
            InputStream is = request.getInputStream();
            this.rawData = IOUtils.toByteArray(is);

            String collect = this.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            if (StringUtils.isEmpty(collect)) {
                return;
            }

            if (request.getContentType() != null && request.getContentType().contains(
                    ContentType.MULTIPART_FORM_DATA.getMimeType())) {
                return;
            }

            JSONParser jsonParser = new JSONParser();
            Object parse = jsonParser.parse(collect);

            if (parse instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) jsonParser.parse(collect);
                setParameter("requestBody", jsonArray.toJSONString());
            } else {
                JSONObject jsonObject = (JSONObject) jsonParser.parse(collect);
                for (Object o : jsonObject.keySet()) {
                    String key = (String) o;
                    setParameter(key, jsonObject.get(key).toString().replace("\"", "\\\""));
                }
            }
        } catch (Exception e) {
            log.error("ReadableRequestWrapper init error", e);
        }
    }

    @Override
    public String getParameter(String name) {
        String[] paramArray = getParameterValues(name);
        if (paramArray != null && paramArray.length > 0) {
            return paramArray[0];
        } else {
            return null;
        }
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return Collections.unmodifiableMap(params);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(params.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] result = null;
        String[] dummyParamValue = params.get(name);

        if (dummyParamValue != null) {
            result = new String[dummyParamValue.length];
            System.arraycopy(dummyParamValue, 0, result, 0, dummyParamValue.length);
        }
        return result;
    }

    private void setParameter(String name, String value) {
        String[] param = {value};
        setParameter(name, param);
    }

    private void setParameter(String name, String[] values) {
        params.put(name, values);
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.rawData);

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // Do nothing
            }

            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream(), this.encoding));
    }
}