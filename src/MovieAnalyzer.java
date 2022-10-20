import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class MovieAnalyzer {
    List<String[]> list = new ArrayList<>();

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
            if(!map.containsKey(x[9])) map.put(x[9],new Pair());
            if(!map.containsKey(x[10])) map.put(x[10],new Pair());
            if(!map.containsKey(x[11])) map.put(x[11],new Pair());
            if(!map.containsKey(x[12])) map.put(x[12],new Pair());
        });
        if(by.equals("rating")){
            list.forEach(x->{
                map.get(x[9]).sum+=Integer.parseInt(x[6]);
                map.get(x[10]).sum+=Integer.parseInt(x[6]);
                map.get(x[11]).sum+=Integer.parseInt(x[6]);
                map.get(x[12]).sum+=Integer.parseInt(x[6]);
                map.get(x[9]).num++;
                map.get(x[10]).num++;
                map.get(x[11]).num++;
                map.get(x[12]).num++;
            });
        }
        if(by.equals("gross")){
            list.forEach(x->{
                map.get(x[9]).sum+=Integer.parseInt(x[14]);
                map.get(x[10]).sum+=Integer.parseInt(x[14]);
                map.get(x[11]).sum+=Integer.parseInt(x[14]);
                map.get(x[12]).sum+=Integer.parseInt(x[14]);
                map.get(x[9]).num++;
                map.get(x[10]).num++;
                map.get(x[11]).num++;
                map.get(x[12]).num++;
            });
        }
        List<Map.Entry<String, Pair>> list1=new ArrayList<>(map.entrySet());
        list1.sort((o1, o2) -> {
            if(o1.getValue().getAverage()!=o2.getValue().getAverage()) return o2.getValue().getAverage()-o1.getValue().getAverage();
            else return compare(o1.getKey(),o2.getKey());
        });
        List<String> ans=new ArrayList<>();
        list1.forEach(x->ans.add(x.getKey()));
        return ans.stream().limit(top_k).toList();
    }

    public int compare(String o1, String o2) {
        char[] chars1=o1.toCharArray();
        char[] chars2=o2.toCharArray();
        int i=0;
        while(i<chars1.length && i<chars2.length){
            if(chars1[i]>chars2[i]){
                return 1;
            }else if(chars1[i]<chars2[i]){
                return -1;
            }else{
                i++;
            }
        }
        if(i==chars1.length){  //o1到头
            return -1;
        }
        if(i== chars2.length){ //o2到头
            return 1;
        }
        return 0;
    }

    public static void main(String[] args) {
        MovieAnalyzer analyzer = new MovieAnalyzer("C:\\Users\\徐璟源\\Desktop\\学习文件\\南科大课程\\A1_Sample\\resources\\imdb_top_500.csv");
//        analyzer.getTopStars(10,"gross").forEach(System.out::println);
    }

    public List<String> searchMovies(String genre, float min_rating, int max_runtime){
        List<String[]> list1 = list.stream().filter(x->x[5].equals(genre)&&Integer.parseInt(x[6])>=min_rating&&Integer.parseInt(x[4].split(" ")[0])<=max_runtime).toList();
        List<String> ans=new ArrayList<>(list1.size());
        list1.forEach(x->ans.add(x[1]));
        Collections.sort(ans);
        return ans;
    }

}

class Pair{
    public int sum=0;
    public int num=0;

    public int getAverage(){
        return sum/num;
    }
}