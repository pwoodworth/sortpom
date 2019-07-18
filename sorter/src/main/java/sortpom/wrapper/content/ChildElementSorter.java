package sortpom.wrapper.content;

import edu.emory.mathcs.backport.java.util.Collections;
import org.jdom.Element;
import sortpom.parameter.DependencySortOrder;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

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
        this.prioritizedGroups = dependencySortOrder.getPrioritizedGroups();
        Collection<String> childElementNames = dependencySortOrder.getChildElementNames();

        childElementNames.forEach(name ->
                childElementTextMappedBySortedNames.put(name.toUpperCase(), ""));

        children.forEach(element ->
                childElementTextMappedBySortedNames.replace(element.getName().toUpperCase(), element.getText()));
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
            text = raisePrecedence(text, prioritizedGroups);
            otherText = raisePrecedence(otherText, prioritizedGroups);
        }

        return text.compareToIgnoreCase(otherText);
    }

    private String raisePrecedence(String text, List<String> match) {
        return IntStream.range(0, match.size())
                .filter(index -> {
                    String s = match.get(index);
                    return text.equals(s) || text.startsWith(s + ".");
                })
                .mapToObj(ii -> String.format("!%03d%s", ii, text))
                .findFirst().orElse(text);
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

        public static Scope getScope(String scope) {
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
