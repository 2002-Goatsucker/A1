import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MovieAnalyzer {
    List<String[]> list = new ArrayList<>();


    /**
     * description:
     * @Param: [dataset_path]
     * @Return:
     */
    public MovieAnalyzer(String dataset_path) {
        BufferedReader reader;
        try {
            FileReader reader1 = new FileReader(dataset_path, StandardCharsets.UTF_8);
            reader = new BufferedReader(reader1);
            String info;
            reader.readLine();
            while ((info = reader.readLine()) != null) {
                String[] mess = getArr(info);
                list.add(mess);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * description:
     * @Param: [str]
     * @Return: java.lang.String[]
     */
    public static String[] getArr(String str) {
        StringBuilder temp = new StringBuilder();
        String[] ans = new String[16];
        int k = 0;
        boolean isText = false;
        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) == '"') isText = !isText;
            if (str.charAt(i) != ',') temp.append(str.charAt(i));
            else if (isText) temp.append(str.charAt(i));
            else {
                ans[k++] = temp.toString().replaceAll("^\"|\"$", "");
                temp = new StringBuilder();
            }
        }
        ans[k] = temp.toString().replaceAll("^\"|\"$", "");
        return ans;
    }

    /**
     * description:
     * @Param: []
     * @Return: java.util.Map<java.lang.Integer, java.lang.Integer>
     */
    public Map<Integer, Integer> getMovieCountByYear() {
        Map<String, Long> map = list.stream().collect(Collectors.groupingBy(x -> x[2], Collectors.counting()));
        List<Map.Entry<String, Long>> temp = new ArrayList<>(map.entrySet());
        temp.sort((o1, o2) -> Integer.parseInt(o2.getKey()) - Integer.parseInt(o1.getKey()));

        Map<Integer, Integer> ans = new LinkedHashMap<>();
        temp.forEach(x -> ans.put(Integer.parseInt(x.getKey()), x.getValue().intValue()));
        return ans;
    }


    /**
     * description:
     * @Param: []
     * @Return: java.util.Map<java.lang.String, java.lang.Integer>
     */
    public Map<String, Integer> getMovieCountByGenre() {
        Map<String, Integer> ans = new LinkedHashMap<>();
        list.forEach(x -> {
            String[] str;
            if (x[5].contains(",")) str = x[5].split(", ");
            else str = new String[]{x[5]};
            Arrays.stream(str).forEach(k -> {
                if (!ans.containsKey(k)) ans.put(k, 1);
                else ans.put(k, ans.get(k) + 1);
            });
        });
        Map<String, Integer> ansOrdered = new LinkedHashMap<>();
        List<Map.Entry<String, Integer>> list1 = new ArrayList<>(ans.entrySet());
        list1.sort((o1, o2) -> {
            if (!o1.getValue().equals(o2.getValue())) return o2.getValue() - o1.getValue();
            else return compare(o1.getKey(), o2.getKey());
        });
        list1.forEach(x -> ansOrdered.put(x.getKey(), x.getValue()));
        return ansOrdered;
    }

//    public static void main(String[] args) {
//        MovieAnalyzer analyzer = new MovieAnalyzer("C:\\Users\\徐璟源\\Desktop\\学习文件\\南科大课程\\A1_Sample\\resources\\imdb_top_500.csv");
//        analyzer.list.forEach(x->{
//            if(x[1].equals("Indiana Jones and the Last Crusade")) System.out.println(x[7].replaceAll("^\"|\"$", ""));
//            if(x[1].equals("Metropolis")) System.out.println(x[7].replaceAll("^\"|\"$", "") + "M");
//        });
//    }
    /**
     * description:
     * @Param: []
     * @Return: java.util.Map<java.util.List < java.lang.String>,java.lang.Integer>
     */
    public Map<List<String>, Integer> getCoStarCount() {
        Map<List<String>, Integer> map = new LinkedHashMap<>();
        list.forEach(x -> add(new String[]{x[10], x[11], x[12], x[13]}, map));
        List<Map.Entry<List<String>, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue() - o1.getValue());
        Map<List<String>, Integer> ans = new LinkedHashMap<>();
        list.forEach(k -> ans.put(k.getKey(), k.getValue()));
        return ans;
    }

    /**
     * description:
     * @Param: [stars, map]
     * @Return: void
     */
    public void add(String[] stars, Map<List<String>, Integer> map) {
        for (int i = 0; i < stars.length; ++i) {
            for (int j = i + 1; j < stars.length; ++j) {
                List<String> list = new ArrayList<>();
                list.add(String.valueOf(stars[i]));
                list.add(String.valueOf(stars[j]));
                Collections.sort(list);
                if (map.containsKey(list)) {
                    map.put(list, map.get(list) + 1);
                } else map.put(list, 1);
            }
        }
    }

    /**
     * description:
     * @Param: [top_k, by]
     * @Return: java.util.List<java.lang.String>
     */
    public List<String> getTopMovies(int top_k, String by) {
        Comparator<String[]> comparator = null;
        if (by.equals("runtime"))
            comparator = Comparator.comparing((String[] x) -> Integer.parseInt(x[4].split(" ")[0])).reversed().thenComparing(x -> x[1]);
//                (o1, o2) -> {
//            if (o1[4].equals(o2[4])) return compare(o1[1], o2[1]);
//            else return Integer.parseInt(o2[4].split(" ")[0]) - Integer.parseInt(o1[4].split(" ")[0]);
//        };
        if (by.equals("overview"))
            comparator = Comparator.comparing((String[] x) -> x[7].length()).reversed().thenComparing(x -> x[1]);
//            comparator = (o1, o2) -> {
//            if (o1[7].length() != o2[7].length()) {
//                return o2[7].length() - o1[7].length();
//            }
//            return compare(o1[1], o2[1]);
//        };

//        list.stream().sorted(comparator).forEach(x -> System.out.println(x[1]+" "+x[7].length()));

        return list.stream()
                .sorted(comparator)
                .map(x -> x[1])
                .limit(top_k)
                .toList();
    }

    /**
     * description:
     * @Param: [top_k, by]
     * @Return: java.util.List<java.lang.String>
     */
    public List<String> getTopStars(int top_k, String by) {
        System.out.println();
        HashMap<String, Pair> map = new HashMap<>();
        list.forEach(x -> {
            if (!map.containsKey(x[10])) map.put(x[10], new Pair());
            if (!map.containsKey(x[11])) map.put(x[11], new Pair());
            if (!map.containsKey(x[12])) map.put(x[12], new Pair());
            if (!map.containsKey(x[13])) map.put(x[13], new Pair());
        });
        if (by.equals("rating")) {
            list.forEach(x -> {
                map.get(x[10]).sum += Float.parseFloat(x[6]);
                map.get(x[11]).sum += Float.parseFloat(x[6]);
                map.get(x[12]).sum += Float.parseFloat(x[6]);
                map.get(x[13]).sum += Float.parseFloat(x[6]);
                map.get(x[10]).num++;
                map.get(x[11]).num++;
                map.get(x[12]).num++;
                map.get(x[13]).num++;
            });
        }
        if (by.equals("gross")) {
            list.forEach(new Consumer<String[]>() {
                @Override
                public void accept(String[] x) {
                    double num;
                    if (x[15].equals("")) return;
                    else num = Double.parseDouble(x[15].replace(",", ""));
                    map.get(x[10]).sum += num;
                    map.get(x[11]).sum += num;
                    map.get(x[12]).sum += num;
                    map.get(x[13]).sum += num;
                    map.get(x[10]).num++;
                    map.get(x[11]).num++;
                    map.get(x[12]).num++;
                    map.get(x[13]).num++;
                    if (x[15].equals("")) {
                        map.get(x[10]).num--;
                        map.get(x[11]).num--;
                        map.get(x[12]).num--;
                        map.get(x[13]).num--;
                    }
                }
            });
        }
        List<Map.Entry<String, Pair>> list1 = new ArrayList<>(map.entrySet());
        list1 = list1.stream().sorted(Comparator.comparingDouble((Map.Entry<String, Pair> x) -> x.getValue().getAverage()).reversed().thenComparing(Map.Entry::getKey)).toList();
        List<String> ans = new ArrayList<>();
        list1.forEach(x -> ans.add(x.getKey()));
        return ans.stream().limit(top_k).toList();
    }

    /**
     * description:
     * @Param: [o1, o2]
     * @Return: int
     */
    public int compare(String o1, String o2) {
        char[] chars1 = o1.toCharArray();
        char[] chars2 = o2.toCharArray();
        int i = 0;
        while (i < chars1.length && i < chars2.length) {
            if (chars1[i] > chars2[i]) {
                return 1;
            } else if (chars1[i] < chars2[i]) {
                return -1;
            } else {
                i++;
            }
        }
        if (i == chars1.length) {  //o1到头
            return -1;
        }
        if (i == chars2.length) { //o2到头
            return 1;
        }
        return 0;
    }

//    public static void main(String[] args) {
//        MovieAnalyzer analyzer = new MovieAnalyzer("C:\\Users\\徐璟源\\Desktop\\学习文件\\南科大课程\\A1_Sample\\resources\\imdb_top_500.csv");
////        analyzer.getTopStars(10,"gross").forEach(System.out::println);
//    }

    /**
     * description:
     * @Param: [genre, min_rating, max_runtime]
     * @Return: java.util.List<java.lang.String>
     */
    public List<String> searchMovies(String genre, float min_rating, int max_runtime) {
        List<String[]> list1 = list.stream().filter(x -> x[5].contains(genre) && Float.parseFloat(x[6]) >= min_rating && Float.parseFloat(x[4].split(" ")[0]) <= max_runtime).toList();
        List<String> ans = new ArrayList<>(list1.size());
        list1.forEach(x -> ans.add(x[1]));
        Collections.sort(ans);
        return ans;
    }
}

class Pair {
    public double sum = 0;
    public double num = 0;


    public double getAverage() {
        if (num == 0) {
            return 0;
        }
        return sum / num;
    }


}