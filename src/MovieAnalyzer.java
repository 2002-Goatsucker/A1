import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class MovieAnalyzer {
    List<String[]> list = new ArrayList<>();

    public MovieAnalyzer(String dataset_path) {
        BufferedWriter writer;
        BufferedReader reader;
        try {
            FileReader reader1 = new FileReader(dataset_path);
            FileWriter writer1 = new FileWriter(dataset_path, true);
            writer = new BufferedWriter(writer1);
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

    public String[] getArr(String str) {
        StringBuilder temp = new StringBuilder();
        String[] ans = new String[15];
        int k = 0;
        boolean isText = false;
        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) == '"') isText = !isText;
            if (str.charAt(i) != ',') temp.append(str.charAt(i));
            else if (isText) temp.append(str.charAt(i));
            else {
                ans[k++] = temp.toString();
                temp = new StringBuilder();
            }
        }
        return ans;
    }

    public Map<Integer, Integer> getMovieCountByYear() {
        Map<String, Long> map = list.stream().collect(Collectors.groupingBy((String[] x) -> x[2], Collectors.counting()));
        List<Map.Entry<String, Long>> temp = new ArrayList<>(map.entrySet());
        temp.sort((o1, o2) -> Integer.parseInt(o2.getKey()) - Integer.parseInt(o1.getKey()));

        Map<Integer, Integer> ans = new LinkedHashMap<>();
        temp.forEach(x -> ans.put(Integer.parseInt(x.getKey()), x.getValue().intValue()));
        return ans;
    }


    public Map<String, Integer> getMovieCountByGenre() {
        Map<String, Long> map = list.stream().collect(Collectors.groupingBy(x -> x[5], Collectors.counting()));
        List<Map.Entry<String, Long>> temp = new ArrayList<>(map.entrySet());
        temp.sort((o1, o2) -> Integer.parseInt(o2.getKey()) - Integer.parseInt(o1.getKey()));

        Map<String, Integer> ans = new LinkedHashMap<>();
        temp.forEach(x -> ans.put(x.getKey(), x.getValue().intValue()));
        return ans;
    }

    public Map<List<String>, Integer> getCoStarCount() {
        Map<List<String>, Integer> map = new LinkedHashMap<>();
        list.forEach(x -> add(new String[]{x[9], x[10], x[11], x[12]}, map));
        List<Map.Entry<List<String>, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue() - o1.getValue());
        Map<List<String>, Integer> ans = new LinkedHashMap<>();
        list.forEach(k -> ans.put(k.getKey(), k.getValue()));
        return ans;
    }

    public void add(String[] stars, Map<List<String>, Integer> map) {
        Object[] obj=Arrays.stream(stars).distinct().toArray();

        for (int i = 0; i < obj.length; ++i) {
            for (int j = i + 1; j < obj.length; ++j) {
                List<String> list = new ArrayList<>();
                list.add(String.valueOf(obj[i]));
                list.add(String.valueOf(obj[j]));
                Collections.sort(list);
                if (map.containsKey(list)) {
                    map.put(list, map.get(list) + 1);
                } else map.put(list, 1);
            }
        }
    }

    public List<String> getTopMovies(int top_k, String by){
        Comparator<String[]> comparator = null;
        if(by.equals("runtime")) comparator=Comparator.comparing((String[] x)->Integer.parseInt(x[4].split(" ")[0])).reversed();
        if(by.equals("overview")) comparator=Comparator.comparing((String[] x)->x[7].length()).reversed();


        return list.stream()
                .sorted(comparator)
                .limit(top_k)
                .map(x->x[1])
                .toList();
    }

    public List<String> getTopStars(int top_k, String by){
        HashMap<String,Pair> map=new HashMap<>();
        list.forEach(x->{
            if(!map.containsKey(x[9])) map.put(x[9],new Pair(Integer.parseInt(x[6])));
            if(!map.containsKey(x[10])) map.put(x[9],new Pair(Integer.parseInt(x[6])));
            if(!map.containsKey(x[11])) map.put(x[9],new Pair(Integer.parseInt(x[6])));
            if(!map.containsKey(x[12])) map.put(x[9],new Pair(Integer.parseInt(x[6])));

        });
//        if(by.equals("rating")){
//
//        }
//        if(by.equals("gross")){
//
//        }
        return null;
    }

    public static void main(String[] args) {
        MovieAnalyzer analyzer = new MovieAnalyzer("C:\\Users\\徐璟源\\Desktop\\学习文件\\南科大课程\\A1_Sample\\resources\\imdb_top_500.csv");
        analyzer.getTopStars(3,"gross").forEach(System.out::println);
    }


}

class Pair{
    int sum;
    int num;
    public Pair(int sum){
        this.sum=sum;
        num=1;
    }
}