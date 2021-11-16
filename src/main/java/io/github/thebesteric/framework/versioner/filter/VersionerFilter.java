package io.github.thebesteric.framework.versioner.filter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.thebesteric.framework.versioner.configuration.VersionerProperties;
import io.github.thebesteric.framework.versioner.core.VersionContext;
import io.github.thebesteric.framework.versioner.core.VersionHelper;
import io.github.thebesteric.framework.versioner.core.VersionManager;
import io.github.thebesteric.framework.versioner.domain.ArrayIndexPointer;
import io.github.thebesteric.framework.versioner.domain.FieldDefinition;
import io.github.thebesteric.framework.versioner.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class VersionerFilter implements Filter {

    private final VersionManager versionManager;

    private final VersionerProperties properties;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        if (!properties.isEnable()) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();
        // set uri into Context
        VersionContext.setURI(requestURI);
        // set appVersion into Context
        VersionContext.setAppVersion(properties.getAppVersionName(), request);
        // set version into Context
        VersionManager.VersionInfo versionInfo = versionManager.get(requestURI);
        VersionContext.setVersion(versionInfo != null ? versionInfo.getVersion() : null);

        ResponseWrapper responseWrapper = new ResponseWrapper(response);
        filterChain.doFilter(request, responseWrapper);

        byte[] content = responseWrapper.getContent();
        String origin = new String(content, StandardCharsets.UTF_8);
        JsonObject jsonObject = JsonUtils.toJsonNode(origin);

        // set @Version annotation on Controller
        if (versionInfo != null) {
            try {
                String key = versionInfo.getKey();
                if (StringUtils.hasLength(key)) {
                    JsonObject keyJsonObject = jsonObject.getAsJsonObject(key);
                    tidyVersion(keyJsonObject, versionInfo.getFieldDefinitions());
                } else {
                    tidyVersion(jsonObject, versionInfo.getFieldDefinitions());
                }
                servletResponse.getOutputStream().write(jsonObject.toString().getBytes(StandardCharsets.UTF_8));
            } catch (Exception ex) {
                log.debug("Versioner parse error: {}", ex.getMessage());
                servletResponse.getOutputStream().write(origin.getBytes(StandardCharsets.UTF_8));
            } finally {
                // clear context
                VersionContext.clear();
            }
        }
        // set inner version control in method
        else if (!VersionHelper.isEmpty()) {
            try {
                VersionHelper.ExcludeInfo excludeInfo = VersionHelper.get();
                // match all or match uri
                if (excludeInfo.getUri() == null || requestURI.equals(excludeInfo.getUri())) {
                    tidyVersionByFieldNames(jsonObject, excludeInfo.getExcludeFields());
                }
                servletResponse.getOutputStream().write(jsonObject.toString().getBytes(StandardCharsets.UTF_8));
            } catch (Exception ex) {
                log.debug("Versioner parse error: {}", ex.getMessage());
                servletResponse.getOutputStream().write(origin.getBytes(StandardCharsets.UTF_8));
            } finally {
                VersionHelper.clear();
                versionManager.arrayIndexPointer.remove();
            }
        }
        // no version control
        else {
            servletResponse.getOutputStream().write(jsonObject.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    private void tidyVersionByFieldNames(JsonElement jsonElement, Set<String> fieldNames) {
        for (String fieldName : fieldNames) {
            String[] segmentArr = null;
            if (fieldName.contains(".")) {
                String firstSegment = fieldName.substring(0, fieldName.indexOf("."));
                String lastSegment = fieldName.substring(fieldName.indexOf(".") + 1);
                segmentArr = new String[]{firstSegment, lastSegment};
            }
            // handle complex object
            if (segmentArr != null) {
                List<String> list = new ArrayList<>(Arrays.asList(segmentArr));
                String parentFieldName = list.remove(0);
                int index = -1;
                if (parentFieldName.contains("[") && parentFieldName.contains("]")) {
                    index = Integer.parseInt(parentFieldName.substring(parentFieldName.indexOf("[") + 1, parentFieldName.indexOf("]")));
                }
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonElement parentJsonElement;
                if (index != -1) {
                    parentFieldName = parentFieldName.substring(0, parentFieldName.indexOf("["));
                }
                parentJsonElement = jsonObject.get(parentFieldName);
                // maybe complex Array or Map
                if (parentJsonElement.isJsonArray()) {
                    JsonArray jsonArray = parentJsonElement.getAsJsonArray();
                    if (index != -1) {
                        if (index < jsonArray.size()) {
                            tidyVersionByFieldNames(jsonArray.get(index), new HashSet<>(list));
                        }
                    } else {
                        for (JsonElement element : jsonArray) {
                            tidyVersionByFieldNames(element, new HashSet<>(list));
                        }
                    }
                }
                // complex Object
                else {
                    tidyVersionByFieldNames(parentJsonElement, new HashSet<>(list));
                }
            }
            // handle normal Array, eg: List<String>
            else if (fieldName.contains("[") && fieldName.lastIndexOf("]") != -1) {
                int index = Integer.parseInt(fieldName.substring(fieldName.indexOf("[") + 1, fieldName.indexOf("]")));
                fieldName = fieldName.substring(0, fieldName.indexOf("["));
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonElement element = jsonObject.get(fieldName);
                if (element.isJsonArray()) {
                    JsonArray jsonArray = element.getAsJsonArray();
                    ArrayIndexPointer arrayIndexPointer = versionManager.arrayIndexPointer.get();
                    arrayIndexPointer.init(jsonArray.size());
                    index = arrayIndexPointer.calcActualIndex(index);
                    if (index < jsonArray.size()) {
                        // remove exclude field
                        jsonArray.remove(index);
                        arrayIndexPointer.delete(index);
                    }
                }
            }
            // handle direct object
            else {
                // remove exclude field
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                jsonObject.remove(fieldName);
            }
        }
    }

    private void tidyVersion(JsonElement jsonElement, Set<FieldDefinition> fieldDefinitions) {
        if (jsonElement.isJsonArray()) {
            for (JsonElement element : jsonElement.getAsJsonArray()) {
                tidyVersion(element, fieldDefinitions);
            }
        } else {
            if (jsonElement.isJsonPrimitive()) {
                return;
            }
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            for (FieldDefinition fieldDefinition : fieldDefinitions) {
                String fieldName = fieldDefinition.getField().getName();
                if (fieldDefinition.isShowBeRemove()) {
                    jsonObject.remove(fieldName);
                    continue;
                }
                JsonElement subElement = jsonObject.get(fieldName);
                if (subElement == null || subElement.isJsonPrimitive()) {
                    continue;
                }
                if (subElement.isJsonObject()) {
                    JsonObject subJsonObject = subElement.getAsJsonObject();
                    // Check whether it is a MAP.
                    List<String> elementKeys = new ArrayList<>();
                    for (String key : subJsonObject.keySet()) {
                        JsonElement element = subJsonObject.get(key);
                        if (element.isJsonObject()) {
                            List<String> keys = element.getAsJsonObject().keySet().stream().map(String::trim).collect(Collectors.toList());
                            elementKeys.add(String.join(",", keys));
                        }
                    }
                    // Maybe MAP
                    if (subJsonObject.keySet().size() == elementKeys.size() && new HashSet<>(elementKeys).size() == 1) {
                        for (String key : subJsonObject.keySet()) {
                            JsonElement mapElement = subJsonObject.get(key);
                            tidyVersion(mapElement, fieldDefinition.getSubFieldDefinitions());
                        }
                    }
                    // Must be POJO
                    else {
                        tidyVersion(subJsonObject, fieldDefinition.getSubFieldDefinitions());
                    }
                } else if (subElement.isJsonArray()) {
                    JsonArray subJsonArray = subElement.getAsJsonArray();
                    for (JsonElement subJsonElement : subJsonArray) {
                        tidyVersion(subJsonElement, fieldDefinition.getSubFieldDefinitions());
                    }
                }
            }
        }
    }
}
