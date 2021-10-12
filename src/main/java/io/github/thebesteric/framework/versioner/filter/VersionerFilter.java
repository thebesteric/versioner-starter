package io.github.thebesteric.framework.versioner.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonObject;
import io.github.thebesteric.framework.versioner.core.VersionerHandler;
import io.github.thebesteric.framework.versioner.core.VersionerManager;
import io.github.thebesteric.framework.versioner.utils.JsonUtils;
import javafx.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class VersionerFilter implements Filter {

    private final VersionerManager versionManager;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();

        VersionerManager.VersionInfo versionInfo = versionManager.get(requestURI);
        if (versionInfo != null) {
            ResponseWrapper responseWrapper = new ResponseWrapper(response);
            filterChain.doFilter(request, responseWrapper);
            byte[] content = responseWrapper.getContent();
            String origin = new String(content, StandardCharsets.UTF_8);
            try {
                JsonObject jsonObject = JsonUtils.toJsonNode(origin);
                String key = versionInfo.getKey();
                if (StringUtils.hasLength(key)) {
                    JsonObject keyJsonObject = jsonObject.getAsJsonObject(key);
                    tidyVersion(versionInfo.getExcludeFields(), keyJsonObject);
                } else {
                    tidyVersion(versionInfo.getExcludeFields(), jsonObject);
                }
                servletResponse.getOutputStream().write(jsonObject.toString().getBytes(StandardCharsets.UTF_8));
            } catch (JsonProcessingException ex) {
                filterChain.doFilter(request, response);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    public void tidyVersion(Set<Pair<String, Field>> excludeFields, JsonObject jsonObject) {
        for (Pair<String, Field> excludeField : excludeFields) {
            String parent = excludeField.getKey();
            Field field = excludeField.getValue();
            if (!StringUtils.hasLength(parent)) {
                jsonObject.remove(field.getName());
                continue;
            }
            JsonObject curJsonObject = jsonObject;
            for (String parentKey : parent.split(VersionerHandler.KEY_SEPARATOR)) {
                curJsonObject = curJsonObject.getAsJsonObject(parentKey);
            }
            Set<Pair<String, Field>> collect = excludeFields.stream()
                    .filter(pair -> pair.getKey().equals(parent))
                    .map(pair -> new Pair<>("", pair.getValue()))
                    .collect(Collectors.toSet());
            tidyVersion(collect, curJsonObject);
        }
    }
}
