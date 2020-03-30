package sortpom.wrapper.content;

import org.jdom.Element;
import sortpom.parameter.DependencySortOrder;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author bjorn
 * @since 2012-09-20
 */
public class ChildElementSorter {
    static final ChildElementSorter EMPTY_SORTER = new ChildElementSorter();
    private static final String GROUP_ID_NAME = "GROUPID";

    private final LinkedHashMap<String, String> childElementTextMappedBySortedNames = new LinkedHashMap<>();
    private final List<String> prioritizedGroups;

    public ChildElementSorter(DependencySortOrder dependencySortOrder, List<Element> children) {
        Collection<String> childElementNames = dependencySortOrder.getChildElementNames();

        childElementNames.forEach(name ->
                childElementTextMappedBySortedNames.put(name.toUpperCase(), ""));

        children.forEach(element ->
                childElementTextMappedBySortedNames.replace(element.getName().toUpperCase(), element.getText()));

        this.prioritizedGroups = dependencySortOrder.getPrioritizedGroups();
    }

    private ChildElementSorter() {
        this.prioritizedGroups = Collections.emptyList();
    }

    boolean compareTo(ChildElementSorter otherChildElementSorter) {
        Function<Map.Entry<String, String>, String> getOtherTextFunc = entry ->
                otherChildElementSorter.childElementTextMappedBySortedNames.get(entry.getKey());

        int compare = childElementTextMappedBySortedNames.entrySet().stream()
                .map(entry -> compareTexts(entry.getKey(), entry.getValue(), getOtherTextFunc.apply(entry)))
                .filter(i -> i != 0)
                .findFirst()
                .orElse(0);

        return compare < 0;
    }

    private int compareTexts(String key, String text, String otherText) {
        if ("scope".equalsIgnoreCase(key)) {
            return compareScope(text, otherText);
        }
        if (GROUP_ID_NAME.equalsIgnoreCase(key)) {
            text = adjustGroupPrecedence(text);
            otherText = adjustGroupPrecedence(otherText);
        }

        return text.compareToIgnoreCase(otherText);
    }

    private String adjustGroupPrecedence(String text) {
        for (int index = 0; index < prioritizedGroups.size(); index++) {
            String s = prioritizedGroups.get(index);
            if (text.equals(s) || text.startsWith(s + ".")) {
                return String.format("!%03d%s", index, text);
            }
        }
        return text;
    }

    private int compareScope(String childElementText, String otherChildElementText) {
        return Scope.getScope(childElementText).compareTo(Scope.getScope(otherChildElementText));
    }

    void setEmptyPluginGroupIdValue(String defaultValue) {
        childElementTextMappedBySortedNames.computeIfPresent(GROUP_ID_NAME, (k, oldValue) -> oldValue.isEmpty() ? defaultValue : oldValue);
    }

    @Override
    public String toString() {
        return "ChildElementSorter{" +
                "childElementTexts=" + childElementTextMappedBySortedNames.values() +
                '}';
    }

    private enum Scope {
        COMPILE, PROVIDED, SYSTEM, RUNTIME, IMPORT, TEST, OTHER;

        static Scope getScope(String scope) {
            if (scope == null || scope.isEmpty()) {
                return COMPILE;
            }
            Scope[] values = Scope.values();
            for (Scope value : values) {
                if (scope.equalsIgnoreCase(value.name())) {
                    return value;
                }
            }
            return OTHER;
        }
    }
}
