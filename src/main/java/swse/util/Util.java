package swse.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import swse.common.Copyable;
import swse.common.ProvidedItem;
import swse.prerequisite.OrPrerequisite;
import swse.prerequisite.Prerequisite;


public class Util
{
    public static final Pattern DIE_EQUATION_MATCHER = Pattern.compile("((\\d+d\\d+)\\/?)+");
    public static final Pattern NUMBER_PATTERN = Pattern.compile("(-?\\d+)");
    private static Pattern PARENS_CONTENT_MATCHER = Pattern.compile("\\((.*)\\)");
    private static List<String> values = new ArrayList<>();

    public static String getDieEquation(String value, String itemName)
    {
        if(value == null){
            return null;
        }
        Matcher m = DIE_EQUATION_MATCHER.matcher(value);
        if (m.find())
        {
            return m.group();
        }
        return null;
    }

    public static String getPlainNumber(String s)
    {
        if(s.isEmpty()) return null;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return null;
                else continue;
            }
            if(Character.digit(s.charAt(i),10) < 0) return null;
        }
        return s;
    }

    public static String getParensContent(String tok)
    {
        Matcher m = PARENS_CONTENT_MATCHER.matcher(tok);
        if (m.find())
        {
            return m.group(1);
        }
        return null;
    }

    public static void printUnique(Object... strings)
    {
        //.filter(Objects::nonNull)
        String key = Arrays.stream(strings).filter(Objects::nonNull).map(String::valueOf).collect(Collectors.joining(", "));
        if (!values.contains(key))
        {
            values.add(key);
            System.out.println(key);
        }
    }
    public static Integer getNumber(String s){
        Matcher m = Util.NUMBER_PATTERN.matcher(s);
        if (m.find())
        {
            return Integer.parseInt(m.group(1));
        }
        return null;
    }

    public static List<Object> mergeBonuses(Collection<?> objects) {
        List<Object> merged = new ArrayList<>();
        Map<ProvidedItem, List<ProvidedItem>> mergeableCategories = new HashMap<>();
        for(Object o : objects){
                if(o instanceof ProvidedItem){
                    ProvidedItem providedItem = (ProvidedItem)o;
                    if(providedItem.getPrerequisite() != null) {
                        mergeableCategories.computeIfAbsent(ProvidedItem.create(providedItem.getName(), providedItem.getType()), cat -> new ArrayList<>()).add(providedItem);
                        continue;
                    }
                }

            merged.add(o);


        }

        for(Map.Entry<ProvidedItem,List<ProvidedItem>> mergeableCategoryEntry : mergeableCategories.entrySet()){
            final List<ProvidedItem> categories = mergeableCategoryEntry.getValue();
            if(categories.size() == 1){
                merged.add(categories.get(0));
            } else {
                List<Prerequisite> prerequisites = categories.stream().map(ProvidedItem::getPrerequisite).collect(Collectors.toList());
                merged.add(ProvidedItem.create(mergeableCategoryEntry.getKey().getName(),
                        mergeableCategoryEntry.getKey().getType(),
                        mergePrerequisites(prerequisites)));
            }
        }

        return merged;
    }

    private static Prerequisite mergePrerequisites(List<Prerequisite> prerequisites) {
        prerequisites = unwrapOrs(prerequisites);
        prerequisites = mergeAnds(prerequisites);

        return new OrPrerequisite(prerequisites);
    }

    private static List<Prerequisite> mergeAnds(List<Prerequisite> prerequisites) {

        Multimap<Prerequisite, Prerequisite> parentByChild =HashMultimap.create();

        for(Prerequisite p : prerequisites){

        }
        return prerequisites;
    }

    private static List<Prerequisite> unwrapOrs(List<Prerequisite> prerequisites) {
        Set<Prerequisite> response = Sets.newHashSet();

        for(Prerequisite p : prerequisites){
            if(p instanceof OrPrerequisite && ((OrPrerequisite)p).getCount() == 1){
                response.addAll(unwrapOrs(((OrPrerequisite)p).getChildren()));
            } else {
                response.add(p);
            }
        }
        return Lists.newArrayList(response);
    }

    public static List<String> getCategoryLinks(Element content)
    {
        Elements categories = content.select("li.category");

        return categories.stream().map(el -> el.select("a").first().attr("href")).collect(Collectors.toList());
    }

    public static <L extends Copyable> List<L> cloneList(List<L> list){
        List<L> newList = new LinkedList<>();
        if(list != null) {
            for (L el : list) {
                newList.add((L) el.copy());
            }
        }
        return newList;
}

    public static String toCamelCase(String text) {
        String[] words = text.split("[\\W_]+");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (i == 0) {
                word = word.isEmpty() ? word : word.toLowerCase();
            } else {
                word = word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
            }
            builder.append(word);
        }
        return builder.toString();
    }

    public static String toEnumCase(String text) {
        List<String> words = Arrays.asList(text.toUpperCase().split("[\\W_]+"));

        return String.join("_", words);
    }

    public static String toTitleCase(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder converted = new StringBuilder();

        boolean convertNext = true;
        for (char ch : text.toCharArray()) {
            if (Character.isSpaceChar(ch) || ch == '-' || ch == '\'') {
                convertNext = true;
            } else if (convertNext) {
                ch = Character.toTitleCase(ch);
                convertNext = false;
            } else {
                ch = Character.toLowerCase(ch);
            }
            converted.append(ch);
        }

        return converted.toString();
    }
}
